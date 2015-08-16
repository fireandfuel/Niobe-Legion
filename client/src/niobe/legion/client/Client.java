package niobe.legion.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import niobe.legion.client.gui.MainController;
import niobe.legion.client.gui.connect.CertificateController;
import niobe.legion.client.gui.connect.ConnectController;
import niobe.legion.client.gui.connect.ReconnectController;
import niobe.legion.client.gui.connect.ReloginController;
import niobe.legion.client.module.ClientModuleLoader;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class Client extends Application
{
	public static final int MAX_CONNECT_TRIALS = 10;

	static String   port                    = "";
	static String   server                  = "";
	static String   keyStoreFile            = null;
	static String   keyStorePassword        = null;
	static String[] cipherSuites            = null;
	static String   authMechanisms          = "";
	static String   blacklistedServersRegex = "";
	static boolean debug;

	private static ClientCommunicator communicator;

	// Service to start a task which creates the connection to the client
	private static Service<ClientCommunicator> clientCommService = new Service<ClientCommunicator>()
	{
		@Override
		protected Task<ClientCommunicator> createTask()
		{
			return new CommunicatorTask();
		}

		@Override
		public void start()
		{
			// if communicator is null or closed
			if (Client.communicator == null || Client.communicator.isClosed())
			{

				if (Client.reloginController != null)
				{
					Platform.runLater(() -> {
						Client.reloginController.close();
						Client.reloginController = null;
					});
				}

				// get the ConnectionController if loaded
				final ConnectController connectController =
						(Client.getFxController().getCurrentController() instanceof ConnectController) ?
						(ConnectController) Client.getFxController().getCurrentController() : null;

				if (connectController != null)
				{
					connectController.getProgressLabelProperty().bind(Client.clientCommService.messageProperty());

				} else
				{
					// if no ConnectionController is loaded, show the reconnect dialog
					try
					{
						Client.javaFxController.showHeavyheightDialog("/niobe/legion/client/fxml/connect/Reconnect.fxml",
																	  "Verbindung verloren",
																	  Modality.APPLICATION_MODAL,
																	  false);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				super.start();
			}
		}
	};

	private static MainController      javaFxController;
	private static ClientModuleLoader  moduleLoader;
	private static ReconnectController reconnectController;
	private static ReloginController   reloginController;

	public static void main(String[] args)
	{

		// launch Application with JavaFx
		Application.launch(args);
	}

	/**
	 * Gibt den Kommunikation-Thread zwischen Client und Server zurück
	 *
	 * @return ClientCommunicator
	 */
	public static ClientCommunicator getCommunicator()
	{
		if (Client.communicator == null || Client.communicator.isClosed())
		{
			Client.clientCommService.restart();
		}

		return Client.communicator;
	}

	public static void bindConnectionProgress(StringProperty property, ReconnectController reconnectController)
	{
		property.bind(Client.clientCommService.messageProperty());
		Client.reconnectController = reconnectController;
	}

	public static void showRelogin()
	{
		if (Client.reloginController == null)
		{
			try
			{
				Client.reloginController = (ReloginController) Client.getFxController().showHeavyheightDialog(
						"/niobe/legion/client/fxml/connect/Relogin.fxml",
						"Authenfizierung",
						Modality.APPLICATION_MODAL,
						false);
			}
			catch (IOException e)
			{
				Logger.exception(LegionLogger.STDERR, e);
			}
		}
	}

	public static void reloginFailed(int sender)
	{
		if (Client.reloginController != null)
		{
			Client.reloginController.loginFailed(sender);
		}
	}

	public static void hideRelogin()
	{
		if (Client.reloginController != null)
		{
			Client.reloginController.close();
			Client.reloginController = null;
		}
	}

	/**
	 * Gibt den Haupt-Controller der JavaFX-GUI zurück
	 *
	 * @return MainController
	 */
	public static MainController getFxController()
	{

		return Client.javaFxController;
	}

	/**
	 * Closes the communicator if not null or closed
	 *
	 * @throws IOException
	 */
	public static void close() throws IOException
	{
		if (Client.getCommunicator() != null && !Client.getCommunicator().isClosed())
		{
			Client.getCommunicator().close();
		}
		Platform.exit();
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		// set client configuration file name
		String configName = "client.ini";
		if (this.getParameters().getRaw().size() == 1)
		{
			// override client configuration file name
			configName = this.getParameters().getRaw().get(0);
		}

		// read client configuration file, if exists
		Properties properties = new Properties();
		try
		{
			File config = new File(configName);
			if (config.exists())
			{
				properties.load(new InputStreamReader(new FileInputStream(configName), "UTF-8"));
				Client.port = properties.getProperty("port", "5242");
				Client.server = properties.getProperty("server", "localhost");
				Client.keyStoreFile = properties.getProperty("keystore_file", null);
				Client.keyStorePassword = properties.getProperty("keystore_password", null);

				String cipherSuitesString = properties.getProperty("cipher_suites");
				if (cipherSuitesString != null && !cipherSuitesString.isEmpty())
				{
					Client.cipherSuites = cipherSuitesString.split(" ");
				}

				Client.authMechanisms = properties.getProperty("auth_mechanisms", "PLAIN");
				Client.blacklistedServersRegex = properties.getProperty("blacklisted_servers_regex", "");
				String logPath = properties.getProperty("log_config_file");
				if (logPath != null && !logPath.isEmpty() && new File(logPath).exists())
				{
					System.setProperty("log4j.configurationFile", logPath);
				} else if (System.getProperty("log4j.configurationFile") == null)
				{
					final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
					try
					{
						ctx.setConfigLocation(Client.class.getResource("/niobe/legion/client/logger.xml").toURI());
					}
					catch (URISyntaxException e)
					{
						e.printStackTrace();
					}
				}

				String debugProperty = properties.getProperty("debug", "false").toLowerCase();
				Client.debug = "true".equals(debugProperty) || "yes".equals(debugProperty) || "1".equals(debugProperty);

				String modulePath = properties.getProperty("module_path", null);
				Client.moduleLoader = ClientModuleLoader
						.getModuleLoader(ClientCommunicator.CLIENT_NAME, ClientCommunicator.CLIENT_VERSION, modulePath);

			} else
			{
				Logger.error(LegionLogger.STDERR, "No config file found. Stop!");
				System.exit(0);
			}

			// load Main pane
			URL location;
			if (System.getProperty("os.name").toLowerCase().contains("mac"))
			{
				location = this.getClass().getResource("/niobe/legion/client/fxml/MainOSX.fxml");
			} else
			{
				location = this.getClass().getResource("/niobe/legion/client/fxml/Main.fxml");
			}

			FXMLLoader loader = new FXMLLoader(location);
			Parent root = loader.load();

			// set Main pane controller
			Client.javaFxController = loader.getController();

			// show main pane
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets()
				 .add(this.getClass().getResource("/niobe/legion/client/css/theme.css").toExternalForm());
			stage.setTitle("Legion Client");
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(scene);
			Client.javaFxController.setStage(stage);
			stage.show();

			// try to start communicator
			Client.getCommunicator();

			// start all client modules
			List<String> moduleNames = moduleLoader.getModuleNames();
			if (moduleNames != null)
			{
				moduleNames.forEach(moduleLoader::startModule);
			}

		}
		catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	// task to start the communicator
	public static class CommunicatorTask extends Task<ClientCommunicator>
	{
		CommunicatorTask()
		{
			// empty constructor, can not be instantiated from other classes
		}

		@Override
		protected ClientCommunicator call() throws Exception
		{
			this.updateTitle("CommunicatorTask");

			this.updateMessage("Verbindung mit Server wird hergestellt ...");
			ClientCommunicator communicator = null;
			int trials = 1;

			// try to start the communicator
			while (communicator == null && trials < Client.MAX_CONNECT_TRIALS)
			{
				try
				{
					Socket socket = new Socket(InetAddress.getByName(Client.server), Integer.parseInt(Client.port));

					Logger.info(LegionLogger.STDOUT, "Client: Connect");

					communicator = new ClientCommunicator(socket,
														  Client.authMechanisms,
														  Client.blacklistedServersRegex,
														  Client.keyStoreFile,
														  Client.keyStorePassword,
														  Client.cipherSuites);
					trials = 1;
				}
				catch (IOException | NumberFormatException e)
				{
					Logger.error(LegionLogger.STDERR, "Verbindung konnte nicht hergestellt werden.");

					this.updateMessage("Verbindung mit Server wird hergestellt (Versuch " + ++trials + " von " +
									   Client.MAX_CONNECT_TRIALS + ") ...");

					try
					{
						Thread.sleep(5000);
					}
					catch (InterruptedException e2)
					{
					}
				}
			}

			// Server not reachable after Client.MAX_CONNECT_TRIALS trials
			if (communicator == null && trials == Client.MAX_CONNECT_TRIALS)
			{
				this.updateMessage(
						"Der Server ist nicht erreichbar.\nBitte beenden Sie die Anwendung und\nversuchen Sie es später erneut.");
				this.failed();
			} else
			{
				Client.communicator = communicator;

				// hide reconnect controller if visible
				if (Client.reconnectController != null)
				{
					Platform.runLater(() -> {
						Client.reconnectController.close();
						Client.reconnectController = null;
					});
				}

				// run the communictator if CertificateController is not loaded
				if (communicator != null)
				{
					if ((Client.getFxController().getCurrentController() instanceof CertificateController))
					{
						return null;
					} else
					{
						new Thread(communicator, "ClientCommunicator").start();
						this.succeeded();
					}
				}
			}

			return communicator;
		}
	}

	public static boolean isDebug()
	{
		return Client.debug;
	}
}
