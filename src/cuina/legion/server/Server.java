package cuina.legion.server;

import cuina.legion.shared.Communicator;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;
import cuina.legion.shared.module.ModuleLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
	private static ModuleLoader            moduleLoader;
	private static Database                DATABASE;
	private        ServerSocket            serverSocket;
	private        ExecutorService         connectionPool;
	private        ArrayList<Communicator> connectionList;
	private        boolean                 closeRequest;

	public Server(int port, String authMechanism, String blacklistedClientsRegex,
			final String keyStoreFile,
			final String keyStorePassword, final String[] cipherSuites) throws XMLStreamException
	{
		try
		{
			this.serverSocket = new ServerSocket(port);
			this.connectionPool = Executors.newCachedThreadPool();
			this.connectionList = new ArrayList<Communicator>();
			Logger.info(LegionLogger.STDOUT, "Server is ready");

			while(!this.closeRequest)
			{
				if(this.serverSocket != null)
				{

					Socket socket = this.serverSocket.accept();
					if(socket != null)
					{
						Logger.info(LegionLogger.STDOUT, "Accept new Connection");
						Communicator communicator = new ServerCommunicator(socket, authMechanism,
								blacklistedClientsRegex, keyStoreFile,
								keyStorePassword, cipherSuites);

						this.connectionList.add(communicator);
						this.connectionPool.execute(communicator);
					} else
					{
						break;
					}

				}

			}

		} catch (IOException e)
		{
			System.exit(0);
		}

	}

	public static void main(String[] args)
	{
		String port = "", keyStoreFile = null, keyStorePassword = null, databaseType = null, maxConnections = null;
		String[] cipherSuites = null;
		String authMechanisms = "";
		String blacklistedClientsRegex = "";

		String configName = "server.ini";
		if(args.length == 1)
		{
			configName = args[0];
		}

		Properties properties = new Properties();
		try
		{
			File config = new File(configName);
			if(config.exists())
			{
				properties.load(new InputStreamReader(new FileInputStream(configName), "UTF-8"));
				port = properties.getProperty("port", "5242");
				keyStoreFile = properties.getProperty("keystore_file", null);
				databaseType = properties.getProperty("database_type", null);
				keyStorePassword = properties.getProperty("keystore_password", null);
				maxConnections = properties.getProperty("sql_max_connections", "1");
				blacklistedClientsRegex = properties.getProperty("blacklisted_clients_regex", "");

				String cipherSuitesString = properties.getProperty("cipher_suites");
				if(cipherSuitesString != null && !cipherSuitesString.isEmpty())
				{
					cipherSuites = cipherSuitesString.split(" ");
				}

				String logPath = properties.getProperty("log_config_file");
				if(logPath != null && !logPath.isEmpty() && new File(logPath).exists())
				{
					System.setProperty("log4j.configurationFile", logPath);
				} else if(System.getProperty("log4j.configurationFile") == null)
				{
					final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
					try
					{
						ctx.setConfigLocation(Server.class.getResource(
								"/cuina/legion/server/logger.xml").toURI());
					} catch (URISyntaxException e)
					{
						e.printStackTrace();
					}
				}

				String modulePath = properties.getProperty("module_path", null);
				Server.moduleLoader = ModuleLoader.getModuleLoader(ServerCommunicator.SERVER_NAME,
						ServerCommunicator.SERVER_VERSION, modulePath);

				if(databaseType.equalsIgnoreCase("mariadb"))
				{
					String host = properties.getProperty("sql_host");
					String sqlPort = properties.getProperty("sql_port");
					String user = properties.getProperty("sql_user");
					String password = properties.getProperty("sql_password");
					String database = properties.getProperty("sql_database");
					String encryptionPassword = properties.getProperty("encryption_key");

					Server.DATABASE = new Database(databaseType, host, sqlPort, database, user,
							password, encryptionPassword, maxConnections);
				} else if(databaseType.equalsIgnoreCase("sqlite"))
				{
					String sqliteDatabase = properties.getProperty("sqlite_database_file");
					String encryptionPassword = properties.getProperty("encryption_key");

					Server.DATABASE = new Database(databaseType, sqliteDatabase,
							encryptionPassword);
				}

				authMechanisms = properties.getProperty("auth_mechanisms", "PLAIN");

			} else
			{
				System.err.println("ERROR: No config file found. Stop!");
				System.exit(0);
			}

			try
			{
				new Server(Integer.parseInt(port), authMechanisms,
						blacklistedClientsRegex, keyStoreFile, keyStorePassword, cipherSuites);

			} catch (NumberFormatException e)
			{
				Logger.exception(LegionLogger.STDERR, e);
			} catch (XMLStreamException e)
			{
				Logger.exception(LegionLogger.STDERR, e);
			}
		} catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		} catch (SQLException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	public static Database getDatabase()
	{
		return Server.DATABASE;
	}
}