package cuina.legion.server;

import cuina.legion.shared.Base64;
import cuina.legion.shared.Communicator;
import cuina.legion.shared.data.Dataset;
import cuina.legion.shared.data.DatasetType;
import cuina.legion.shared.data.IDatasetType;
import cuina.legion.shared.data.XmlStanza;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;

import javax.net.ssl.*;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.*;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ServerCommunicator extends Communicator
{
	protected final static String       SERVER_NAME     = "legion_server";
	protected final static String       SERVER_VERSION  = "0";
	private final static   List<String> SERVER_FEATURES = new ArrayList<String>();
	final         String   keyStoreFile;
	final         String   keyStorePassword;
	final         String[] cipherSuites;
	private final String[] authMechanisms;
	SaslServer saslServer;
	String     userName;
	String     blacklistedClientsRegex;
	private boolean serverAcceptedFromClient;
	private boolean tlsEstablished;
	private String  clientName;
	private String  clientVersion;
	private List<String> clientFeatures = new ArrayList<String>();

	public ServerCommunicator(Socket socket, String authMechanisms, String blacklistedClientsRegex,
			final String keyStoreFile,
			final String keyStorePassword, final String[] cipherSuites)
	{
		super(socket);

		this.authMechanisms = authMechanisms.split(" ");
		this.blacklistedClientsRegex = blacklistedClientsRegex;

		this.keyStoreFile = keyStoreFile;
		this.keyStorePassword = keyStorePassword;
		this.cipherSuites = cipherSuites;

		if(this.keyStoreFile != null && !this.keyStoreFile.isEmpty()
				&& new File(this.keyStoreFile).exists())
		{
			ServerCommunicator.SERVER_FEATURES.add("starttls");
		}
		try
		{
			this.initInputReader();
		} catch (XMLStreamException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	private void sendServer() throws IOException
	{
		XmlStanza stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.setName("legion:server");
		stanza.setSequenceId(this.localStanzaSequenceId++);
		stanza.getAttributes().put("name", ServerCommunicator.SERVER_NAME);
		stanza.getAttributes().put("version", ServerCommunicator.SERVER_VERSION);
		this.write(stanza);

		stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.setName("legion:features");
		this.write(stanza);

		for(String feature : ServerCommunicator.SERVER_FEATURES)
		{
			stanza = new XmlStanza();
			stanza.setEventType(XMLStreamConstants.CHARACTERS);
			stanza.setName("legion:feature");
			stanza.setValue(feature);
			this.write(stanza);
		}

		stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		stanza.setName("legion:features");
		this.write(stanza);

		stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.setName("legion:mechanisms");
		this.write(stanza);

		for(String authMechanism : this.authMechanisms)
		{
			if("PLAIN".equalsIgnoreCase(authMechanism)
					&& !ServerCommunicator.SERVER_FEATURES.contains("starttls"))
			{
				continue;
			}

			stanza = new XmlStanza();
			stanza.setEventType(XMLStreamConstants.CHARACTERS);
			stanza.setName("legion:mechanism");
			stanza.setValue(authMechanism);
			this.write(stanza);
		}

		stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		stanza.setName("legion:mechanisms");
		this.write(stanza);

		stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		stanza.setName("legion:server");
		this.write(stanza);
	}

	@Override
	public final void consumeStartElement(final XmlStanza currentStanza) throws IOException
	{
		XmlStanza stanza;

		String stanzaName = currentStanza.getName();

		switch(stanzaName)
		{
			case "legion:stream":
				stanza = new XmlStanza();
				stanza.setEventType(XMLStreamConstants.START_ELEMENT);
				stanza.setName("legion:stream");
				stanza.setSequenceId(this.localStanzaSequenceId++);
				stanza.getAttributes().put("xmlns:" + this.getNamespace(),
						"'" + this.getNamespaceURI() + "'");
				this.write(stanza);
				break;
			case "legion:client":

				this.clientName = currentStanza.getAttributes().get("name");
				this.clientVersion = currentStanza.getAttributes().get("version");

				break;
			case "legion:starttls":
				if(this.serverAcceptedFromClient
						&& ServerCommunicator.SERVER_FEATURES.contains("starttls")
						&& this.clientFeatures.contains("starttls"))
				{
					try
					{
						if(!this.tlsEstablished && this.keyStorePassword != null
								&& this.keyStoreFile != null && !this.keyStoreFile.isEmpty()
								&& new File(this.keyStoreFile).exists())
						{
							if(this.keyStorePassword.isEmpty())
							{
								System.out.println("WARNING: Your key store password is empty!!!");
							}
							stanza = new XmlStanza();
							stanza.setEventType(XMLStreamConstants.START_ELEMENT);
							stanza.setEmptyElement(true);
							stanza.setName("legion:proceedtls");
							stanza.setSequenceId(this.localStanzaSequenceId++);
							this.write(stanza);
							this.tlsEstablished = this.setSslSocket(this.keyStorePassword,
									this.keyStoreFile, this.cipherSuites);
							if(this.tlsEstablished)
							{
								this.resetReader();
							}
						} else
						{
							this.decline("starttls", "there is no valid certificate selected");
						}
					} catch (Exception e)
					{
						Logger.exception(LegionLogger.STDERR, e);
						this.decline("starttls", "there is no valid certificate selected");
					}
				}
				break;
			case "legion:auth":
				if(this.serverAcceptedFromClient)
				{
					if(currentStanza.getAttributes() != null
							&& currentStanza.getAttributes().containsKey("mechanism"))
					{
						String mechanism = currentStanza.getAttributes().get("mechanism");

						if(Arrays.asList(this.authMechanisms).contains(mechanism)
								|| (Arrays.asList(this.authMechanisms).contains(mechanism)
								&& "PLAIN".equalsIgnoreCase(mechanism)
								&& ServerCommunicator.SERVER_FEATURES
								.contains("starttls")))
						{

							byte[] initialResponse = new byte[0];
							if(currentStanza.getAttributes().containsKey("initialResponse"))
							{
								String initialResponseAsString = currentStanza.getAttributes().get(
										"initialResponse");
								if(initialResponseAsString != null
										&& !initialResponseAsString.isEmpty())
								{
									initialResponse = Base64.decode(initialResponseAsString);
								}
							}

							this.saslServer = Sasl.createSaslServer(mechanism, "legion",
									ServerCommunicator.SERVER_NAME + "_"
											+ ServerCommunicator.SERVER_VERSION,
									new HashMap<String, Object>(), (Callback[] callbacks) ->
									{
										NameCallback ncb = null;
										PasswordCallback pcb = null;
										RealmCallback rcb = null;

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
												AuthorizeCallback acb = (AuthorizeCallback) callback;
												acb.setAuthorized(true);
											}
										}

										if(rcb != null)
										{
											rcb.setText(ServerCommunicator.SERVER_NAME + "_"
													+ ServerCommunicator.SERVER_VERSION);
										}

										if(ncb != null && pcb != null)
										{
											ServerCommunicator.this.userName = ncb
													.getDefaultName();

											if(Server.getDatabase().getUsers() != null
													&& Server
													.getDatabase()
													.getUsers()
													.contains(
															ServerCommunicator.this.userName))
											{
												String password = Server
														.getDatabase()
														.getPassword(
																ServerCommunicator.this.userName);

												pcb.setPassword(password != null
														&& !password.isEmpty() ? password
														.toCharArray() : null);
											} else
											{
												pcb.setPassword(null);
											}
										}
									});

							if(this.saslServer != null)
							{
								try
								{
									byte[] challenge = this.saslServer
											.evaluateResponse(initialResponse);

									if(!this.saslServer.isComplete())
									{
										stanza = new XmlStanza();
										stanza.setName("legion:challenge");
										stanza.setSequenceId(this.localStanzaSequenceId++);
										stanza.setEventType(XMLStreamConstants.CHARACTERS);
										stanza.setValue(Base64.encodeBytes(challenge));
										this.write(stanza);
									} else
									{
										stanza = new XmlStanza();
										stanza.setName("legion:success");
										stanza.setSequenceId(this.localStanzaSequenceId++);
										stanza.setEventType(XMLStreamConstants.START_ELEMENT);
										stanza.setEmptyElement(true);
										this.write(stanza);
									}
								} catch (SaslException e)
								{
									stanza = new XmlStanza();
									stanza.setName("legion:failure");
									stanza.setSequenceId(this.localStanzaSequenceId++);
									stanza.setEventType(XMLStreamConstants.CHARACTERS);
									stanza.setValue(e.getMessage());
									this.write(stanza);
								}
							} else
							{
								stanza = new XmlStanza();
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
					if("set".equals(currentStanza.getAttributes().get("action"))
							&& currentStanza.getSequenceId() != null
							&& currentStanza.getSequenceId().matches("\\d+"))
					{
						this.cachedDatasets.put(Long.parseLong(currentStanza.getSequenceId()),
								new ArrayList<Dataset>());
					}
				}
				break;

			default:
				if(this.isAuthenficated())
				{
					for(IDatasetType datasetType : DatasetType.values())
					{
						if(datasetType.getXmlStanzaName().equals(stanzaName))
						{
							this.parseDataset(currentStanza, datasetType);
							break;
						}
					}
				}
		}
	}

	@Override
	public final void consumeCharacters(final XmlStanza currentStanza) throws IOException
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
			case "legion:response":
				if(this.saslServer != null && !this.saslServer.isComplete())
				{
					XmlStanza stanza;
					try
					{
						stanza = new XmlStanza();
						byte[] challenge = this.saslServer.evaluateResponse(Base64
								.decode(currentStanza.getValue()));

						stanza.setName("legion:challenge");
						stanza.setSequenceId(this.localStanzaSequenceId++);
						stanza.setEventType(XMLStreamConstants.CHARACTERS);
						stanza.setValue(Base64.encodeBytes(challenge));
						this.write(stanza);

						if(this.saslServer.isComplete())
						{
							stanza = new XmlStanza();
							stanza.setName("legion:success");
							stanza.setSequenceId(this.localStanzaSequenceId++);
							stanza.setEventType(XMLStreamConstants.START_ELEMENT);
							stanza.setEmptyElement(true);
							this.write(stanza);
						}
					} catch (SaslException e)
					{
						stanza = new XmlStanza();
						stanza.setName("legion:failure");
						stanza.setSequenceId(this.localStanzaSequenceId++);
						stanza.setEventType(XMLStreamConstants.CHARACTERS);
						stanza.setValue(e.getMessage());
						this.write(stanza);
					}
				}
				break;
			case "legion:column":
				if(this.isAuthenficated())
				{
					if(this.isStackAt(2, "legion:query")
							&& "set".equals(this.getParameterValueAt(2, "action")))
					{
						List<Dataset> datasets = this.cachedDatasets.get(Long
								.parseLong(currentStanza.getSequenceId()));
						if(datasets != null)
						{
							datasets.get(datasets.size() - 1).fromXML(currentStanza);
						}
					}
				}
				break;
		}
	}

	@Override
	public final void consumeEndElement(final XmlStanza currentStanza) throws IOException
	{
		String stanzaName = currentStanza.getName();

		switch(stanzaName)
		{
			case "legion:server":
				if(this.isAcceptAt(1) && currentStanza.getValue() != null)
				{
					String[] identification = currentStanza.getValue().split(":");
					if(identification.length == 2
							&& ServerCommunicator.SERVER_NAME.equals(identification[0])
							&& ServerCommunicator.SERVER_VERSION.equals(identification[1]))
					{
						this.serverAcceptedFromClient = true;
					}
				}
				break;
			case "legion:client":

				if(this.clientName == null || this.clientName.isEmpty()
						|| this.clientVersion == null || this.clientVersion.isEmpty())
				{
					this.decline("legion:client", "Invalid Client Identification received");
				} else if(this.blacklistedClientsRegex != null
						&& !this.blacklistedClientsRegex.isEmpty())
				{
					String checkValue = this.clientName + "_" + this.clientVersion;
					if(checkValue.matches(this.blacklistedClientsRegex))
					{
						this.decline("legion:client", "Client is blacklisted on Server");
					}
				} else
				{
					XmlStanza stanza = new XmlStanza();
					stanza.setEventType(XMLStreamConstants.CHARACTERS);
					stanza.setName("legion:client");
					stanza.setSequenceId(this.localStanzaSequenceId++);
					stanza.setValue(this.clientName + ":" + this.clientVersion);

					this.accept(stanza);
					this.sendServer();
				}

				break;
			case "legion:stream":
				if(!this.isCloseRequested)
				{
					this.isCloseRequested = true;
					XmlStanza stanza = new XmlStanza();
					stanza.setEventType(XMLStreamConstants.END_ELEMENT);
					stanza.setName("legion:stream");
					stanza.setSequenceId(this.localStanzaSequenceId++);
					this.write(stanza);

					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
					}
				}

				this.closeSocket();
				break;

			case "legion:query":
				if(this.isAuthenficated())
				{
					final String id = currentStanza.getSequenceId();
					if(id != null && id.matches("\\d+")
							&& "set".equals(currentStanza.getAttributes().get("action")))
					{
						final List<Dataset> datasets = this.cachedDatasets.remove(Long
								.parseLong(id));

						if(datasets != null && !datasets.isEmpty())
						{
							new Thread("DatabaseSetThread #" + id)
							{
								@Override
								public void run()
								{
									Database db = Server.getDatabase();
									List<Dataset> datasetsToSend = new ArrayList<Dataset>();

									for(Dataset dataset : datasets)
									{
										Integer setId = dataset.getInteger("id");

										if(setId != null && setId > 0)
										{
											db.update(dataset, "id = " + setId);
										} else
										{
											setId = db.insert(dataset);
											dataset.set("id", setId);

											datasetsToSend.add(dataset);
										}
									}

									if(!datasetsToSend.isEmpty())
									{
										List<XmlStanza> list = new ArrayList<XmlStanza>();
										XmlStanza startStanza = new XmlStanza();
										startStanza.setName("legion:query");
										startStanza.getAttributes().put("type", "result");
										startStanza.setSequenceId(id);
										startStanza.setEventType(XMLStreamConstants.START_ELEMENT);
										list.add(startStanza);

										for(Dataset dataset : datasetsToSend)
										{
											list.addAll(dataset.toXML(id));
										}

										XmlStanza endStanza = new XmlStanza();
										endStanza.setName("legion:query");
										endStanza.setEventType(XMLStreamConstants.END_ELEMENT);
										list.add(endStanza);

										synchronized(ServerCommunicator.this)
										{
											for(XmlStanza stanza : list)
											{
												try
												{
													ServerCommunicator.this.write(stanza);
												} catch (IOException e)
												{
													Logger.exception(LegionLogger.SEND, e);
												}
											}
										}
									} else
									{
										XmlStanza startStanza = new XmlStanza();
										startStanza.setName("legion:query");
										startStanza.getAttributes().put("type", "result");
										startStanza.setSequenceId(id);
										startStanza.setEventType(XMLStreamConstants.START_ELEMENT);
										startStanza.setEmptyElement(true);

										synchronized(ServerCommunicator.this)
										{
											try
											{
												ServerCommunicator.this.write(startStanza);
											} catch (IOException e)
											{
												Logger.exception(LegionLogger.SEND, e);
											}
										}
									}
								}
							}.start();
						}
					}
				}
				break;
		}
	}

	@Override
	protected final boolean setSslSocket(String keyStorePassword, String keyStoreFile,
			String[] cipherSuites)
			throws IOException, NoSuchAlgorithmException, KeyManagementException,
			CertificateException, KeyStoreException, UnrecoverableKeyException, XMLStreamException
	{
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

		char[] passphrase = keyStorePassword.toCharArray();
		KeyStore keystore = KeyStore.getInstance("JKS");

		if(new File(keyStoreFile).exists())
		{
			InputStream in = new FileInputStream(keyStoreFile);
			keystore.load(in, passphrase);
			in.close();

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keystore, passphrase);
			KeyManager[] keyManagers = kmf.getKeyManagers();

			sslContext.init(keyManagers, null, null);
			SSLSocketFactory sslFactory = sslContext.getSocketFactory();
			this.sslSocket = (SSLSocket) sslFactory.createSocket(this.socket, null,
					this.socket.getLocalPort(), false);
			this.sslSocket
					.setEnabledCipherSuites(cipherSuites != null && cipherSuites.length > 0 ?
							cipherSuites :
							new String[] { "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256" });
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

	private void parseDataset(XmlStanza currentStanza, IDatasetType datasetType)
	{
		if(this.isStackAt(1, "legion:query") && this.isParameterAt(1, "action")
				&& this.isParameterAt(1, "sequenceId"))
		{

			final long id = Long.parseLong(this.getParameterValueAt(1, "sequenceId"));
			switch(this.getParameterValueAt(1, "action"))
			{
				case "get":
					String loadForeignKeyStr = this.getParameterValueAt(1, "loadForeignKeys");
					boolean loadForeignKey = loadForeignKeyStr != null
							&& ("1".equals(loadForeignKeyStr) || "true".equals(loadForeignKeyStr)
							|| "yes"
							.equals(loadForeignKeyStr));

					this.getDatasets(id, datasetType, loadForeignKey);
					break;
				case "set":
					if(currentStanza.getSequenceId() != null
							&& currentStanza.getSequenceId().matches("\\d+"))
					{
						this.setDataset(Long.parseLong(currentStanza.getSequenceId()), datasetType);
					}
					break;
				case "delete":
					final String deleteId = currentStanza.getAttributes().get("id");

					this.deleteDataset(id, deleteId, datasetType);
					break;
			}
		}
	}

	private void getDatasets(final long id, final IDatasetType datasetType,
			final boolean loadForeignKey)
	{
		// asynchron database action - otherwise the
		// connection can
		// not be used until the action is done
		new Thread("DatabaseQueryThread #" + id)
		{
			@Override
			public void run()
			{
				Database db = Server.getDatabase();

				List<Dataset> result = db.select(datasetType, "*", null, null, loadForeignKey);

				if(result != null)
				{
					List<XmlStanza> list = new ArrayList<XmlStanza>();
					XmlStanza startStanza = new XmlStanza();
					startStanza.setName("legion:query");
					startStanza.getAttributes().put("type", "result");
					startStanza.setSequenceId(id);
					startStanza.setEventType(XMLStreamConstants.START_ELEMENT);
					list.add(startStanza);

					for(Dataset object : result)
					{
						list.addAll(object.toXML(id));
					}

					XmlStanza endStanza = new XmlStanza();
					endStanza.setName("legion:query");
					endStanza.setEventType(XMLStreamConstants.END_ELEMENT);
					list.add(endStanza);

					synchronized(ServerCommunicator.this)
					{
						for(XmlStanza stanza : list)
						{
							try
							{
								ServerCommunicator.this.write(stanza);
							} catch (IOException e)
							{
								Logger.exception(LegionLogger.SEND, e);
							}
						}
					}
				}
			}

		}.start();
	}

	private void setDataset(long id, IDatasetType datasetType)
	{
		List<Dataset> datasets = this.cachedDatasets.get(id);
		if(datasets != null)
		{
			datasets.add(new Dataset(datasetType));
		}
	}

	private void deleteDataset(long id, final String deleteid, final IDatasetType datasetType)
	{
		new Thread("DatabaseDeleteThread #" + id)
		{
			@Override
			public void run()
			{
				Server.getDatabase().delete(datasetType, "`id` = " + deleteid);
			}
		}.start();
	}

	public String getUserName()
	{
		if(this.isAuthenficated())
		{
			return this.userName;
		}
		return null;
	}

	@Override
	protected void socketUnexpectedClosed()
	{
		// DO NOTHING
	}
}
