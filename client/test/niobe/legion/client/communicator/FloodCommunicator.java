/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (FloodCommunicator.java) is part of Niobe Legion (module niobe-legion-client_test).
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

package niobe.legion.client.communicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import niobe.legion.client.Client;
import niobe.legion.client.FloodingClient;
import niobe.legion.client.gui.connect.ConnectController;
import niobe.legion.client.gui.connect.LoginController;
import niobe.legion.shared.communication.XmlCommunication;
import niobe.legion.shared.communicator.Communicator;
import niobe.legion.shared.data.Stanza;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

/**
 * @author fireandfuel
 */
public class FloodCommunicator extends Communicator
{
    private final static List<String> CLIENT_FEATURES = new ArrayList<String>(Arrays.asList("starttls",
                                                                                            "compressed_stream_gzip"));
    private static final String CLIENT_NAME = "legion_test_flood_client";
    private static final String CLIENT_VERSION = "1.0";

    private boolean clientAcceptedFromServer;
    private boolean tlsEstablished;
    private boolean compressionActive;
    private String serverName;
    private String serverVersion;

    private List<String> serverFeatures = new ArrayList<String>();
    private final String keyStoreFile = "client.ks";
    private final String keyStorePassword = "";
    private final String[] cipherSuites = null;

    public FloodCommunicator(Socket socket)
    {
        super(socket, new XmlCommunication());
    }

    @Override
    public void run()
    {
        try
        {
            this.openStream();
            super.run();
        } catch(IOException e)
        {
//            Logger.exception(LegionLogger.STDERR, e);
        }
    }

    private void openStream() throws IOException
    {
        Stanza stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setName("legion:stream");
        stanza.setSequenceId(this.localStanzaSequenceId);
        stanza.putAttribute("xmlns:" + this.getNamespace(), "'" + this.getNamespaceURI() + "'");

        this.write(stanza);
    }

    private void sendClient() throws IOException
    {
        Stanza stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setName("legion:client");
        stanza.setSequenceId(this.localStanzaSequenceId++);
        stanza.putAttribute("name", FloodCommunicator.CLIENT_NAME);
        stanza.putAttribute("version", FloodCommunicator.CLIENT_VERSION);
        this.write(stanza);

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setName("legion:features");
        this.write(stanza);

        for(String feature : FloodCommunicator.CLIENT_FEATURES)
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

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
        stanza.setName("legion:mechanisms");
        this.write(stanza);

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
        stanza.setName("legion:client");
        this.write(stanza);
    }

    @Override
    protected boolean setSslSocket(String keyStorePassword, String keyStoreFile, String[] cipherSuites) throws
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
        } else
        {
            keystore.load(null, passphrase);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(keystore);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];

        ClientTrustManager trustManager = new ClientTrustManager(defaultTrustManager);
        TrustManager[] trustManagers = new TrustManager[]{trustManager};

        sslContext.init(null, trustManagers, null);
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

        this.sslSocket.setUseClientMode(true);
        try
        {
            this.sslSocket.startHandshake();
            this.replaceStreamsWithSslStreams();

            return true;
        } catch(SSLException e)
        {
            Logger.exception(LegionLogger.TLS, e);

            this.close();
            return false;
        }
    }

    @Override
    protected void socketUnexpectedClosed()
    {
        FloodingClient.broken();
    }

    @Override
    public void consumeStartElement(Stanza currentStanza) throws IOException
    {
        String stanzaName = currentStanza.getName();

        switch(stanzaName)
        {
            case "legion:stream":
                if(!this.clientAcceptedFromServer)
                {
                    this.sendClient();
                } else
                {
                    // skip server/client checks
                    proceedConnection();
                }
                break;
            case "legion:server":
                this.serverName = currentStanza.getAttribute("name");
                this.serverVersion = currentStanza.getAttribute("version");
                break;
            case "legion:proceedtls":
                if(this.clientAcceptedFromServer && !this.tlsEstablished && this.keyStorePassword != null &&
                        this.keyStoreFile != null && !this.keyStoreFile.isEmpty())
                {
                    if(this.keyStorePassword.isEmpty())
                    {
                        Logger.warn(LegionLogger.STDOUT, "WARNING: Your key store password is empty!!!");
                    }
                    try
                    {
                        this.tlsEstablished = this
                                .setSslSocket(this.keyStorePassword, this.keyStoreFile, this.cipherSuites);
                        if(this.tlsEstablished)
                        {
                            this.openStream();
                            this.resetReader();
                        } else
                        {
                            this.decline("proceedtls", "client don't trust server certificate");
                        }
                    } catch(Exception e)
                    {
                        Logger.exception(LegionLogger.STDERR, e);
                        if(!this.tlsEstablished)
                        {
                            this.decline("proceedtls", "tls is not established, see client error log");
                        }
                    }
                } else
                {
                    if(this.tlsEstablished)
                    {
                        this.decline("proceedtls", "tls is allready established");
                    } else
                    {
                        this.decline("proceedtls", "there is no valid server certificate selected");
                    }
                }
                break;
            case "legion:proceedcompression":
                if(this.clientAcceptedFromServer && !this.compressionActive)
                {
                    String algorithm = currentStanza.getAttribute("algorithm");
                    if(algorithm != null)
                    {
                        switch(algorithm)
                        {
                            case "gzip":
                            case "xz":
                                if(CLIENT_FEATURES.contains("compressed_stream_" + algorithm) && this.serverFeatures
                                        .contains("compressed_stream_" + algorithm))
                                {
                                    try
                                    {
                                        this.compressionActive = this
                                                .replaceStreamsWithCompressedStreams(true, algorithm);
                                        if(this.compressionActive)
                                        {
                                            this.openStream();
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
                break;
        }
    }

    @Override
    public void consumeCharacters(Stanza currentStanza) throws IOException
    {

    }

    @Override
    public void consumeEndElement(Stanza currentStanza) throws IOException
    {
        String stanzaName = currentStanza.getName();

        switch(stanzaName)
        {
            case "legion:client":
                if(this.isAcceptAt(1) && currentStanza.getValue() != null)
                {
                    String[] identification = currentStanza.getValue().split(":");
                    if(identification.length == 2 && ClientCommunicator.CLIENT_NAME.equals(identification[0]) &&
                            ClientCommunicator.CLIENT_VERSION.equals(identification[1]))
                    {
                        this.clientAcceptedFromServer = true;
                    }
                }
                break;
            case "legion:server":
                if(this.clientAcceptedFromServer)
                {
                    Stanza stanza = new Stanza();
                    stanza.setEventType(XMLStreamConstants.CHARACTERS);
                    stanza.setName("legion:server");
                    stanza.setSequenceId(this.localStanzaSequenceId++);
                    stanza.setValue(this.serverName + ":" + this.serverVersion);
                    this.accept(stanza);

                    proceedConnection();
                }
                break;
            case "legion:stream":
                if(!this.isCloseRequested)
                {
                    this.isCloseRequested = true;
                    Stanza stanza = new Stanza();
                    stanza.setName("legion:stream");
                    stanza.setSequenceId(this.localStanzaSequenceId++);
                    stanza.setEventType(XMLStreamConstants.END_ELEMENT);
                    this.write(stanza);
                }
                this.closeSocket();
                break;
        }
    }

    private void proceedConnection() throws IOException
    {
        Stanza stanza;
        if(CLIENT_FEATURES.contains("starttls") &&
                this.serverFeatures.contains("starttls") && !tlsEstablished)
        {
            stanza = new Stanza();
            stanza.setEmptyElement(true);
            stanza.setEventType(XMLStreamConstants.START_ELEMENT);
            stanza.setName("legion:starttls");
            this.write(stanza);
        } else if(CLIENT_FEATURES.contains("compressed_stream_xz") &&
                this.serverFeatures.contains("compressed_stream_xz") && !compressionActive)
        {
            stanza = new Stanza();
            stanza.setEmptyElement(true);
            stanza.setEventType(XMLStreamConstants.START_ELEMENT);
            stanza.setName("legion:startcompression");
            stanza.putAttribute("algorithm", "xz");
            this.write(stanza);
        } else if(CLIENT_FEATURES.contains("compressed_stream_gzip") &&
                this.serverFeatures.contains("compressed_stream_gzip") && !compressionActive)
        {
            stanza = new Stanza();
            stanza.setEmptyElement(true);
            stanza.setEventType(XMLStreamConstants.START_ELEMENT);
            stanza.setName("legion:startcompression");
            stanza.putAttribute("algorithm", "gzip");
            this.write(stanza);
        } else if(Client.getFxController().getCurrentController() instanceof ConnectController)
        {
            Client.getFxController().loadMask("/niobe/legion/client/fxml/connect/Login.fxml");
        } else if(!(Client.getFxController().getCurrentController() instanceof LoginController))
        {
            Client.showRelogin();
        }
    }

    public void closeConnection()
    {
        try
        {
            this.closeSocket();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}