package niobe.legion.server;

import niobe.legion.shared.Base64;
import niobe.legion.shared.Communicator;
import niobe.legion.shared.data.XmlStanza;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.IEntity;
import niobe.legion.shared.model.marshal.XmlMarshaller;

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
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

	CallbackHandler saslServerHandler = (Callback[] callbacks) -> {
		NameCallback ncb = null;
		PasswordCallback pcb = null;
		RealmCallback rcb = null;

		for (Callback callback : callbacks)
		{
			if (callback instanceof NameCallback)
			{
				ncb = (NameCallback) callback;
			} else if (callback instanceof PasswordCallback)
			{
				pcb = (PasswordCallback) callback;
			} else if (callback instanceof RealmCallback)
			{
				rcb = (RealmCallback) callback;
			} else if (callback instanceof AuthorizeCallback)
			{
				AuthorizeCallback acb = (AuthorizeCallback) callback;
				acb.setAuthorized(true);
			}
		}

		if (rcb != null)
		{
			rcb.setText(ServerCommunicator.SERVER_NAME + "_" + ServerCommunicator.SERVER_VERSION);
		}

		if (ncb != null && pcb != null)
		{
			ServerCommunicator.this.userName = ncb.getDefaultName();

			String password = Server.getDatabase().getPassword(ServerCommunicator.this.userName);

			pcb.setPassword(password != null && !password.isEmpty() ? password.toCharArray() : null);
		}
	};

	private boolean serverAcceptedFromClient;
	private boolean tlsEstablished;
	private String  clientName;
	private String  clientVersion;
	private List<String> clientFeatures = new ArrayList<String>();

	public ServerCommunicator(Socket socket,
							  String authMechanisms,
							  String blacklistedClientsRegex,
							  final String keyStoreFile,
							  final String keyStorePassword,
							  final String[] cipherSuites)
	{
		super(socket);

		this.authMechanisms = authMechanisms.split(" ");
		this.blacklistedClientsRegex = blacklistedClientsRegex;

		this.keyStoreFile = keyStoreFile;
		this.keyStorePassword = keyStorePassword;
		this.cipherSuites = cipherSuites;

		if (this.keyStoreFile != null && !this.keyStoreFile.isEmpty() && new File(this.keyStoreFile).exists())
		{
			ServerCommunicator.SERVER_FEATURES.add("starttls");
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

		for (String feature : ServerCommunicator.SERVER_FEATURES)
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

		for (String authMechanism : this.authMechanisms)
		{
			if ("PLAIN".equalsIgnoreCase(authMechanism) && !ServerCommunicator.SERVER_FEATURES.contains("starttls"))
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

		switch (stanzaName)
		{
			case "legion:stream":
				stanza = new XmlStanza();
				stanza.setEventType(XMLStreamConstants.START_ELEMENT);
				stanza.setName("legion:stream");
				stanza.setSequenceId(this.localStanzaSequenceId++);
				stanza.getAttributes().put("xmlns:" + this.getNamespace(), "'" + this.getNamespaceURI() + "'");
				this.write(stanza);
				break;
			case "legion:client":

				this.clientName = currentStanza.getAttributes().get("name");
				this.clientVersion = currentStanza.getAttributes().get("version");

				break;
			case "legion:starttls":
				if (this.serverAcceptedFromClient && ServerCommunicator.SERVER_FEATURES.contains("starttls") &&
					this.clientFeatures.contains("starttls"))
				{
					try
					{
						if (!this.tlsEstablished && this.keyStorePassword != null && this.keyStoreFile != null &&
							!this.keyStoreFile.isEmpty() && new File(this.keyStoreFile).exists())
						{
							if (this.keyStorePassword.isEmpty())
							{
								System.out.println("WARNING: Your key store password is empty!!!");
							}
							stanza = new XmlStanza();
							stanza.setEventType(XMLStreamConstants.START_ELEMENT);
							stanza.setEmptyElement(true);
							stanza.setName("legion:proceedtls");
							stanza.setSequenceId(this.localStanzaSequenceId++);
							this.write(stanza);
							this.tlsEstablished =
									this.setSslSocket(this.keyStorePassword, this.keyStoreFile, this.cipherSuites);
							if (this.tlsEstablished)
							{
								this.resetReader();
							} else
							{
								this.decline("starttls", "server don't have a certificate");
							}
						} else
						{
							if (this.tlsEstablished)
							{
								this.decline("starttls", "tls is allready established");
							} else
							{
								this.decline("starttls", "there is no valid server certificate selected");
							}
						}
					}
					catch (Exception e)
					{
						Logger.exception(LegionLogger.STDERR, e);
						this.decline("starttls", "there is no valid server certificate selected");
					}
				}
				break;
			case "legion:auth":
				if (this.serverAcceptedFromClient)
				{
					if (currentStanza.getAttributes() != null && currentStanza.getAttributes().containsKey("mechanism"))
					{
						String mechanism = currentStanza.getAttributes().get("mechanism");

						if (Arrays.asList(this.authMechanisms).contains(mechanism) ||
							(Arrays.asList(this.authMechanisms).contains(mechanism) &&
							 "PLAIN".equalsIgnoreCase(mechanism) &&
							 ServerCommunicator.SERVER_FEATURES.contains("starttls")))
						{

							byte[] initialResponse = new byte[0];
							if (currentStanza.getAttributes().containsKey("initialResponse"))
							{
								String initialResponseAsString = currentStanza.getAttributes().get("initialResponse");
								if (initialResponseAsString != null && !initialResponseAsString.isEmpty())
								{
									initialResponse = Base64.decode(initialResponseAsString);
								}
							}

							this.saslServer =
									Sasl.createSaslServer(mechanism,
														  "legion",
														  ServerCommunicator.SERVER_NAME + "_" +
														  ServerCommunicator.SERVER_VERSION,
														  new HashMap<String, Object>(),
														  this.saslServerHandler);

							if (this.saslServer != null)
							{
								try
								{
									byte[] challenge = this.saslServer.evaluateResponse(initialResponse);

									if (!this.saslServer.isComplete())
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
								}
								catch (SaslException e)
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
				if (this.isAuthenficated())
				{
					if (("set".equals(currentStanza.getAttributes().get("action")) ||
						 "delete".equals(currentStanza.getAttributes().get("action"))) &&
						currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
					{
						this.cachedStanzas
								.put(Long.parseLong(currentStanza.getSequenceId()), new ArrayList<XmlStanza>());
					} else if ("get".equals(currentStanza.getAttributes().get("action")) &&
							   currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
					{
						if (currentStanza.getAttributes().get("class") != null)
						{
							try
							{
								long id = Long.parseLong(currentStanza.getSequenceId());
								Class<?> clazz = Class.forName(currentStanza.getAttributes().get("class"));
								String where = currentStanza.getAttributes().get("where");
								String queryName = currentStanza.getAttributes().get("namedQuery");

								Map<String, Object> whereMap = this.createWhereMapFromString(where);

								this.getDatasets(id, clazz, queryName, whereMap);
							}
							catch (ClassNotFoundException e)
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
				if (this.isAuthenficated())
				{
					if (currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
					{
						long id = Long.parseLong(currentStanza.getSequenceId());

						if (this.cachedStanzas.containsKey(id))
						{
							this.cachedStanzas.get(id).add(new XmlStanza(currentStanza));
						}
					}
				}
		}
	}

	@Override
	public final void consumeCharacters(final XmlStanza currentStanza) throws IOException
	{
		String stanzaName = currentStanza.getName();

		switch (stanzaName)
		{
			case "legion:feature":
				if (this.isStackAt(1, "legion:features") && this.isStackAt(2, "legion:client"))
				{
					this.clientFeatures.add(currentStanza.getValue());
				}
				break;
			case "legion:response":
				if (this.saslServer != null && !this.saslServer.isComplete())
				{
					XmlStanza stanza;
					try
					{
						stanza = new XmlStanza();
						byte[] challenge = this.saslServer.evaluateResponse(Base64.decode(currentStanza.getValue()));

						stanza.setName("legion:challenge");
						stanza.setSequenceId(this.localStanzaSequenceId++);
						stanza.setEventType(XMLStreamConstants.CHARACTERS);
						stanza.setValue(Base64.encodeBytes(challenge));
						this.write(stanza);

						if (this.saslServer.isComplete())
						{
							stanza = new XmlStanza();
							stanza.setName("legion:success");
							stanza.setSequenceId(this.localStanzaSequenceId++);
							stanza.setEventType(XMLStreamConstants.START_ELEMENT);
							stanza.setEmptyElement(true);
							this.write(stanza);
						}
					}
					catch (SaslException e)
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
			case "legion:dataset":
			case "legion:column":
			case "legion:entry":
				if (this.isAuthenficated())
				{
					if (currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
					{
						List<XmlStanza> stanzas = this.cachedStanzas.get(Long.parseLong(currentStanza.getSequenceId()));
						if (stanzas != null)
						{
							stanzas.add(currentStanza);
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

		switch (stanzaName)
		{
			case "legion:server":
				if (this.isAcceptAt(1) && currentStanza.getValue() != null)
				{
					String[] identification = currentStanza.getValue().split(":");
					if (identification.length == 2 && ServerCommunicator.SERVER_NAME.equals(identification[0]) &&
						ServerCommunicator.SERVER_VERSION.equals(identification[1]))
					{
						this.serverAcceptedFromClient = true;
					}
				}
				break;
			case "legion:client":

				if (this.clientName == null || this.clientName.isEmpty() || this.clientVersion == null ||
					this.clientVersion.isEmpty())
				{
					this.decline("legion:client", "Invalid Client Identification received");
				} else if (this.blacklistedClientsRegex != null && !this.blacklistedClientsRegex.isEmpty())
				{
					String checkValue = this.clientName + "_" + this.clientVersion;
					if (checkValue.matches(this.blacklistedClientsRegex))
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
				if (!this.isCloseRequested)
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
					}
					catch (InterruptedException e)
					{
					}
				}

				this.closeSocket();
				break;
			case "legion:dataset":
			case "legion:column":
			case "legion:entry":
				if (this.isAuthenficated())
				{
					if (currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
					{
						List<XmlStanza> stanzas = this.cachedStanzas.get(Long.parseLong(currentStanza.getSequenceId()));
						if (stanzas != null)
						{
							stanzas.add(currentStanza);
						}
					}
				}
				break;
			case "legion:query":
				if (this.isAuthenficated())
				{
					final String id = currentStanza.getSequenceId();
					if (id != null && id.matches("\\d+"))
					{
						final List<XmlStanza> stanzas = this.cachedStanzas.remove(Long.parseLong(id));

						if (stanzas != null && !stanzas.isEmpty())
						{
							if ("set".equals(currentStanza.getAttributes().get("action")))
							{
								new Thread("DatabaseSetThread #" + id)
								{
									@Override
									public void run()
									{
										Database db = Server.getDatabase();
										List<Object> datasetsToSend = new ArrayList<Object>();

										XmlMarshaller.unmarshall(stanzas).stream().filter(dataset -> dataset != null)
													 .forEach(dataset -> {
														 int setId = ((IEntity) dataset).getId();

														 if (setId > 0)
														 {
															 db.update(dataset);
														 } else
														 {
															 dataset = db.insert(dataset);
														 }
														 datasetsToSend.add(dataset);
													 });

										if (!datasetsToSend.isEmpty())
										{
											List<XmlStanza> list = new ArrayList<XmlStanza>();
											XmlStanza startStanza = new XmlStanza();
											startStanza.setName("legion:query");
											startStanza.getAttributes().put("type", "result");
											startStanza.setSequenceId(id);
											startStanza.setEventType(XMLStreamConstants.START_ELEMENT);
											list.add(startStanza);

											for (Object dataset : datasetsToSend)
											{
												list.addAll(XmlMarshaller.marshall(dataset, Long.parseLong(id)));
											}

											XmlStanza endStanza = new XmlStanza();
											endStanza.setName("legion:query");
											endStanza.setEventType(XMLStreamConstants.END_ELEMENT);
											list.add(endStanza);

											synchronized (ServerCommunicator.this)
											{
												for (XmlStanza stanza : list)
												{
													try
													{
														ServerCommunicator.this.write(stanza);
													}
													catch (IOException e)
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

											synchronized (ServerCommunicator.this)
											{
												try
												{
													ServerCommunicator.this.write(startStanza);
												}
												catch (IOException e)
												{
													Logger.exception(LegionLogger.SEND, e);
												}
											}
										}
									}
								}.start();
							} else if ("delete".equals(currentStanza.getAttributes().get("action")))
							{
								XmlMarshaller.unmarshall(stanzas).stream().filter(dataset -> dataset instanceof IEntity)
											 .forEach(dataset -> this.deleteDataset((IEntity) dataset));
							}
						}
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

		if (new File(keyStoreFile).exists())
		{
			InputStream in = new FileInputStream(keyStoreFile);
			keystore.load(in, passphrase);
			in.close();

			if (keystore.size() == 0)
			{
				return false;
			}

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keystore, passphrase);
			KeyManager[] keyManagers = kmf.getKeyManagers();

			sslContext.init(keyManagers, null, null);
			SSLSocketFactory sslFactory = sslContext.getSocketFactory();
			this.sslSocket =
					(SSLSocket) sslFactory.createSocket(this.socket, "localhost", this.socket.getLocalPort(), true);
			SSLParameters sslParameters = this.sslSocket.getSSLParameters();
			sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
			this.sslSocket.setSSLParameters(sslParameters);

			if (cipherSuites != null && cipherSuites.length > 0)
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

	private void getDatasets(final long id,
							 final Class datasetType,
							 final String queryName,
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
				Database db = Server.getDatabase();

				List<?> result = null;
				if (queryName != null && !queryName.isEmpty())
				{
					// Check if entity supports this query
					Optional<Annotation> annotation =
							Stream.of(datasetType.getAnnotations()).filter(an -> an instanceof NamedQueries)
								  .findFirst();
					if (annotation.isPresent())
					{
						NamedQueries namedQueries = (NamedQueries) annotation.get();

						if (namedQueries.value() != null && namedQueries.value().length > 0)
						{
							if (Stream.of(namedQueries.value()).anyMatch(query -> query.name().equals(queryName)))
							{
								// run the query
								if (parameters != null && !parameters.isEmpty())
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

				if (result != null)
				{
					List<XmlStanza> list = new ArrayList<XmlStanza>();
					XmlStanza startStanza = new XmlStanza();
					startStanza.setName("legion:query");
					startStanza.getAttributes().put("type", "result");
					startStanza.setSequenceId(id);
					startStanza.setEventType(XMLStreamConstants.START_ELEMENT);
					list.add(startStanza);

					for (Object object : result)
					{
						list.addAll(XmlMarshaller.marshall(object, id));
					}

					XmlStanza endStanza = new XmlStanza();
					endStanza.setName("legion:query");
					endStanza.setEventType(XMLStreamConstants.END_ELEMENT);
					list.add(endStanza);

					synchronized (ServerCommunicator.this)
					{
						for (XmlStanza stanza : list)
						{
							try
							{
								ServerCommunicator.this.write(stanza);
							}
							catch (IOException e)
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
		if (this.isAuthenficated())
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
