package cuina.legion.client;

import cuina.legion.client.gui.MainController;
import cuina.legion.client.gui.connect.ConnectController;
import cuina.legion.client.gui.connect.ReconnectController;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;
import cuina.legion.shared.module.ModuleLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
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

	private static ClientCommunicator communicator;
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
			if(Client.communicator == null || Client.communicator.isClosed())
			{
				final ConnectController connectController = (Client.getFxController()
						.getCurrentController() instanceof ConnectController) ?
						(ConnectController) Client
								.getFxController().getCurrentController() :
						null;

				if(connectController != null)
				{
					connectController.getProgressLabelProperty().bind(
							Client.clientCommService.messageProperty());

				} else
				{
					try
					{
						Client.javaFxController.showFatDialog(
								"/cuina/legion/client/fxml/connect/Reconnect.fxml",
								"Verbindung verloren");
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}

				super.start();
			}
		}

		;
	};
	private static MainController      javaFxController;
	private static ModuleLoader        moduleLoader;
	private static ReconnectController reconnectController;

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	/**
	 * Gibt den Kommunikation-Thread zwischen Client und Server zurück
	 *
	 * @return
	 */
	public static ClientCommunicator getCommunicator()
	{
		if(Client.communicator == null || Client.communicator.isClosed())
		{
			Client.clientCommService.restart();
		}

		return Client.communicator;
	}

	public static void bindConnectionProgress(StringProperty property,
			ReconnectController reconnectController)
	{
		property.bind(Client.clientCommService.messageProperty());
		Client.reconnectController = reconnectController;
	}

	/**
	 * Gibt den Haupt-Controller der JavaFX-GUI zurück
	 *
	 * @return
	 */
	public static MainController getFxController()
	{

		return Client.javaFxController;
	}

	public static void close() throws SocketException, IOException
	{
		if(Client.getCommunicator() != null && !Client.getCommunicator().isClosed())
		{
			Client.getCommunicator().close();
		}
		Platform.exit();
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		String configName = "client.ini";
		if(this.getParameters().getRaw().size() == 1)
		{
			configName = this.getParameters().getRaw().get(0);
		}

		Properties properties = new Properties();
		try
		{
			File config = new File(configName);
			if(config.exists())
			{
				properties.load(new InputStreamReader(new FileInputStream(configName), "UTF-8"));
				Client.port = properties.getProperty("port", "5242");
				Client.server = properties.getProperty("server", "localhost");
				Client.keyStoreFile = properties.getProperty("keystore_file", null);
				Client.keyStorePassword = properties.getProperty("keystore_password", null);

				String cipherSuitesString = properties.getProperty("cipher_suites");
				if(cipherSuitesString != null && !cipherSuitesString.isEmpty())
				{
					Client.cipherSuites = cipherSuitesString.split(" ");
				}

				Client.authMechanisms = properties.getProperty("auth_mechanisms", "PLAIN");
				Client.blacklistedServersRegex = properties.getProperty(
						"blacklisted_servers_regex", "");
				String logPath = properties.getProperty("log_config_file");
				if(logPath != null && !logPath.isEmpty() && new File(logPath).exists())
				{
					System.setProperty("log4j.configurationFile", logPath);
				} else if(System.getProperty("log4j.configurationFile") == null)
				{
					final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
					try
					{
						ctx.setConfigLocation(Client.class.getResource(
								"/cuina/legion/client/logger.xml").toURI());
					} catch (URISyntaxException e)
					{
						e.printStackTrace();
					}
				}

				String modulePath = properties.getProperty("module_path", null);
				Client.moduleLoader = ModuleLoader.getModuleLoader(ClientCommunicator.CLIENT_NAME,
						ClientCommunicator.CLIENT_VERSION, modulePath);

			} else
			{
				Logger.error(LegionLogger.STDERR, "No config file found. Stop!");
				System.exit(0);
			}

			URL location = this.getClass().getResource("/cuina/legion/client/fxml/Main.fxml");

			FXMLLoader loader = new FXMLLoader(location);
			Parent root = (Parent) loader.load();

			Client.javaFxController = loader.getController();

			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(
					this.getClass().getResource("/cuina/legion/client/css/theme.css")
							.toExternalForm());
			stage.setTitle("Legion Client");
			stage.initStyle(StageStyle.UNDECORATED);
			Client.javaFxController.setStage(stage);
			stage.setScene(scene);
			stage.show();

			Client.getCommunicator();

		} catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	public static class CommunicatorTask extends Task<ClientCommunicator>
	{
		private CommunicatorTask()
		{
		}

		@Override
		protected ClientCommunicator call() throws Exception
		{
			this.updateMessage("Verbindung mit Server wird hergestellt ...");
			ClientCommunicator communicator = null;
			int trials = 1;

			while(communicator == null && trials < Client.MAX_CONNECT_TRIALS)
			{
				try
				{
					Socket socket = new Socket(InetAddress.getByName(Client.server),
							Integer.parseInt(Client.port));

					Logger.info(LegionLogger.STDOUT, "Client: Connect");

					communicator = new ClientCommunicator(socket, Client.authMechanisms,
							Client.blacklistedServersRegex, Client.keyStoreFile,
							Client.keyStorePassword, Client.cipherSuites);
					trials = 1;
				} catch (IOException | NumberFormatException e)
				{
					Logger.error(LegionLogger.STDERR,
							"Verbindung konnte nicht hergestellt werden.");

					this.updateMessage("Verbindung mit Server wird hergestellt (Versuch "
							+ ++trials + " von " + Client.MAX_CONNECT_TRIALS + ") ...");

					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e2)
					{
					}
				}
			}
			if(communicator == null && trials == Client.MAX_CONNECT_TRIALS)
			{
				this.updateMessage(
						"Der Server ist nicht erreichbar.\nBitte beenden Sie die Anwendung und\nversuchen Sie es später erneut.");
			} else
			{
				Client.communicator = communicator;

				if(Client.reconnectController != null)
				{
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							Client.reconnectController.close();
							Client.reconnectController = null;
						}
					});
				}

				communicator.run();
			}
			return communicator;
		}
	}
}
