/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (Communicator.java) is part of Niobe Legion (module niobe-legion-shared).
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
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.net.ssl.SSLSocket;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import niobe.legion.shared.communication.CommunicationException;
import niobe.legion.shared.communication.CompressedCommunication;
import niobe.legion.shared.communication.ICommunication;
import niobe.legion.shared.data.IRight;
import niobe.legion.shared.data.LegionRight;
import niobe.legion.shared.data.Stanza;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.GroupRightEntity;
import niobe.legion.shared.module.ModuleRightManager;

public abstract class Communicator implements XMLStreamConstants, ICommunicator, Runnable
{
    private static final String LEGION_NAMESPACE = "legion";
    private static final String LEGION_NAMESPACE_URI = "https://github.com/fireandfuel/Niobe-Legion";

    public static final String DEBUG_NAMESPACE = "debug";
    public static final String DEBUG_NAMESPACE_URI = "https://github.com/fireandfuel/Niobe-Legion/debug";

    private static ICommunicator DEBUG_COMMUNICATOR;

    private final HashMap<String, ICommunicator> moduleCommunicators = new HashMap<String, ICommunicator>();
    protected final HashMap<Long, List<Stanza>> cachedStanzas = new HashMap<Long, List<Stanza>>();

    protected Socket socket;
    protected SSLSocket sslSocket;

    private ICommunication communication;

    protected long localStanzaSequenceId = Math.abs(Utils.random.nextLong());
    protected boolean isCloseRequested;

    private InputStream in;
    private OutputStream out;

    private LinkedList<Stanza> stanzaStack = new LinkedList<Stanza>();

    private Object pingResponder;

    protected Communicator(Socket socket, ICommunication communication)
    {
        this.socket = socket;
        Logger.debug(LegionLogger.STDOUT, "connected to " + this.getAddress().getHostAddress() + ":" + this.getPort());

        try
        {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            this.communication = communication;
        } catch(IOException e)
        {
            Logger.exception(LegionLogger.STDERR, e);
        }

        ModuleRightManager.addRights(LegionRight.values());
    }

    public void addModuleCommunicator(ICommunicator communicator)
    {

        if(communicator != null)
        {
            if(DEBUG_NAMESPACE_URI.equals(communicator.getNamespaceURI()))
            {
                DEBUG_COMMUNICATOR = communicator;
            } else
            {
                moduleCommunicators.put(communicator.getNamespaceURI(), communicator);
            }
        }
    }

    public void removeModuleCommunicator(String namespaceURI)
    {
        if(namespaceURI != null)
        {
            if(DEBUG_NAMESPACE_URI.equals(namespaceURI))
            {
                DEBUG_COMMUNICATOR = null;
            } else
            {
                moduleCommunicators.remove(namespaceURI);
            }
        }
    }

    /**
     * Sets a TLSv1.2 socket.
     *
     * @param keyStorePassword
     * @param keyStoreFile
     * @param cipherSuites     null -> "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256" will be used
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws XMLStreamException
     * @link docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html#SunJSSEProvider
     */
    protected abstract boolean setSslSocket(String keyStorePassword, String keyStoreFile, String[] cipherSuites) throws
                                                                                                                 IOException,
                                                                                                                 NoSuchAlgorithmException,
                                                                                                                 KeyManagementException,
                                                                                                                 CertificateException,
                                                                                                                 KeyStoreException,
                                                                                                                 UnrecoverableKeyException,
                                                                                                                 XMLStreamException;

    protected final void replaceStreamsWithSslStreams() throws IOException
    {
        this.in = new DataInputStream(this.sslSocket.getInputStream());
        this.out = new DataOutputStream(this.sslSocket.getOutputStream());
    }

    protected final boolean replaceStreamsWithCompressedStreams(boolean clientMode, String algorithm) throws IOException
    {
        if(algorithm != null)
        {
            this.communication = new CompressedCommunication(this.communication,
                                                             (!clientMode ? this.in : null),
                                                             (clientMode ? this.out : null),
                                                             algorithm);
            return true;
        }
        return false;
    }

    @Override
    public void run()
    {
        try
        {
            this.communication.initInputReader(this.in);

            while((this.communication == null || this.communication.hasNextStanza()) && !this.isClosed())
            {
                if(this.communication != null)
                {
                    Stanza currentStanza = this.communication.getNextStanza();
                    if(currentStanza != null)
                    {
                        switch(currentStanza.getEventType())
                        {
                            case START_ELEMENT:
                                this.stanzaStack.addFirst(currentStanza);

                                if("legion:ping".equals(currentStanza.getName()))
                                {
                                    this.pong();
                                } else if("legion:pong".equals(currentStanza.getName()))
                                {
                                    // ping successful
                                    if(this.pingResponder != null)
                                    {
                                        synchronized(pingResponder)
                                        {
                                            pingResponder.notify();
                                        }
                                    }
                                } else
                                {
                                    if(DEBUG_COMMUNICATOR != null)
                                    {
                                        DEBUG_COMMUNICATOR.consumeStartElement(currentStanza);
                                    }

                                    if(Communicator.LEGION_NAMESPACE_URI.equals(currentStanza.getNameSpaceURI()))
                                    {
                                        this.consumeStartElement(currentStanza);
                                    } else
                                    {
                                        ICommunicator communicator = moduleCommunicators
                                                .get(currentStanza.getNameSpaceURI());
                                        if(communicator != null)
                                        {
                                            communicator.consumeStartElement(currentStanza);
                                        } else
                                        {
                                            Logger.debug(LegionLogger.RECEIVED,
                                                         "Unknown xml stanza namespace " + currentStanza
                                                                 .getNameSpaceURI());
                                        }
                                    }
                                }
                                break;

                            case CHARACTERS:
                                if(DEBUG_COMMUNICATOR != null)
                                {
                                    DEBUG_COMMUNICATOR.consumeCharacters(currentStanza);
                                }

                                if(Communicator.LEGION_NAMESPACE_URI.equals(currentStanza.getNameSpaceURI()))
                                {
                                    this.consumeCharacters(currentStanza);
                                } else
                                {
                                    ICommunicator communicator = moduleCommunicators
                                            .get(currentStanza.getNameSpaceURI());
                                    if(communicator != null)
                                    {
                                        communicator.consumeCharacters(currentStanza);
                                    } else
                                    {
                                        Logger.debug(LegionLogger.RECEIVED,
                                                     "Unknown xml stanza namespace " + currentStanza.getNameSpaceURI());
                                    }
                                }
                                break;

                            case END_ELEMENT:
                                if(DEBUG_COMMUNICATOR != null)
                                {
                                    DEBUG_COMMUNICATOR.consumeEndElement(currentStanza);
                                }

                                if(Communicator.LEGION_NAMESPACE_URI.equals(currentStanza.getNameSpaceURI()))
                                {
                                    this.consumeEndElement(currentStanza);
                                } else
                                {
                                    ICommunicator communicator = moduleCommunicators
                                            .get(currentStanza.getNameSpaceURI());
                                    if(communicator != null)
                                    {
                                        communicator.consumeEndElement(currentStanza);
                                    } else
                                    {
                                        Logger.debug(LegionLogger.RECEIVED,
                                                     "Unknown xml stanza namespace " + currentStanza.getNameSpaceURI());
                                    }
                                }

                                // pop start/character stanza from stack
                                this.stanzaStack.pop();
                                // set last stanza from stack as current stanza
                                this.communication.setCurrentStanza(this.stanzaStack.peek());
                                break;
                        }
                    }
                } else
                {
                    try
                    {
                        // wait for the reader to get ready
                        Thread.sleep(500);
                    } catch(InterruptedException e)
                    {
                        Logger.exception(LegionLogger.STDERR, e);
                    }
                }
            }
        } catch(Exception e)
        {
            Logger.exception(LegionLogger.STDERR, e);
        } finally
        {
            try
            {
                this.closeSocket();
                this.socketUnexpectedClosed();
            } catch(Exception e)
            {
                Logger.exception(LegionLogger.STDERR, e);
            }
        }
    }

    protected abstract void socketUnexpectedClosed();

    @Override
    public final void write(Stanza message) throws IOException, SocketException
    {
        if(this.socket != null && !this.socket.isClosed() && message != null)
        {
            Logger.debug(LegionLogger.SEND, "send: " + message);

            if(DEBUG_COMMUNICATOR != null)
            {
                DEBUG_COMMUNICATOR.write(message);
            }
            this.communication.write(this.out, message);
        }
    }

    @Override
    public abstract void consumeStartElement(final Stanza currentStanza) throws IOException;

    @Override
    public abstract void consumeCharacters(final Stanza currentStanza) throws IOException;

    @Override
    public abstract void consumeEndElement(final Stanza currentStanza) throws IOException;

    public final synchronized void close() throws IOException
    {
        Stanza stanza = new Stanza();
        stanza.setName("legion:stream");
        stanza.setSequenceId(this.localStanzaSequenceId++);
        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
        this.write(stanza);

        this.isCloseRequested = true;
    }

    protected final void closeSocket() throws IOException
    {
        if(this.socket != null && !this.socket.isClosed())
        {
            this.in.close();
            this.out.close();
            this.socket.close();
            this.socket = null;
            Logger.info(LegionLogger.STDOUT, "Connection closed");
        }
    }

    public final boolean isClosed()
    {
        return this.socket == null || this.socket.isClosed() || this.isCloseRequested;
    }

    public final void accept(Stanza message) throws IOException
    {
        Stanza stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setName("legion:accept");
        this.write(stanza);

        this.write(message);

        stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
        stanza.setName("legion:accept");
        this.write(stanza);
    }

    protected final boolean isAcceptAt(int index)
    {
        return this.isStackAt(index, "legion:accept");
    }

    protected final void decline(String type, String reason) throws IOException
    {
        Stanza stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.CHARACTERS);
        stanza.setName("legion:decline");
        stanza.putAttribute("type", type);
        stanza.setValue(reason);
        this.write(stanza);
    }

    public final void ping(Object sync) throws IOException
    {
        this.pingResponder = sync;
        Stanza stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setEmptyElement(true);
        stanza.setName("legion:ping");
        this.write(stanza);
    }

    public final void pong() throws IOException
    {
        Stanza stanza = new Stanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setEmptyElement(true);
        stanza.setName("legion:pong");
        this.write(stanza);
    }

    protected final boolean isDeclineAt(int index)
    {
        return this.isStackAt(index, "legion:decline");
    }

    protected final boolean isStackAt(int index, String name)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return this.stanzaStack.get(index).getName().equals(name);
        }
        return false;
    }

    protected final boolean isParameterAt(int index, String name)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return this.stanzaStack.get(index).containsAttributeKey(name);
        }
        return false;
    }

    protected final String getNameAt(int index)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return this.stanzaStack.get(index).getName();
        }
        return null;
    }

    protected final int getEventTypeAt(int index)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return this.stanzaStack.get(index).getEventType();
        }
        return -1;
    }

    protected final String getValueAt(int index)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null &&
                this.stanzaStack.get(index).getEventType() == XMLStreamConstants.CHARACTERS)
        {
            return this.stanzaStack.get(index).getValue();
        }
        return null;
    }

    protected final List<String> getParameterKeysAt(int index)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return new ArrayList<String>(this.stanzaStack.get(index).getAttributeKeys());
        }
        return null;
    }

    protected final String getParameterValueAt(int index, String key)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null &&
                this.stanzaStack.get(index).containsAttributeKey(key))
        {
            return this.stanzaStack.get(index).getAttribute(key);
        }
        return null;
    }

    protected final int getPosInStack(String name)
    {
        for(int i = 0; i < this.stanzaStack.size(); i++)
        {
            if(this.stanzaStack.get(i) != null)
            {
                if(this.stanzaStack.get(i).getName().equals(name))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public String getNamespace()
    {
        return Communicator.LEGION_NAMESPACE;
    }

    @Override
    public String getNamespaceURI()
    {
        return Communicator.LEGION_NAMESPACE_URI;
    }

    protected Map<String, Object> createWhereMapFromString(String where)
    {
        if(where != null)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            String[] tokens = where.split("###");
            if(tokens != null && tokens.length > 0)
            {
                for(String token : tokens)
                {
                    if(token != null)
                    {
                        String[] keyAndValue = token.split("=", 1);
                        if(keyAndValue != null && keyAndValue.length == 2)
                        {
                            map.put(keyAndValue[0], keyAndValue[1]);
                        }
                    }
                }
            }
        }
        return null;
    }

    protected String createWhereStringFromMap(Map<String, Object> map)
    {
        if(map != null)
        {
            return map.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("###"));
        }
        return null;
    }

    @Override
    public InetAddress getAddress()
    {
        return this.socket.getInetAddress();
    }

    @Override
    public int getPort()
    {
        return this.socket.getPort();
    }

    protected static boolean validateRight(GroupRightEntity groupRightEntity, IRight right)
    {
        if(groupRightEntity != null && right != null)
        {
            if(groupRightEntity.getName() != null && right.getName() != null &&
                    groupRightEntity.getName().equals(right.getName()))
            {
                return groupRightEntity.isActive();
            }

            if(groupRightEntity.getChildren() != null)
            {
                for(GroupRightEntity childRightEntity : groupRightEntity.getChildren())
                {
                    if(validateRight(childRightEntity, right))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public String getCommunicationType(){
        if(this.communication != null)
        {
            return this.communication.toString();
        }
        return "not set";
    }

    protected void resetReader() throws CommunicationException
    {
        this.communication.initInputReader(this.in);
    }
}
