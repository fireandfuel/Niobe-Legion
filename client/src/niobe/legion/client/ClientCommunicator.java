package niobe.legion.client;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.stage.Modality;
import niobe.legion.client.Client.CommunicatorTask;
import niobe.legion.client.gui.connect.CertificateController;
import niobe.legion.client.gui.connect.ConnectController;
import niobe.legion.client.gui.connect.LoginController;
import niobe.legion.client.gui.debug.DebugController;
import niobe.legion.shared.Base64;
import niobe.legion.shared.Communicator;
import niobe.legion.shared.data.IRight;
import niobe.legion.shared.data.LegionRight;
import niobe.legion.shared.data.XmlStanza;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.GroupRightEntity;
import niobe.legion.shared.model.marshal.XmlMarshaller;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
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
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Bidirectional communicator client to server based on XML stream parsing
 * (StAX).<br>
 * It supports SSL and authenfication over SASL. The communication is influenced
 * by RFC 6120 (xmpp). <br>
 * Runs in JavaFX Thread as a JavaFX service. The service is closely integrated
 * into the JavaFX GUI.<br>
 *
 * @author fireandfuel
 */
public class ClientCommunicator extends Communicator
{
	public static final    int          SERVER_AUTH_ERR = 0;
	public static final    int          CLIENT_AUTH_ERR = 1;
	protected final static String       CLIENT_NAME     = "legion_client";
	protected final static String       CLIENT_VERSION  = "0";
	private final static   List<String> CLIENT_FEATURES = Arrays.asList("starttls");

	final String   keyStoreFile;
	final String   keyStorePassword;
	final String[] cipherSuites;

	private final String[] authMechanisms;
	private final HashMap<Long, DatasetReceiver> databaseRetrievers = new HashMap<Long, DatasetReceiver>();

	SaslClient             saslClient;
	boolean                serverSideAuthenficated;
	String                 blacklistedServersRegex;
	List<GroupRightEntity> userRights;

	private CommunicatorTask connectTask;

	private boolean clientAcceptedFromServer;
	private boolean tlsEstablished;
	private String  serverName;
	private String  serverVersion;
	private List<String> serverFeatures = new ArrayList<String>();

	private List<String> serverAuthMechanisms = new ArrayList<String>();
	private static DebugController debugController;

	String                 userName;
	String                 groupName;
	List<GroupRightEntity> groupRights;

	public ClientCommunicator(Socket socket,
							  String authMechanisms,
							  String blacklistedServersRegex,
							  final String keyStoreFile,
							  final String keyStorePassword,
							  final String[] cipherSuites)
	{
		super(socket);
		this.authMechanisms = authMechanisms.split(" ");
		this.blacklistedServersRegex = blacklistedServersRegex;
		this.keyStoreFile = keyStoreFile;
		this.keyStorePassword = keyStorePassword;
		this.cipherSuites = cipherSuites;

		if (Client.isDebug())
		{
			try
			{
				if (debugController == null || debugController.isClosed())
				{
					debugController = (DebugController) Client.getFxController().
							showHeavyWeightDialog("/niobe/legion/client/fxml/debug/Debug.fxml",
												  "Watchdog",
												  Modality.NONE,
												  true);

					this.addModuleCommunicator(debugController);
				} else
				{
					debugController.writeMessage("reconnected " + LocalDateTime.now().toString());
				}
			}
			catch (IOException e)
			{
				Logger.exception(LegionLogger.STDERR, e);
			}
		}


	}

	@Override
	public void run()
	{
		try
		{
			this.openStream();
			super.run();
		}
		catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	private void openStream() throws IOException
	{
		XmlStanza stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.setName("legion:stream");
		stanza.setSequenceId(this.localStanzaSequenceId);
		stanza.putAttribute("xmlns:" + this.getNamespace(), "'" + this.getNamespaceURI() + "'");

		this.write(stanza);
	}

	private void sendClient() throws IOException
	{
		XmlStanza stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.setName("legion:client");
		stanza.setSequenceId(this.localStanzaSequenceId++);
		stanza.putAttribute("name", ClientCommunicator.CLIENT_NAME);
		stanza.putAttribute("version", ClientCommunicator.CLIENT_VERSION);
		this.write(stanza);

		stanza = new XmlStanza();
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.setName("legion:features");
		this.write(stanza);

		for (String feature : ClientCommunicator.CLIENT_FEATURES)
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
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		stanza.setName("legion:client");
		this.write(stanza);
	}

	@Override
	public final void consumeStartElement(final XmlStanza currentStanza) throws IOException
	{
		String stanzaName = currentStanza.getName();

		switch (stanzaName)
		{
			case "legion:stream":
				this.sendClient();
			case "legion:server":
				this.serverName = currentStanza.getAttribute("name");
				this.serverVersion = currentStanza.getAttribute("version");

				break;
			case "legion:proceedtls":
				if (this.clientAcceptedFromServer && !this.tlsEstablished && this.keyStorePassword != null &&
					this.keyStoreFile != null && !this.keyStoreFile.isEmpty())
				{
					if (this.keyStorePassword.isEmpty())
					{
						Logger.warn(LegionLogger.STDOUT, "WARNING: Your key store password is empty!!!");
					}
					try
					{
						this.tlsEstablished =
								this.setSslSocket(this.keyStorePassword, this.keyStoreFile, this.cipherSuites);
						if (this.tlsEstablished)
						{
							this.openStream();
							this.resetReader();
							this.connectTask = null;
							if (Client.getFxController().getCurrentController() instanceof ConnectController)
							{
								Client.getFxController().loadMask("/niobe/legion/client/fxml/connect/Login.fxml");
							} else if (!(Client.getFxController().getCurrentController() instanceof LoginController))
							{
								Client.showRelogin();
							}
						} else
						{
							this.decline("proceedtls", "client don't trust server certificate");
						}
					}
					catch (Exception e)
					{
						Logger.exception(LegionLogger.STDERR, e);
						if (!this.tlsEstablished)
						{
							this.decline("proceedtls", "tls is not established, see client error log");
						}
					}
				} else
				{
					if (this.tlsEstablished)
					{
						this.decline("proceedtls", "tls is allready established");
					} else
					{
						this.decline("proceedtls", "there is no valid server certificate selected");
					}
				}
				break;
			case "legion:query":
				if (this.isAuthenficated())
				{
					if ("result".equals(currentStanza.getAttribute("type")) &&
						currentStanza.getSequenceId() != null && currentStanza.getSequenceId().matches("\\d+"))
					{
						long id = Long.parseLong(currentStanza.getSequenceId());

						if (!this.cachedStanzas.containsKey(id))
						{
							this.cachedStanzas.put(id, new ArrayList<XmlStanza>());
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
				break;
		}
	}

	@Override
	public final void consumeCharacters(final XmlStanza currentStanza) throws IOException
	{
		String stanzaName = currentStanza.getName();

		switch (stanzaName)
		{
			case "legion:feature":
				if (this.isStackAt(1, "legion:features") && this.isStackAt(2, "legion:server"))
				{
					this.serverFeatures.add(currentStanza.getValue());
				}
				break;
			case "legion:mechanism":
				if (this.isStackAt(1, "legion:mechanisms") && this.isStackAt(2, "legion:server"))
				{
					this.serverAuthMechanisms.add(currentStanza.getValue());
				}
				break;
			case "legion:failure":
				if (this.saslClient != null)
				{
					this.loginFailed(ClientCommunicator.SERVER_AUTH_ERR);
					Logger.error(LegionLogger.AUTH, currentStanza.getValue());
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
							this.cachedStanzas.get(id).add(currentStanza);
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
			case "legion:client":
				if (this.isAcceptAt(1) && currentStanza.getValue() != null)
				{
					String[] identification = currentStanza.getValue().split(":");
					if (identification.length == 2 && ClientCommunicator.CLIENT_NAME.equals(identification[0]) &&
						ClientCommunicator.CLIENT_VERSION.equals(identification[1]))
					{
						this.clientAcceptedFromServer = true;
					}
				}
				break;
			case "legion:server":
				if (this.clientAcceptedFromServer)
				{
					if (this.serverName == null || this.serverName.isEmpty() || this.serverVersion == null ||
						this.serverVersion.isEmpty())
					{
						this.decline("legion:server", "Invalid Server Identification received");
					} else if (this.blacklistedServersRegex != null && !this.blacklistedServersRegex.isEmpty())
					{
						String checkValue = this.serverName + ":" + this.serverVersion;
						if (checkValue.matches(this.blacklistedServersRegex))
						{
							this.decline("legion:server", "Server is blacklisted on client");
						}
					} else
					{
						XmlStanza stanza = new XmlStanza();
						stanza.setEventType(XMLStreamConstants.CHARACTERS);
						stanza.setName("legion:server");
						stanza.setSequenceId(this.localStanzaSequenceId++);
						stanza.setValue(this.serverName + ":" + this.serverVersion);
						this.accept(stanza);

						if (ClientCommunicator.CLIENT_FEATURES.contains("starttls") &&
							this.serverFeatures.contains("starttls") && !tlsEstablished)
						{
							stanza = new XmlStanza();
							stanza.setEmptyElement(true);
							stanza.setEventType(XMLStreamConstants.START_ELEMENT);
							stanza.setName("legion:starttls");
							this.write(stanza);
						} else
						{
							this.connectTask = null;
							if (Client.getFxController().getCurrentController() instanceof ConnectController)
							{
								Client.getFxController().loadMask("/niobe/legion/client/fxml/connect/Login.fxml");
							} else if (!(Client.getFxController().getCurrentController() instanceof LoginController))
							{
								Client.showRelogin();
							}
						}
					}
				}

				break;
			case "legion:challenge":
				if (this.saslClient != null && !this.saslClient.isComplete())
				{
					try
					{
						byte[] response = this.saslClient.evaluateChallenge(Base64.decode(currentStanza.getValue()));
						if (!this.saslClient.isComplete())
						{
							XmlStanza stanza = new XmlStanza();
							stanza.setName("legion:response");
							stanza.setSequenceId(this.localStanzaSequenceId++);
							stanza.setEventType(XMLStreamConstants.CHARACTERS);
							stanza.setValue(Base64.encodeBytes(response));
							this.write(stanza);
						}
					}
					catch (SaslException e)
					{
						this.loginFailed(ClientCommunicator.CLIENT_AUTH_ERR);
						Logger.exception(LegionLogger.AUTH, e);
					}
				}
				break;
			case "legion:group":
				if ("true".equals(currentStanza.getAttribute("active")))
				{
					this.groupName = currentStanza.getAttribute("name");
				}
				break;
			case "legion:groupRight":
				if (this.isStackAt(1, "legion:group"))
				{
					String name = currentStanza.getAttribute("name");
					boolean active = "true".equals(currentStanza.getAttribute("active"));

					GroupRightEntity rightEntity = new GroupRightEntity();
					rightEntity.setName(name);
					rightEntity.setActive(active);

					if (this.groupRights == null)
					{
						this.groupRights = new ArrayList<GroupRightEntity>();
					}

					this.groupRights.add(rightEntity);
				}
				break;
			case "legion:success":
				this.serverSideAuthenficated = true;

				if (this.isAuthenficated() && checkRight(LegionRight.LOGIN))
				{
					this.doLogin();
				} else
				{
					this.loginFailed(CLIENT_AUTH_ERR);
				}
				break;
			case "legion:stream":
				if (!this.isCloseRequested)
				{
					this.isCloseRequested = true;
					XmlStanza stanza = new XmlStanza();
					stanza.setName("legion:stream");
					stanza.setSequenceId(this.localStanzaSequenceId++);
					stanza.setEventType(XMLStreamConstants.END_ELEMENT);
					this.write(stanza);
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
						long id = Long.parseLong(currentStanza.getSequenceId());

						if (this.cachedStanzas.containsKey(id))
						{
							this.cachedStanzas.get(id).add(currentStanza);
						}
					}
				}
				break;
			case "legion:query":
				if (this.isAuthenficated())
				{
					String id = currentStanza.getSequenceId();
					if (id != null && id.matches("\\d+"))
					{
						DatasetReceiver retriever = this.databaseRetrievers.remove(Long.parseLong(id));
						List<XmlStanza> datasets = this.cachedStanzas.remove(Long.parseLong(id));

						if (retriever != null && datasets != null)
						{
							retriever.setAll(XmlMarshaller.unmarshal(datasets));
						}
					}
				}
				break;
		}
	}

	public void login(final String user, final char[] password) throws IOException
	{
		this.userName = user;

		this.saslClient = Sasl.createSaslClient(this.authMechanisms,
												user,
												"legion",
												this.serverName + "_" + this.serverVersion,
												new HashMap<String, Object>(),
												callbacks -> {
													for (Callback callback : callbacks)
													{
														if (callback instanceof NameCallback)
														{
															((NameCallback) callback).setName(user);
														} else if (callback instanceof PasswordCallback)
														{
															((PasswordCallback) callback).setPassword(password);
														} else if (callback instanceof RealmCallback)
														{
															((RealmCallback) callback)
																	.setText(ClientCommunicator.this.serverName + "_" +
																			 ClientCommunicator.this.serverVersion);
														} else if (callback instanceof TextInputCallback)
														{
															((TextInputCallback) callback).setText(null);

															ObservableValue<String> observableValue =
																	Client.getFxController()
																		  .showLightweightTextInputDialog(((TextInputCallback) callback)
																												  .getPrompt(),
																										  true);

															observableValue
																	.addListener((observable, oldValue, newValue) -> {
																		new Thread(() -> {
																			((TextInputCallback) callback)
																					.setText(newValue);
																			synchronized (ClientCommunicator.this)
																			{
																				ClientCommunicator.this.notify();
																			}
																		}).start();
																	});


															while (((TextInputCallback) callback).getText() == null)
															{
																synchronized (ClientCommunicator.this)
																{
																	try
																	{
																		ClientCommunicator.this.wait();
																	}
																	catch (InterruptedException e)
																	{
																		Logger.exception(LegionLogger.STDERR, e);
																	}
																}
															}
														} else
														{
															Logger.debug(LegionLogger.AUTH,
																		 "Unknown Callback: " +
																		 callback.getClass().toString());
														}
													}
												});

		byte[] initialResponse = new byte[0];
		if (this.saslClient.hasInitialResponse())
		{
			initialResponse = this.saslClient.evaluateChallenge(new byte[0]);
		}

		XmlStanza stanza = new XmlStanza();
		stanza.setName("legion:auth");
		stanza.setSequenceId(this.localStanzaSequenceId++);
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.putAttribute("mechanism", this.saslClient.getMechanismName());
		if (initialResponse != null && initialResponse.length > 0)
		{
			stanza.putAttribute("initialResponse", Base64.encodeBytes(initialResponse));
		}
		stanza.setEmptyElement(true);
		this.write(stanza);
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
		this.sslSocket =
				(SSLSocket) sslFactory.createSocket(this.socket, "localhost", this.socket.getLocalPort(), true);
		SSLParameters sslParameters = this.sslSocket.getSSLParameters();
		sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
		this.sslSocket.setSSLParameters(sslParameters);

		if (cipherSuites != null && cipherSuites.length > 0)
		{
			this.sslSocket.setEnabledCipherSuites(cipherSuites);
		}

		this.sslSocket.setUseClientMode(true);
		try
		{
			this.sslSocket.startHandshake();
			this.replaceStreamsWithSslStreams();

			return true;
		}
		catch (SSLException e)
		{
			Logger.exception(LegionLogger.TLS, e);

			// client don't know certificate
			X509Certificate[] chain = trustManager.getChain();

			if (chain != null)
			{
				for (X509Certificate cert : chain)
				{
					// ask user to accept certificate
					CertificateController certController = (CertificateController) Client.getFxController().
							loadMask("/niobe/legion/client/fxml/connect/Certificate.fxml");

					try
					{
						cert.checkValidity();
						certController.setCertificateData(this.socket.getInetAddress().getCanonicalHostName(),
														  cert,
														  keystore,
														  keyStoreFile,
														  passphrase);
					}
					catch (CertificateExpiredException | CertificateNotYetValidException e1)
					{
						Logger.exception(LegionLogger.TLS, e1);
						certController.setCertificateExpired(this.socket.getInetAddress().getCanonicalHostName(), cert);
					}
				}

				this.close();
			}
			return false;
		}
	}

	public boolean isAuthenficated()
	{
		return (this.saslClient != null && this.saslClient.isComplete() && this.serverSideAuthenficated);
	}

	private void doLogin() throws IOException
	{
		if (Client.getFxController().getCurrentController() instanceof LoginController)
		{
			Client.getFxController().loadMask("/niobe/legion/client/fxml/tab/TabView.fxml");
		} else
		{
			Client.hideRelogin();
		}
		Client.getFxController().setUserButton(this.getUserName());
	}

	public void logout() throws IOException
	{
		if (this.isAuthenficated())
		{
			this.userName = null;
			this.saslClient = null;
			this.groupRights = new ArrayList<>();
			this.serverSideAuthenficated = false;

			XmlStanza stanza = new XmlStanza();
			stanza.setName("legion:deAuth");
			stanza.setSequenceId(this.localStanzaSequenceId++);
			stanza.setEventType(XMLStreamConstants.START_ELEMENT);
			stanza.setEmptyElement(true);
			this.write(stanza);

			Client.getFxController().setUserButton(null);
			Client.getFxController().loadMask("/niobe/legion/client/fxml/connect/Login.fxml");
		}
	}

	public <T> void getDataset(Class<T> datasetType,
							   DatasetReceiver<T> retriever,
							   String queryName,
							   HashMap<String, Object> parameters) throws IOException
	{
		this.databaseRetrievers.put(this.localStanzaSequenceId, retriever);
		XmlStanza stanza = new XmlStanza();
		stanza.setName("legion:query");
		stanza.setSequenceId(this.localStanzaSequenceId++);
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.putAttribute("action", "get");
		stanza.putAttribute("class", datasetType.getCanonicalName());
		stanza.putAttribute("namedQuery", queryName);
		stanza.putAttribute("where", this.createWhereStringFromMap(parameters));
		this.write(stanza);

		stanza = new XmlStanza();
		stanza.setName("legion:query");
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		this.write(stanza);
	}

	public <T> void setDataset(T dataset, DatasetReceiver<T> retriever) throws IOException
	{
		this.databaseRetrievers.put(this.localStanzaSequenceId, retriever);
		XmlStanza stanza = new XmlStanza();
		stanza.setName("legion:query");
		stanza.setSequenceId(this.localStanzaSequenceId);
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.putAttribute("action", "set");
		this.write(stanza);

		List<XmlStanza> stanzas = XmlMarshaller.marshal(dataset, this.localStanzaSequenceId++);
		for (XmlStanza datasetStanza : stanzas)
		{
			this.write(datasetStanza);
		}

		stanza = new XmlStanza();
		stanza.setName("legion:query");
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		this.write(stanza);
	}

	public <T> void removeDataset(T dataset, DatasetReceiver<T> retriever) throws IOException
	{
		this.databaseRetrievers.put(this.localStanzaSequenceId, retriever);
		XmlStanza stanza = new XmlStanza();
		stanza.setName("legion:query");
		stanza.setSequenceId(this.localStanzaSequenceId++);
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		stanza.putAttribute("action", "delete");
		this.write(stanza);

		List<XmlStanza> stanzas = XmlMarshaller.marshal(dataset, this.localStanzaSequenceId++);
		for (XmlStanza datasetStanza : stanzas)
		{
			this.write(datasetStanza);
		}

		stanza = new XmlStanza();
		stanza.setName("legion:query");
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		this.write(stanza);
	}

	private void loginFailed(int sender)
	{
		Object controller = Client.getFxController().getCurrentController();
		if (controller instanceof LoginController)
		{
			((LoginController) controller).loginFailed(sender);
		} else
		{
			Client.reloginFailed(sender);
		}
	}

	public String getUserName()
	{
		if (this.isAuthenficated())
		{
			return this.userName;
		}
		return null;
	}

	public boolean checkRight(IRight right)
	{
		if (this.groupRights != null)
		{
			for (GroupRightEntity rightEntity : this.groupRights)
			{
				if (Communicator.validateRight(rightEntity, right))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void socketUnexpectedClosed()
	{
		Platform.runLater(Client::getCommunicator);
	}
}
