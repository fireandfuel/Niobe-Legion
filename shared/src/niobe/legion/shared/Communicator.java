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
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.net.ssl.SSLSocket;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import niobe.legion.shared.data.IRight;
import niobe.legion.shared.data.LegionRight;
import niobe.legion.shared.data.XmlStanza;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.GroupRightEntity;
import niobe.legion.shared.module.ModuleRightManager;
import niobe.legion.shared.sasl.LegionSaslProvider;

public abstract class Communicator implements XMLStreamConstants, ICommunicator, Runnable
{
    private static final String LEGION_NAMESPACE = "legion";
    private static final String LEGION_NAMESPACE_URI = "ashnurazg.de/niobe/legion";

    public static final String DEBUG_NAMESPACE = "debug";
    public static final String DEBUG_NAMESPACE_URI = "ashnurazg.de/niobe/legion_debug";

    private static final LegionSaslProvider SASL_PROVIDER = new LegionSaslProvider();

    private final HashMap<String, ICommunicator> moduleCommunicators = new HashMap<String, ICommunicator>();
    protected final HashMap<Long, List<XmlStanza>> cachedStanzas = new HashMap<Long, List<XmlStanza>>();

    static
    {
        Security.addProvider(Communicator.SASL_PROVIDER);
    }

    protected Socket socket;
    protected SSLSocket sslSocket;
    protected long localStanzaSequenceId = Math.abs(Utils.random.nextLong());
    protected boolean isCloseRequested;

    private DataInputStream in;
    private DataOutputStream out;
    private XMLInputFactory inputFactory;
    private XMLStreamReader reader;

    private LinkedList<XmlStanza> stanzaStack = new LinkedList<XmlStanza>();
    private XmlStanza currentStanza;

    protected static ICommunicator DEBUG_COMMUNICATOR;

    protected Communicator(Socket socket)
    {
        this.socket = socket;

        Logger.debug(LegionLogger.STDOUT, "connected to " + this.getAddress().getHostAddress() + ":" + this.getPort());

        try
        {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.inputFactory = XMLInputFactory.newFactory();
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

    protected void initInputReader() throws XMLStreamException
    {
        this.reader = this.inputFactory.createXMLStreamReader(this.in, "UTF-8");
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

    protected final void resetReader() throws XMLStreamException
    {
        this.reader = this.inputFactory.createXMLStreamReader(this.in, "UTF-8");
    }

    @Override
    public void run()
    {
        try
        {
            this.initInputReader();

            while((this.reader == null || this.reader.hasNext()) && !this.isClosed())
            {
                if(this.reader != null)
                {
                    this.reader.next();

                    switch(this.reader.getEventType())
                    {
                        case START_ELEMENT:
                            // create current stanza from reader
                            this.currentStanza = new XmlStanza();
                            this.currentStanza
                                    .setName(((this.reader.getName().getPrefix() != null) ? (this.reader.getName()
                                            .getPrefix() + ":") : "") + this.reader.getName().getLocalPart());
                            this.currentStanza.setLocalName(this.reader.getLocalName());
                            this.currentStanza.setNameSpaceURI(this.reader.getName().getNamespaceURI());
                            this.currentStanza.setEventType(XMLStreamConstants.START_ELEMENT);

                            Logger.debug(LegionLogger.RECEIVED,
                                         "received START_ELEMENT : " + this.currentStanza.getName());

                            for(int i = 0; i < this.reader.getAttributeCount(); i++)
                            {
                                this.currentStanza.putAttribute(this.reader.getAttributeLocalName(i),
                                                                this.reader.getAttributeValue(i));
                                Logger.debug(LegionLogger.RECEIVED,
                                             "attribute " + this.reader.getAttributeLocalName(i) + " : " +
                                                     this.reader.getAttributeValue(i));
                            }

                            this.stanzaStack.addFirst(this.currentStanza);

                            if("legion:ping".equals(this.currentStanza.getName()))
                            {
                                this.pong();
                            } else if("legion:pong".equals(this.currentStanza.getName()))
                            {
                                // ping successful
                            } else
                            {
                                if(DEBUG_COMMUNICATOR != null)
                                {
                                    DEBUG_COMMUNICATOR.consumeStartElement(this.currentStanza);
                                }

                                if(Communicator.LEGION_NAMESPACE_URI.equals(this.currentStanza.getNameSpaceURI()))
                                {
                                    this.consumeStartElement(this.currentStanza);
                                } else
                                {
                                    ICommunicator communicator = moduleCommunicators
                                            .get(this.currentStanza.getNameSpaceURI());
                                    if(communicator != null)
                                    {
                                        communicator.consumeStartElement(this.currentStanza);
                                    } else
                                    {
                                        Logger.debug(LegionLogger.RECEIVED,
                                                     "Unknown xml stanza namespace " + this.currentStanza
                                                             .getNameSpaceURI());
                                    }
                                }
                            }
                            break;

                        case CHARACTERS:
                            // set content of stanza
                            this.currentStanza.setValue(this.reader.getText());
                            Logger.debug(LegionLogger.RECEIVED,
                                         "received CHARACTERS : " + this.currentStanza.getName() + " value: " +
                                                 this.reader.getText());
                            this.currentStanza.setEventType(XMLStreamConstants.CHARACTERS);

                            if(DEBUG_COMMUNICATOR != null)
                            {
                                DEBUG_COMMUNICATOR.consumeCharacters(this.currentStanza);
                            }

                            if(Communicator.LEGION_NAMESPACE_URI.equals(this.currentStanza.getNameSpaceURI()))
                            {
                                this.consumeCharacters(this.currentStanza);
                            } else
                            {
                                ICommunicator communicator = moduleCommunicators
                                        .get(this.currentStanza.getNameSpaceURI());
                                if(communicator != null)
                                {
                                    communicator.consumeCharacters(this.currentStanza);
                                } else
                                {
                                    Logger.debug(LegionLogger.RECEIVED,
                                                 "Unknown xml stanza namespace " + this.currentStanza
                                                         .getNameSpaceURI());
                                }
                            }
                            break;

                        case END_ELEMENT:
                            this.currentStanza = new XmlStanza(this.currentStanza);
                            this.currentStanza.setEventType(XMLStreamConstants.END_ELEMENT);
                            Logger.debug(LegionLogger.RECEIVED,
                                         "received END_ELEMENT : " + this.currentStanza.getName());

                            if(DEBUG_COMMUNICATOR != null)
                            {
                                DEBUG_COMMUNICATOR.consumeEndElement(this.currentStanza);
                            }

                            if(Communicator.LEGION_NAMESPACE_URI.equals(this.currentStanza.getNameSpaceURI()))
                            {
                                this.consumeEndElement(this.currentStanza);
                            } else
                            {
                                ICommunicator communicator = moduleCommunicators
                                        .get(this.currentStanza.getNameSpaceURI());
                                if(communicator != null)
                                {
                                    communicator.consumeEndElement(this.currentStanza);
                                } else
                                {
                                    Logger.debug(LegionLogger.RECEIVED,
                                                 "Unknown xml stanza namespace " + this.currentStanza
                                                         .getNameSpaceURI());
                                }
                            }

                            // pop start/character stanza from stack
                            this.stanzaStack.pop();
                            // set last stanza from stack as current stanza
                            this.currentStanza = this.stanzaStack.peek();
                            break;

                        default:
                            Logger.debug(LegionLogger.RECEIVED, "Unknown event type " + this.reader.getEventType());
                            break;
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
    public final void write(XmlStanza message) throws IOException, SocketException
    {
        if(this.socket != null && !this.socket.isClosed() && message != null)
        {
            Logger.debug(LegionLogger.SEND, "send: " + message);

            if(DEBUG_COMMUNICATOR != null)
            {
                DEBUG_COMMUNICATOR.write(message);
            }

            switch(message.getEventType())
            {
                case START_ELEMENT:
                    this.out.write(("<" + message.getName()).getBytes("UTF-8"));

                    if(message.hasNoAttributes())
                    {
                        if(message.isEmptyElement())
                        {
                            this.out.write("/>".getBytes("UTF-8"));
                        } else
                        {
                            this.out.write(">".getBytes("UTF-8"));
                        }
                    } else
                    {

                        message.forEachAttribute((attrName, attribute) -> {
                            if(attribute != null)
                            {
                                if(!attribute.startsWith("\"") && !attribute.endsWith("\"") &&
                                        !attribute.startsWith("'") && !attribute.endsWith("'"))
                                {
                                    attribute = "\"" + attribute + "\"";
                                }
                                try
                                {
                                    this.out.write((" " + attrName + "=" + attribute).getBytes("UTF-8"));
                                } catch(IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });

                        if(message.isEmptyElement())
                        {
                            this.out.write("/>".getBytes("UTF-8"));
                        } else
                        {
                            this.out.write(">".getBytes("UTF-8"));
                        }
                    }

                    break;
                case CHARACTERS:
                    this.out.write(("<" + message.getName()).getBytes("UTF-8"));

                    if(message.hasNoAttributes())
                    {
                        this.out.write(">".getBytes("UTF-8"));
                    } else
                    {
                        for(String attrName : message.getAttributeKeys())
                        {
                            this.out.write((" " + attrName + "=\"" + message.getAttribute(attrName) + "\"")
                                                   .getBytes("UTF-8"));
                        }
                        this.out.write(">".getBytes("UTF-8"));
                    }

                    this.out.write(message.getValue().getBytes("UTF-8"));
                case END_ELEMENT:
                    this.out.write(("</" + message.getName() + ">").getBytes("UTF-8"));
                    break;
            }

            this.out.flush();
        }
    }

    @Override
    public abstract void consumeStartElement(final XmlStanza currentStanza) throws IOException;

    @Override
    public abstract void consumeCharacters(final XmlStanza currentStanza) throws IOException;

    @Override
    public abstract void consumeEndElement(final XmlStanza currentStanza) throws IOException;

    public final synchronized void close() throws IOException
    {
        XmlStanza stanza = new XmlStanza();
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

    public final void accept(XmlStanza message) throws IOException
    {
        XmlStanza stanza = new XmlStanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setName("legion:accept");
        this.write(stanza);

        this.write(message);

        stanza = new XmlStanza();
        stanza.setEventType(XMLStreamConstants.END_ELEMENT);
        stanza.setName("legion:accept");
        this.write(stanza);
    }

    public final boolean isAcceptAt(int index)
    {
        return this.isStackAt(index, "legion:accept");
    }

    public final void decline(String type, String reason) throws IOException
    {
        XmlStanza stanza = new XmlStanza();
        stanza.setEventType(XMLStreamConstants.CHARACTERS);
        stanza.setName("legion:decline");
        stanza.putAttribute("type", type);
        stanza.setValue(reason);
        this.write(stanza);
    }

    public final void ping() throws IOException
    {
        XmlStanza stanza = new XmlStanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setEmptyElement(true);
        stanza.setName("legion:ping");
        this.write(stanza);
    }

    protected final void pong() throws IOException
    {
        XmlStanza stanza = new XmlStanza();
        stanza.setEventType(XMLStreamConstants.START_ELEMENT);
        stanza.setEmptyElement(true);
        stanza.setName("legion:pong");
        this.write(stanza);
    }

    public final boolean isDeclineAt(int index)
    {
        return this.isStackAt(index, "legion:decline");
    }

    public final boolean isCurrent(String name)
    {
        if(this.currentStanza != null)
        {
            return this.currentStanza.getName().equals(name);
        }
        return false;
    }

    public final boolean isStackAt(int index, String name)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return this.stanzaStack.get(index).getName().equals(name);
        }
        return false;
    }

    public final boolean isParameterAt(int index, String name)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return this.stanzaStack.get(index).containsAttributeKey(name);
        }
        return false;
    }

    public final String getNameAt(int index)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return this.stanzaStack.get(index).getName();
        }
        return null;
    }

    public final int getEventTypeAt(int index)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return this.stanzaStack.get(index).getEventType();
        }
        return -1;
    }

    public final String getValueAt(int index)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null &&
                this.stanzaStack.get(index).getEventType() == XMLStreamConstants.CHARACTERS)
        {
            return this.stanzaStack.get(index).getValue();
        }
        return null;
    }

    public final List<String> getParameterKeysAt(int index)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
        {
            return new ArrayList<String>(this.stanzaStack.get(index).getAttributeKeys());
        }
        return null;
    }

    public final String getParameterValueAt(int index, String key)
    {
        if(index < this.stanzaStack.size() && this.stanzaStack.get(index) != null &&
                this.stanzaStack.get(index).containsAttributeKey(key))
        {
            return this.stanzaStack.get(index).getAttribute(key);
        }
        return null;
    }

    public final int getPosInStack(String name)
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
}
