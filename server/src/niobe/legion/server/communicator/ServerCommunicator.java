/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ServerCommunicator.java) is part of Niobe Legion (module niobe-legion-server_main).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.server.communicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.persistence.NamedQueries;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import niobe.legion.server.database.LegionDatabase;
import niobe.legion.server.Server;
import niobe.legion.server.communicator.sasl.LegionSaslServerProvider;
import niobe.legion.shared.Base64;
import niobe.legion.shared.communicator.Communicator;
import niobe.legion.shared.communication.XmlCommunication;
import niobe.legion.shared.data.IRight;
import niobe.legion.shared.data.LegionRight;
import niobe.legion.shared.data.Stanza;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.GroupEntity;
import niobe.legion.shared.model.GroupRightEntity;
import niobe.legion.shared.model.IEntity;
import niobe.legion.shared.model.UserEntity;
import niobe.legion.shared.model.marshal.StanzaMarshaller;

public class ServerCommunicator extends Communicator
{
    public final static String SERVER_NAME = "legion_server";
    public final static String SERVER_VERSION = "1.0";
    private final static List<String> SERVER_FEATURES = new ArrayList<String>();

    final String keyStoreFile;
    final String keyStorePassword;
    final String[] cipherSuites;
    final String[] authMechanisms;

    SaslServer saslServer;
    UserEntity user;
    String blacklistedClientsRegex;

    static
    {
        Security.addProvider(new LegionSaslServerProvider());
    }

    final CallbackHandler saslServerHandler = (Callback[] callbacks) -> {
        NameCallback ncb = null;
        PasswordCallback pcb = null;
        RealmCallback rcb = null;
        AuthorizeCallback acb = null;

        for(Callback callback : callbacks)
        {
            if(callback instanceof NameCallback)
            {
                ncb = (NameCallback) callback;
            } else if(callback instanceof PasswordCallback)
            {
                pcb = (PasswordCallback) callback;
            } else if(callback instanceof RealmCallback)
            {
                rcb = (RealmCallback) callback;
            } else if(callback instanceof AuthorizeCallback)
            {
                acb = (AuthorizeCallback) callback;
            }
        }

        if(rcb != null)
        {
            rcb.setText(ServerCommunicator.SERVER_NAME + "_" + ServerCommunicator.SERVER_VERSION);
        }

        if((ncb != null && pcb != null) || acb != null)
        {
            UserEntity userEntity = Server.getDatabase()
                    .getUser(ncb != null ? ncb.getDefaultName() : (acb != null ? acb.getAuthenticationID() : null));
            String password = userEntity != null ? userEntity.getPassword() : null;

            if(checkRight(LegionRight.LOGIN, userEntity))
            {
                if(pcb != null)
                {
                    pcb.setPassword(password != null && !password.isEmpty() ? password.toCharArray() : null);
                }
                if(acb != null)
                {
                    acb.setAuthorized(true);
                    acb.setAuthorizedID(acb.getAuthenticationID());
                }
            } else
            {
                if(ncb != null)
                {
                    ncb.setName(null);
                }
                if(pcb != null)
                {
                    pcb.setPassword(null);
                }
                if(acb != null)
                {
                    acb.setAuthorized(false);
                }
            }
        }
    };

    private boolean serverAcceptedFromClient;
    private boolean tlsEstablished;
    private boolean compressionActive;
    private String clientName;
    private String clientVersion;
    private List<String> clientFeatures = new ArrayList<String>();
    private List<String> clientAuthMechanisms = new ArrayList<String>();

    public ServerCommunicator(Socket socket, String authMechanisms, String blacklistedClientsRegex,
                              final String keyStoreFile, final String keyStorePassword, final String[] cipherSuites,
                              List<String> additionalFeatures)
    {
        super(socket, new XmlCommunication());

        this.authMechanisms = authMechanisms.split(" ");
        this.blacklistedClientsRegex = blacklistedClientsRegex;

        this.keyStoreFile = keyStoreFile;
        this.keyStorePassword = keyStorePassword;
        this.cipherSuites = cipherSuites;

        if(this.keyStoreFile != null && !this.keyStoreFile.isEmpty() && new File(this.keyStoreFile).exists())
        {
            if(!SERVER_FEATURES.contains("starttls"))
            {
                ServerCommunicator.SERVER_FEATURES.add("starttls");
            }
        }
        if(additionalFeatures != null)
        {
            additionalFeatures.stream()
                    .filter(feature -> !"starttls".equals(feature) && !ServerCommunicator.SERVER_FEATURES
                            .contains(feature)).forEach(ServerCommunicator.SERVER_FEATURES::add);
        }
    }

    private void sendServer() throws IOException
    {
        Stanza stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setName("legion:server");
        stanza.setSequenceId(this.localStanzaSequenceId++);
        stanza.putAttribute("name", ServerCommunicator.SERVER_NAME);
        stanza.putAttribute("version", ServerCommunicator.SERVER_VERSION);
        this.write(stanza);

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setName("legion:features");
        this.write(stanza);

        for(String feature : ServerCommunicator.SERVER_FEATURES)
        {
            stanza = new Stanza();
            stanza.setEventType(XMLStreamConstants.CHARACTERS);
            stanza.setName("legion:feature");
            stanza.setValue(feature);
            this.write(stanza);
        }

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
        stanza.setName("legion:features");
        this.write(stanza);

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setName("legion:mechanisms");
        this.write(stanza);

        for(String authMechanism : this.authMechanisms)
        {
            if("PLAIN".equalsIgnoreCase(authMechanism) && !ServerCommunicator.SERVER_FEATURES.contains("starttls"))
            {
                continue;
            }

            stanza = new Stanza();
            stanza.setEventType(XMLStreamConstants.CHARACTERS);
            stanza.setName("legion:mechanism");
            stanza.setValue(authMechanism);
            this.write(stanza);
        }

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
        stanza.setName("legion:mechanisms");
        this.write(stanza);

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
        stanza.setName("legion:server");
        this.write(stanza);
    }

    @Override
    public final void consumeStartElement(final Stanza currentStanza) throws IOException
    {
        Stanza stanza;

        String stanzaName = currentStanza.getName();

        switch(stanzaName)
        {
            case "legion:stream":
                stanza = new Stanza();
                stanza.setEventType(XMLStreamConstants.START_ELEMENT);
                stanza.setName("legion:stream");
                stanza.setSequenceId(this.localStanzaSequenceId++);
                stanza.putAttribute("xmlns:" + this.getNamespace(), "'" + this.getNamespaceURI() + "'");
                this.write(stanza);
                break;
            case "legion:client":
                this.clientName = currentStanza.getAttribute("name");
                this.clientVersion = currentStanza.getAttribute("version");

                break;
            case "legion:starttls":
                if(this.serverAcceptedFromClient && ServerCommunicator.SERVER_FEATURES.contains("starttls") &&
                        this.clientFeatures.contains("starttls"))
                {
                    try
                    {
                        if(!this.tlsEstablished && this.keyStorePassword != null && this.keyStoreFile != null &&
                                !this.keyStoreFile.isEmpty() && new File(this.keyStoreFile).exists())
                        {
                            if(this.keyStorePassword.isEmpty())
                            {
                                System.out.println("WARNING: Your key store password is empty!!!");
                            }
                            stanza = new Stanza();
                            stanza.setEventType(XMLStreamConstants.START_ELEMENT);
                            stanza.setEmptyElement(true);
                            stanza.setName("legion:proceedtls");
                            stanza.setSequenceId(this.localStanzaSequenceId++);
                            stanza.setEmptyElement(true);
                            this.write(stanza);
                            this.tlsEstablished = this
                                    .setSslSocket(this.keyStorePassword, this.keyStoreFile, this.cipherSuites);
                            if(this.tlsEstablished)
                            {
                                this.resetReader();
                            } else
                            {
                                this.decline("starttls", "server don't have a certificate");
                            }
                        } else
                        {
                            if(this.tlsEstablished)
                            {
                                this.decline("starttls", "tls is allready established");
                            } else
                            {
                                this.decline("starttls", "there is no valid server certificate selected");
                            }
                        }
                    } catch(Exception e)
                    {
                        Logger.exception(LegionLogger.STDERR, e);
                        this.decline("starttls", "there is no valid server certificate selected");
                    }
                }
                break;
            case "legion:startcompression":
                if(this.serverAcceptedFromClient && !compressionActive)
                {
                    String algorithm = currentStanza.getAttribute("algorithm");
                    if(algorithm != null)
                    {
                        switch(algorithm)
                        {
                            case "gzip":
                            case "xz":
                                if(ServerCommunicator.SERVER_FEATURES
                                        .contains("compressed_stream_" + algorithm) && this.clientFeatures
                                        .contains("compressed_stream_" + algorithm))
                                {
                                    stanza = new Stanza();
                                    stanza.setEventType(XMLStreamConstants.START_ELEMENT);
                                    stanza.setEmptyElement(true);
                                    stanza.setName("legion:proceedcompression");
                                    stanza.setSequenceId(this.localStanzaSequenceId++);
                                    stanza.putAttribute("algorithm", algorithm);
                                    this.write(stanza);
                                    try
                                    {
                                        this.compressionActive = this
                                                .replaceStreamsWithCompressedStreams(false, algorithm);
                                        if(this.compressionActive)
                                        {
                                            this.resetReader();
                                        } else
                                        {
                                            this.decline("startcompression", "can not start compression");
                                        }
                                    } catch(Exception e)
                                    {
                                        Logger.exception(LegionLogger.STDERR, e);
                                        this.decline("starttls", "can not start compression");
                                    }
                                } else
                                {
                                    this.decline("startcompression",
                                                 "compression feature \"compressed_stream_" + algorithm + "\" is not enabled");
                                }
                                break;
                            default:
                                this.decline("startcompression", "unknown compression algorithm \"" + algorithm + "\"");
                                break;
                        }
                    } else
                    {
                        this.decline("startcompression", "no compression algorithm selected");
                    }
                }
            case "legion:auth":
                if(this.serverAcceptedFromClient)
                {
                    if(currentStanza.containsAttributeKey("mechanism"))
                    {
                        String mechanism = currentStanza.getAttribute("mechanism");

                        if(Arrays.asList(this.authMechanisms).contains(mechanism) || (Arrays.asList(this.authMechanisms)
                                .contains(mechanism) &&
                                "PLAIN".equalsIgnoreCase(mechanism) &&
                                ServerCommunicator.SERVER_FEATURES.contains("starttls")))
                        {

                            byte[] initialResponse = new byte[0];
                            if(currentStanza.containsAttributeKey("initialResponse"))
                            {
                                String initialResponseAsString = currentStanza.getAttribute("initialResponse");
                                if(initialResponseAsString != null && !initialResponseAsString.isEmpty())
                                {
                                    initialResponse = Base64.decode(initialResponseAsString);
                                }
                            }

                            this.saslServer = Sasl
                                    .createSaslServer(mechanism,
                                                      "legion",
                                                      ServerCommunicator.SERVER_NAME + "_" +
                                                              ServerCommunicator.SERVER_VERSION,
                                                      new HashMap<String, Object>(),
                                                      this.saslServerHandler);

                            if(this.saslServer != null)
                            {
                                try
                                {
                                    byte[] challenge = this.saslServer.evaluateResponse(initialResponse);

                                    if(!this.saslServer.isComplete())
                                    {
                                        stanza = new Stanza();
                                        stanza.setName("legion:challenge");
                                        stanza.setSequenceId(this.localStanzaSequenceId++);
                                        stanza.setEventType(XMLStreamConstants.CHARACTERS);
                                        stanza.setValue(Base64.encodeBytes(challenge));
                                        this.write(stanza);
                                    } else
                                    {
                                        stanza = new Stanza();
                                        stanza.setName("legion:success");
                                        stanza.setSequenceId(this.localStanzaSequenceId++);
                                        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
                                        this.write(stanza);

                                        this.user = Server.getDatabase().getUser(this.saslServer.getAuthorizationID());
                                        this.sendUserGroup();

                                        stanza = new Stanza();
                                        stanza.setName("legion:success");
                                        stanza.setSequenceId(this.localStanzaSequenceId++);
                                        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
                                        this.write(stanza);
                                    }
                                } catch(SaslException e)
                                {
                                    stanza = new Stanza();
                                    stanza.setName("legion:failure");
                                    stanza.setSequenceId(this.localStanzaSequenceId++);
                                    stanza.setEventType(XMLStreamConstants.CHARACTERS);
                                    stanza.setValue(e.getMessage());
                                    this.write(stanza);
                                }
                            } else
                            {
                                stanza = new Stanza();
                                stanza.setName("legion:failure");
                                stanza.setSequenceId(this.localStanzaSequenceId++);
                                stanza.setEventType(XMLStreamConstants.CHARACTERS);
                                stanza.setValue("Authenfication Mechanism too weak");
                                this.write(stanza);
                            }
                        }
                    }
                }
                break;
            case "legion:query":
                if(this.isAuthenficated())
                {
                    if(("set".equals(currentStanza.getAttribute("action")) || "delete"
                            .equals(currentStanza.getAttribute("action"))) &&
                            currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
                    {
                        this.cachedStanzas.put(Long.parseLong(currentStanza.getSequenceId()), new ArrayList<Stanza>());
                    } else if("get".equals(currentStanza.getAttribute("action")) &&
                            currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
                    {
                        if(currentStanza.getAttribute("class") != null)
                        {
                            try
                            {
                                long id = Long.parseLong(currentStanza.getSequenceId());
                                Class<?> clazz = Class.forName(currentStanza.getAttribute("class"));
                                String where = currentStanza.getAttribute("where");
                                String queryName = currentStanza.getAttribute("namedQuery");

                                Map<String, Object> whereMap = this.createWhereMapFromString(where);

                                this.getDatasets(id, clazz, queryName, whereMap);
                            } catch(ClassNotFoundException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case "legion:dataset":
            case "legion:column":
            case "legion:entry":
                if(this.isAuthenficated())
                {
                    if(currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
                    {
                        long id = Long.parseLong(currentStanza.getSequenceId());

                        if(this.cachedStanzas.containsKey(id))
                        {
                            this.cachedStanzas.get(id).add(new Stanza(currentStanza));
                        }
                    }
                }
        }
    }

    @Override
    public final void consumeCharacters(final Stanza currentStanza) throws IOException
    {
        String stanzaName = currentStanza.getName();

        switch(stanzaName)
        {
            case "legion:feature":
                if(this.isStackAt(1, "legion:features") && this.isStackAt(2, "legion:client"))
                {
                    this.clientFeatures.add(currentStanza.getValue());
                }
                break;
            case "legion:mechanism":
                if(this.isStackAt(1, "legion:mechanisms") && this.isStackAt(2, "legion:client"))
                {
                    this.clientAuthMechanisms.add(currentStanza.getValue());
                }
                break;
            case "legion:dataset":
            case "legion:column":
            case "legion:entry":
                if(this.isAuthenficated())
                {
                    if(currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
                    {
                        List<Stanza> stanzas = this.cachedStanzas.get(Long.parseLong(currentStanza.getSequenceId()));
                        if(stanzas != null)
                        {
                            stanzas.add(currentStanza);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public final void consumeEndElement(final Stanza currentStanza) throws IOException
    {
        String stanzaName = currentStanza.getName();
        Stanza stanza;

        switch(stanzaName)
        {
            case "legion:server":
                if(this.isAcceptAt(1) && currentStanza.getValue() != null)
                {
                    String[] identification = currentStanza.getValue().split(":");
                    if(identification.length == 2 && ServerCommunicator.SERVER_NAME.equals(identification[0]) &&
                            ServerCommunicator.SERVER_VERSION.equals(identification[1]))
                    {
                        this.serverAcceptedFromClient = true;
                    }
                }
                break;
            case "legion:client":
                if(this.clientName == null || this.clientName.isEmpty() || this.clientVersion == null ||
                        this.clientVersion.isEmpty())
                {
                    this.decline("legion:client", "Invalid Client Identification received");
                    return;
                }

                if(this.blacklistedClientsRegex != null && !this.blacklistedClientsRegex.isEmpty())
                {
                    String checkValue = this.clientName + "_" + this.clientVersion;
                    if(checkValue.matches(this.blacklistedClientsRegex))
                    {
                        this.decline("legion:client", "Client is blacklisted on server");
                        return;
                    }
                }

                if(this.authMechanisms != null)
                {
                    if(Stream.of(this.authMechanisms)
                            .noneMatch(authMechanism -> this.clientAuthMechanisms.contains(authMechanism)))
                    {
                        this.decline("legion:server", "Client have no auth mechanisms in common");
                        return;
                    }
                } else
                {
                    this.decline("legion:server", "Client have no auth mechanisms in common");
                    return;
                }

                stanza = new Stanza();
                stanza.setEventType(XMLStreamConstants.CHARACTERS);
                stanza.setName("legion:client");
                stanza.setSequenceId(this.localStanzaSequenceId++);
                stanza.setValue(this.clientName + ":" + this.clientVersion);

                this.accept(stanza);
                this.sendServer();

                break;
            case "legion:response":
                if(this.saslServer != null && !this.saslServer.isComplete())
                {
                    try
                    {
                        stanza = new Stanza();
                        byte[] challenge = this.saslServer.evaluateResponse(Base64.decode(currentStanza.getValue()));

                        stanza.setName("legion:challenge");
                        stanza.setSequenceId(this.localStanzaSequenceId++);
                        stanza.setEventType(XMLStreamConstants.CHARACTERS);
                        stanza.setValue(Base64.encodeBytes(challenge));
                        this.write(stanza);

                        if(this.saslServer.isComplete())
                        {
                            stanza = new Stanza();
                            stanza.setName("legion:success");
                            stanza.setSequenceId(this.localStanzaSequenceId);
                            stanza.setEventType(XMLStreamConstants.START_ELEMENT);
                            this.write(stanza);

                            this.user = Server.getDatabase().getUser(this.saslServer.getAuthorizationID());
                            this.sendUserGroup();

                            stanza = new Stanza();
                            stanza.setName("legion:success");
                            stanza.setSequenceId(this.localStanzaSequenceId++);
                            stanza.setEventType(XMLStreamConstants.END_ELEMENT);
                            this.write(stanza);
                        }
                    } catch(SaslException e)
                    {
                        stanza = new Stanza();
                        stanza.setName("legion:failure");
                        stanza.setSequenceId(this.localStanzaSequenceId++);
                        stanza.setEventType(XMLStreamConstants.CHARACTERS);
                        stanza.setValue(e.getMessage());
                        this.write(stanza);
                    }
                }
                break;
            case "legion:success":
                if(this.saslServer.isComplete())
                {
                    stanza = new Stanza();
                    stanza.setName("legion:success");
                    stanza.setSequenceId(this.localStanzaSequenceId);
                    stanza.setEventType(XMLStreamConstants.START_ELEMENT);
                    this.write(stanza);

                    this.user = Server.getDatabase().getUser(this.saslServer.getAuthorizationID());
                    this.sendUserGroup();

                    stanza = new Stanza();
                    stanza.setName("legion:success");
                    stanza.setSequenceId(this.localStanzaSequenceId++);
                    stanza.setEventType(XMLStreamConstants.END_ELEMENT);
                    this.write(stanza);
                }
                break;
            case "legion:deAuth":
                if(this.isAuthenficated())
                {
                    this.user = null;
                    this.saslServer = null;
                }
                break;
            case "legion:stream":
                if(!this.isCloseRequested)
                {
                    this.isCloseRequested = true;
                    stanza = new Stanza();
                    stanza.setEventType(XMLStreamConstants.END_ELEMENT);
                    stanza.setName("legion:stream");
                    stanza.setSequenceId(this.localStanzaSequenceId++);
                    this.write(stanza);

                    try
                    {
                        Thread.sleep(1000);
                    } catch(InterruptedException e)
                    {
                    }
                }

                this.closeSocket();
                break;
            case "legion:dataset":
            case "legion:column":
            case "legion:entry":
                if(this.isAuthenficated())
                {
                    if(currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
                    {
                        List<Stanza> stanzas = this.cachedStanzas.get(Long.parseLong(currentStanza.getSequenceId()));
                        if(stanzas != null)
                        {
                            stanzas.add(currentStanza);
                        }
                    }
                }
                break;
            case "legion:query":
                if(this.isAuthenficated())
                {
                    final String id = currentStanza.getSequenceId();
                    if(id != null && id.matches("\\d+"))
                    {
                        final List<Stanza> stanzas = this.cachedStanzas.remove(Long.parseLong(id));

                        if(stanzas != null && !stanzas.isEmpty())
                        {
                            if("set".equals(currentStanza.getAttribute("action")))
                            {
                                new Thread("DatabaseSetThread #" + id)
                                {
                                    @Override
                                    public void run()
                                    {
                                        LegionDatabase db = Server.getDatabase();
                                        List<Object> datasetsToSend = new ArrayList<Object>();

                                        StanzaMarshaller.unmarshal(stanzas).stream().filter(dataset -> dataset != null)
                                                .forEach(dataset -> {
                                                    int setId = ((IEntity) dataset).getId();

                                                    if(setId > 0)
                                                    {
                                                        db.update(dataset);
                                                    } else
                                                    {
                                                        dataset = db.insert(dataset);
                                                    }
                                                    datasetsToSend.add(dataset);
                                                });

                                        if(!datasetsToSend.isEmpty())
                                        {
                                            List<Stanza> list = new ArrayList<Stanza>();
                                            Stanza startStanza = new Stanza();
                                            startStanza.setName("legion:query");
                                            startStanza.putAttribute("type", "result");
                                            startStanza.setSequenceId(id);
                                            startStanza.setEventType(XMLStreamConstants.START_ELEMENT);
                                            list.add(startStanza);

                                            for(Object dataset : datasetsToSend)
                                            {
                                                list.addAll(StanzaMarshaller.marshal(dataset, Long.parseLong(id)));
                                            }

                                            Stanza endStanza = new Stanza();
                                            endStanza.setName("legion:query");
                                            endStanza.setEventType(XMLStreamConstants.END_ELEMENT);
                                            list.add(endStanza);

                                            synchronized(ServerCommunicator.this)
                                            {
                                                for(Stanza stanza : list)
                                                {
                                                    try
                                                    {
                                                        ServerCommunicator.this.write(stanza);
                                                    } catch(IOException e)
                                                    {
                                                        Logger.exception(LegionLogger.SEND, e);
                                                    }
                                                }
                                            }
                                        } else
                                        {
                                            Stanza startStanza = new Stanza();
                                            startStanza.setName("legion:query");
                                            startStanza.putAttribute("type", "result");
                                            startStanza.setSequenceId(id);
                                            startStanza.setEventType(XMLStreamConstants.START_ELEMENT);
                                            startStanza.setEmptyElement(true);

                                            synchronized(ServerCommunicator.this)
                                            {
                                                try
                                                {
                                                    ServerCommunicator.this.write(startStanza);
                                                } catch(IOException e)
                                                {
                                                    Logger.exception(LegionLogger.SEND, e);
                                                }
                                            }
                                        }
                                    }
                                }.start();
                            } else if("delete".equals(currentStanza.getAttribute("action")))
                            {
                                StanzaMarshaller.unmarshal(stanzas).stream()
                                        .filter(dataset -> dataset instanceof IEntity)
                                        .forEach(dataset -> this.deleteDataset((IEntity) dataset));
                            }
                        }
                    }
                }
                break;
            case "legion:decline":
                if(currentStanza.getAttribute("type") != null)
                {
                    switch(currentStanza.getAttribute("type"))
                    {
                        case "legion:client":
                            this.close();
                            break;
                    }
                }
                break;
        }
    }

    @Override
    protected final boolean setSslSocket(String keyStorePassword, String keyStoreFile, String[] cipherSuites) throws
                                                                                                              IOException,
                                                                                                              NoSuchAlgorithmException,
                                                                                                              KeyManagementException,
                                                                                                              CertificateException,
                                                                                                              KeyStoreException,
                                                                                                              UnrecoverableKeyException,
                                                                                                              XMLStreamException
    {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

        char[] passphrase = keyStorePassword.toCharArray();
        KeyStore keystore = KeyStore.getInstance("JKS");

        if(new File(keyStoreFile).exists())
        {
            InputStream in = new FileInputStream(keyStoreFile);
            keystore.load(in, passphrase);
            in.close();

            if(keystore.size() == 0)
            {
                return false;
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, passphrase);
            KeyManager[] keyManagers = kmf.getKeyManagers();

            sslContext.init(keyManagers, null, null);
            SSLSocketFactory sslFactory = sslContext.getSocketFactory();
            this.sslSocket = (SSLSocket) sslFactory
                    .createSocket(this.socket, "localhost", this.socket.getLocalPort(), true);
            SSLParameters sslParameters = this.sslSocket.getSSLParameters();
            sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
            this.sslSocket.setSSLParameters(sslParameters);

            if(cipherSuites != null && cipherSuites.length > 0)
            {
                this.sslSocket.setEnabledCipherSuites(cipherSuites);
            }

            this.sslSocket.addHandshakeCompletedListener((HandshakeCompletedEvent event) -> Logger
                    .debug(LegionLogger.TLS, "Used cipherSuite: " + event.getCipherSuite()));
            this.sslSocket.setUseClientMode(false);

            this.replaceStreamsWithSslStreams();

            return true;
        }
        return false;
    }

    public boolean isAuthenficated()
    {
        return (this.saslServer != null && this.saslServer.isComplete());
    }

    private void getDatasets(final long id, final Class datasetType, final String queryName,
                             final Map<String, Object> parameters)
    {
        // asynchron database action - otherwise the
        // connection can
        // not be used until the action is done
        new Thread("DatabaseQueryThread #" + id)
        {
            @Override
            public void run()
            {
                LegionDatabase db = Server.getDatabase();

                List<?> result = null;
                if(queryName != null && !queryName.isEmpty())
                {
                    // Check if entity supports this query
                    Optional<Annotation> annotation = Stream.of(datasetType.getAnnotations())
                            .filter(an -> an instanceof NamedQueries).findFirst();
                    if(annotation.isPresent())
                    {
                        NamedQueries namedQueries = (NamedQueries) annotation.get();

                        if(namedQueries.value() != null && namedQueries.value().length > 0)
                        {
                            if(Stream.of(namedQueries.value()).anyMatch(query -> query.name().equals(queryName)))
                            {
                                // run the query
                                if(parameters != null && !parameters.isEmpty())
                                {
                                    result = db.getResults(queryName, datasetType, parameters);
                                } else
                                {
                                    result = db.getResults(queryName, datasetType);
                                }
                            }

                        }
                    }
                } else
                {
                    result = db.getResults(datasetType);
                }

                if(result != null)
                {
                    List<Stanza> list = new ArrayList<Stanza>();
                    Stanza startStanza = new Stanza();
                    startStanza.setName("legion:query");
                    startStanza.putAttribute("type", "result");
                    startStanza.setSequenceId(id);
                    startStanza.setEventType(XMLStreamConstants.START_ELEMENT);
                    list.add(startStanza);

                    for(Object object : result)
                    {
                        list.addAll(StanzaMarshaller.marshal(object, id));
                    }

                    Stanza endStanza = new Stanza();
                    endStanza.setName("legion:query");
                    endStanza.setEventType(XMLStreamConstants.END_ELEMENT);
                    list.add(endStanza);

                    synchronized(ServerCommunicator.this)
                    {
                        for(Stanza stanza : list)
                        {
                            try
                            {
                                ServerCommunicator.this.write(stanza);
                            } catch(IOException e)
                            {
                                Logger.exception(LegionLogger.SEND, e);
                            }
                        }
                    }
                }
            }

        }.start();
    }

    private void deleteDataset(IEntity object)
    {
        new Thread("DatabaseDeleteThread #" + object.getId())
        {
            @Override
            public void run()
            {
                Server.getDatabase().remove(object);
            }
        }.start();
    }

    public String getUserName()
    {
        if(this.isAuthenficated())
        {
            return this.user.getName();
        }
        return null;
    }

    /**
     * only check group right at server if necessary, for example for administrative tasks.
     * otherwise use client's right check function, avoid unnecessary right poll requests
     * <p>
     * if check failed the server should send decline xml stanza with a "not enough rights" as reason to the client
     */
    public boolean checkRight(IRight right)
    {
        checkRight(right, this.user);
        return false;
    }

    private static boolean checkRight(IRight right, UserEntity userEntity)
    {
        if(userEntity != null)
        {
            GroupEntity groupEntity = userEntity.getGroup();
            if(groupEntity != null && groupEntity.getRights() != null)
            {
                for(GroupRightEntity rightEntity : groupEntity.getRights())
                {
                    if(Communicator.validateRight(rightEntity, right))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void sendUserGroup() throws IOException
    {
        if(this.user.getGroup() != null)
        {
            GroupEntity groupEntity = this.user.getGroup();
            Stanza stanza = new Stanza();
            stanza.setName("legion:group");
            stanza.setSequenceId(this.localStanzaSequenceId);
            stanza.setEventType(XMLStreamConstants.START_ELEMENT);
            stanza.putAttribute("name", groupEntity.getName());
            stanza.putAttribute("active", groupEntity.isActive() ? "true" : "false");
            this.write(stanza);

            groupEntity.getRights().stream().flatMap(ServerCommunicator::flatRights).forEach(groupRightEntity -> {
                Stanza rightStanza = new Stanza();
                rightStanza.setName("legion:groupRight");
                rightStanza.setSequenceId(this.localStanzaSequenceId);
                rightStanza.setEventType(XMLStreamConstants.START_ELEMENT);
                rightStanza.putAttribute("name", groupRightEntity.getName());
                rightStanza.putAttribute("active", Boolean.toString(groupRightEntity.isActive()));
                rightStanza.setEmptyElement(true);
                try
                {
                    this.write(rightStanza);
                } catch(IOException e)
                {
                    e.printStackTrace();
                }
            });

            stanza = new Stanza();
            stanza.setName("legion:group");
            stanza.setSequenceId(this.localStanzaSequenceId);
            stanza.setEventType(XMLStreamConstants.END_ELEMENT);
            this.write(stanza);
        }
    }

    private static Stream<GroupRightEntity> flatRights(GroupRightEntity right)
    {
        if(right.getChildren() != null && !right.getChildren().isEmpty())
        {
            return Stream
                    .concat(Stream.of(right), right.getChildren().stream().flatMap(ServerCommunicator::flatRights));
        } else
        {
            return Stream.of(right);
        }
    }

    @Override
    protected void socketUnexpectedClosed()
    {
        Server.getCommunicators().remove(this);
    }
}
