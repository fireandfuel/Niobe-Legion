package niobe.legion.shared;

import niobe.legion.shared.data.XmlStanza;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.sasl.LegionSaslProvider;

import javax.net.ssl.SSLSocket;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

public abstract class Communicator implements XMLStreamConstants, ICommunicator, Runnable
{
	private static final String                         LEGION_NAMESPACE     = "legion";
	private static final String                         LEGION_NAMESPACE_URI = "ashnurazg.de/niobe/legion";
	private static final LegionSaslProvider             SASL_PROVIDER        = new LegionSaslProvider();
	private static final HashMap<String, ICommunicator> MODULE_COMMUNICATORS = new HashMap<String, ICommunicator>();

	protected final HashMap<Long, List<XmlStanza>> cachedStanzas = new HashMap<Long, List<XmlStanza>>();

	static
	{
		Security.addProvider(Communicator.SASL_PROVIDER);
	}

	protected Socket    socket;
	protected SSLSocket sslSocket;
	protected long localStanzaSequenceId = Math.abs(Utils.random.nextLong());
	protected boolean isCloseRequested;

	private DataInputStream  in;
	private DataOutputStream out;
	private XMLInputFactory  inputFactory;
	private XMLStreamReader  reader;

	private LinkedList<XmlStanza> stanzaStack = new LinkedList<XmlStanza>();
	private XmlStanza currentStanza;

	protected Communicator(Socket socket)
	{
		this.socket = socket;
		try
		{
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			this.inputFactory = XMLInputFactory.newFactory();
		}
		catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	public static final void addModuleCommunicator(ICommunicator communicator)
	{
		Communicator.MODULE_COMMUNICATORS.put(communicator.getNamespaceURI(), communicator);
	}

	public static final void removeModuleCommunicator(String namespaceURI)
	{
		Communicator.MODULE_COMMUNICATORS.remove(namespaceURI);
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
	public final void run()
	{
		try
		{
			while ((this.reader == null || this.reader.hasNext()) && !this.isClosed())
			{
				if (this.reader != null)
				{
					this.reader.next();

					switch (this.reader.getEventType())
					{
						case START_ELEMENT:
							// create current stanza from reader
							this.currentStanza = new XmlStanza();
							this.currentStanza.setName(((this.reader.getName().getPrefix() != null) ?
														(this.reader.getName().getPrefix() + ":") : "") +
													   this.reader.getName().getLocalPart());
							this.currentStanza.setLocalName(this.reader.getLocalName());
							this.currentStanza.setNameSpaceURI(this.reader.getName().getNamespaceURI());
							this.currentStanza.setEventType(XMLStreamConstants.START_ELEMENT);

							Logger.debug(LegionLogger.RECEIVED,
										 "received START_ELEMENT : " + this.currentStanza.getName());

							for (int i = 0; i < this.reader.getAttributeCount(); i++)
							{
								this.currentStanza.getAttributes().put(this.reader.getAttributeLocalName(i),
																	   this.reader.getAttributeValue(i));
								Logger.debug(LegionLogger.RECEIVED,
											 "attribute " + this.reader.getAttributeLocalName(i) + " : " +
											 this.reader.getAttributeValue(i));
							}

							this.stanzaStack.addFirst(this.currentStanza);

							if ("legion:ping".equals(this.currentStanza.getName()))
							{
								this.pong();
							} else if ("legion:pong".equals(this.currentStanza.getName()))
							{
								// ping successful
							} else
							{
								if (Communicator.LEGION_NAMESPACE_URI.equals(this.currentStanza.getNameSpaceURI()))
								{
									this.consumeStartElement(this.currentStanza);
								} else
								{
									ICommunicator communicator =
											Communicator.MODULE_COMMUNICATORS.get(this.currentStanza.getNameSpaceURI());
									if (communicator != null)
									{
										communicator.consumeStartElement(this.currentStanza);
									} else
									{
										Logger.debug(LegionLogger.RECEIVED,
													 "Unknown xml stanza namespace " +
													 this.currentStanza.getNameSpaceURI());
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

							if (Communicator.LEGION_NAMESPACE_URI.equals(this.currentStanza.getNameSpaceURI()))
							{
								this.consumeCharacters(this.currentStanza);
							} else
							{
								ICommunicator communicator =
										Communicator.MODULE_COMMUNICATORS.get(this.currentStanza.getNameSpaceURI());
								if (communicator != null)
								{
									communicator.consumeCharacters(this.currentStanza);
								} else
								{
									Logger.debug(LegionLogger.RECEIVED,
												 "Unknown xml stanza namespace " +
												 this.currentStanza.getNameSpaceURI());
								}
							}
							break;

						case END_ELEMENT:
							this.currentStanza = new XmlStanza(this.currentStanza);
							this.currentStanza.setEventType(XMLStreamConstants.END_ELEMENT);
							Logger.debug(LegionLogger.RECEIVED,
										 "received END_ELEMENT : " + this.currentStanza.getName());

							if (Communicator.LEGION_NAMESPACE_URI.equals(this.currentStanza.getNameSpaceURI()))
							{
								this.consumeEndElement(this.currentStanza);
							} else
							{
								ICommunicator communicator =
										Communicator.MODULE_COMMUNICATORS.get(this.currentStanza.getNameSpaceURI());
								if (communicator != null)
								{
									communicator.consumeEndElement(this.currentStanza);
								} else
								{
									Logger.debug(LegionLogger.RECEIVED,
												 "Unknown xml stanza namespace " +
												 this.currentStanza.getNameSpaceURI());
								}
							}

							// pop stanza from stack
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
					}
					catch (InterruptedException e)
					{
						Logger.exception(LegionLogger.STDERR, e);
					}
				}
			}
		}
		catch (Exception e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
		finally
		{
			try
			{
				this.closeSocket();
				this.socketUnexpectedClosed();
			}
			catch (Exception e)
			{
				Logger.exception(LegionLogger.STDERR, e);
			}
		}
	}

	protected abstract void socketUnexpectedClosed();

	public final void write(XmlStanza message) throws IOException, SocketException
	{
		if (this.socket != null && !this.socket.isClosed() && message != null)
		{
			Logger.debug(LegionLogger.SEND, "send: " + message);

			switch (message.getEventType())
			{
				case START_ELEMENT:
					this.out.write(("<" + message.getName()).getBytes("UTF-8"));

					if (message.getAttributes().isEmpty())
					{
						if (message.isEmptyElement())
						{
							this.out.write("/>".getBytes("UTF-8"));
						} else
						{
							this.out.write(">".getBytes("UTF-8"));
						}
					} else
					{
						for (String attrName : message.getAttributes().keySet())
						{
							String attribute = message.getAttributes().get(attrName);

							if (attribute != null)
							{
								if (!attribute.startsWith("\"") && !attribute.endsWith("\"") &&
									!attribute.startsWith("'") && !attribute.endsWith("'"))
								{
									attribute = "\"" + attribute + "\"";
								}
								this.out.write((" " + attrName + "=" + attribute).getBytes("UTF-8"));
							}

						}
						if (message.isEmptyElement())
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

					if (message.getAttributes().isEmpty())
					{
						this.out.write(">".getBytes("UTF-8"));
					} else
					{
						for (String attrName : message.getAttributes().keySet())
						{
							this.out.write((" " + attrName + "=\"" + message.getAttributes().get(attrName) + "\"")
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

	public final synchronized void close() throws SocketException, IOException
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
		if (this.socket != null && !this.socket.isClosed())
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
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.setName("legion:decline");
		this.write(stanza);

		stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.CHARACTERS);
		stanza.setName("legion:reason");
		stanza.getAttributes().put("type", type);
		stanza.setValue(reason);
		this.write(stanza);

		stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		stanza.setName("legion:decline");
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
		if (this.currentStanza != null)
		{
			return this.currentStanza.getName().equals(name);
		}
		return false;
	}

	public final boolean isStackAt(int index, String name)
	{
		if (index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
		{
			return this.stanzaStack.get(index).getName().equals(name);
		}
		return false;
	}

	public final boolean isParameterAt(int index, String name)
	{
		if (index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
		{
			return this.stanzaStack.get(index).getAttributes().containsKey(name);
		}
		return false;
	}

	public final String getNameAt(int index)
	{
		if (index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
		{
			return this.stanzaStack.get(index).getName();
		}
		return null;
	}

	public final int getEventTypeAt(int index)
	{
		if (index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
		{
			return this.stanzaStack.get(index).getEventType();
		}
		return -1;
	}

	public final String getValueAt(int index)
	{
		if (index < this.stanzaStack.size() && this.stanzaStack.get(index) != null &&
			this.stanzaStack.get(index).getEventType() == XMLStreamConstants.CHARACTERS)
		{
			return this.stanzaStack.get(index).getValue();
		}
		return null;
	}

	public final List<String> getParameterKeysAt(int index)
	{
		if (index < this.stanzaStack.size() && this.stanzaStack.get(index) != null)
		{
			return new ArrayList<String>(this.stanzaStack.get(index).getAttributes().keySet());
		}
		return null;
	}

	public final String getParameterValueAt(int index, String key)
	{
		if (index < this.stanzaStack.size() && this.stanzaStack.get(index) != null &&
			this.stanzaStack.get(index).getAttributes().containsKey(key))
		{
			return this.stanzaStack.get(index).getAttributes().get(key);
		}
		return null;
	}

	public final int getPosInStack(String name)
	{
		for (int i = 0; i < this.stanzaStack.size(); i++)
		{
			if (this.stanzaStack.get(i) != null)
			{
				if (this.stanzaStack.get(i).getName().equals(name))
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
		if (where != null)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			String[] tokens = where.split("###");
			if (tokens != null && tokens.length > 0)
			{
				for (String token : tokens)
				{
					if (token != null)
					{
						String[] keyAndValue = token.split("=", 1);
						if (keyAndValue != null && keyAndValue.length == 2)
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
		if (map != null)
		{
			return map.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
					  .collect(Collectors.joining("###"));
		}
		return null;
	}
}
