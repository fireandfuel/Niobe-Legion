/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (Server.java) is part of Niobe Legion (module niobe-legion-server).
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

package niobe.legion.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.xml.stream.XMLStreamException;
import niobe.legion.server.module.ServerModuleLoader;
import niobe.legion.shared.Communicator;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class Server
{
    private static final String PERSISTENCE_NAME = "niobe_legion";

    private static ServerModuleLoader moduleLoader;
    private static LegionDatabase DATABASE;

    private static final ServerCommunicatorThreadFactory COMMUNICATOR_THREAD_FACTORY = new ServerCommunicatorThreadFactory();
    private static final List<Communicator> COMMUNICATORS = new ArrayList<Communicator>();

    private final ExecutorService connectionPool;
    private final BlockingQueue<Runnable> connectionList;


    private ServerSocket serverSocket;

    private boolean closeRequest;

    public Server(short port, String authMechanism, String blacklistedClientsRegex, final String keyStoreFile,
                  final String keyStorePassword, final String[] cipherSuites, short maxConnections) throws
                                                                                                    XMLStreamException
    {
        if(maxConnections == 0)
        {
            connectionList = new SynchronousQueue<Runnable>();
            connectionPool = new ThreadPoolExecutor(0,
                                                    Integer.MAX_VALUE,
                                                    60L,
                                                    TimeUnit.SECONDS,
                                                    this.connectionList,
                                                    COMMUNICATOR_THREAD_FACTORY,
                                                    new ThreadPoolExecutor.AbortPolicy());
        } else
        {
            connectionList = new ArrayBlockingQueue<Runnable>(Math.max(maxConnections, 10));
            connectionPool = new ThreadPoolExecutor(Math.min(maxConnections, 10),
                                                    Math.max(maxConnections, 10),
                                                    60L,
                                                    TimeUnit.SECONDS,
                                                    this.connectionList,
                                                    COMMUNICATOR_THREAD_FACTORY,
                                                    new ThreadPoolExecutor.AbortPolicy());
        }
        try
        {
            this.serverSocket = new ServerSocket(port);

            List<String> moduleNames = Server.moduleLoader.getModuleNames();
            if(moduleNames != null)
            {
                moduleNames.forEach(moduleLoader::startModule);
            }

            Logger.info(LegionLogger.STDOUT, "Server is ready");

            while(!this.closeRequest)
            {
                if(this.serverSocket != null)
                {
                    Socket socket = this.serverSocket.accept();
                    if(socket != null)
                    {
                        Logger.info(LegionLogger.STDOUT, "Accept new Connection");
                        Communicator communicator = new ServerCommunicator(socket,
                                                                           authMechanism,
                                                                           blacklistedClientsRegex,
                                                                           keyStoreFile,
                                                                           keyStorePassword,
                                                                           cipherSuites);
                        COMMUNICATORS.add(communicator);
                        connectionPool.execute(communicator);
                    } else
                    {
                        break;
                    }
                }
            }
        } catch(IOException e)
        {
            e.printStackTrace();
            Logger.exception(LegionLogger.STDERR, e);
            System.exit(0);
        }
    }

    public static void main(String[] args)
    {
        short port = 5242;
        String keyStoreFile = null, keyStorePassword = null, databaseType = null;
        String minConnections = null, maxConnections = null;
        String[] cipherSuites = null;
        String authMechanisms = "";
        String blacklistedClientsRegex = "";
        short maxThreadPoolSize = 100;

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
                String portString = properties.getProperty("port", "5242");
                if(portString.matches("\\d+"))
                {
                    port = Short.parseShort(portString);
                }
                keyStoreFile = properties.getProperty("keystore_file", null);
                databaseType = properties.getProperty("database_type", null);
                keyStorePassword = properties.getProperty("keystore_password", null);
                minConnections = properties.getProperty("sql_min_connections", "1");
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
                        ctx.setConfigLocation(Server.class.getResource("/niobe/legion/server/logger.xml").toURI());
                    } catch(URISyntaxException e)
                    {
                        e.printStackTrace();
                    }
                }

                if(databaseType.equalsIgnoreCase("mariadb") || databaseType.equalsIgnoreCase("h2-server"))
                {
                    String host = properties.getProperty("sql_host");
                    String sqlPort = properties.getProperty("sql_port");
                    String user = properties.getProperty("sql_user");
                    String password = properties.getProperty("sql_password");
                    String database = properties.getProperty("sql_database");
                    String encryptionPassword = properties.getProperty("encryption_key");

                    Server.DATABASE = LegionDatabase.init(databaseType,
                                                          PERSISTENCE_NAME,
                                                          host,
                                                          sqlPort,
                                                          database,
                                                          user,
                                                          password,
                                                          encryptionPassword,
                                                          minConnections,
                                                          maxConnections);
                } else if(databaseType.equalsIgnoreCase("h2-embedded"))
                {
                    String h2Database = properties.getProperty("sql_database_file");
                    String user = properties.getProperty("sql_user");
                    String password = properties.getProperty("sql_password");
                    String encryptionPassword = properties.getProperty("encryption_key");

                    Server.DATABASE = LegionDatabase.init(databaseType,
                                                          PERSISTENCE_NAME,
                                                          h2Database,
                                                          user,
                                                          password,
                                                          encryptionPassword,
                                                          minConnections,
                                                          maxConnections);
                }

                authMechanisms = properties.getProperty("auth_mechanisms", "PLAIN");
                String maxThreadPoolSizeString = properties.getProperty("max_connections", "10");
                if(maxThreadPoolSizeString.matches("\\d+"))
                {
                    maxThreadPoolSize = Short.parseShort(maxThreadPoolSizeString);
                }

                String modulePath = properties.getProperty("module_path", null);
                Server.moduleLoader = ServerModuleLoader
                        .getModuleLoader(ServerCommunicator.SERVER_NAME, ServerCommunicator.SERVER_VERSION, modulePath);
            } else
            {
                System.err.println("ERROR: No config file found. Stop!");
                System.exit(0);
            }

            try
            {
                new Server(port,
                           authMechanisms,
                           blacklistedClientsRegex,
                           keyStoreFile,
                           keyStorePassword,
                           cipherSuites,
                           maxThreadPoolSize);

            } catch(NumberFormatException e)
            {
                Logger.exception(LegionLogger.STDERR, e);
            } catch(XMLStreamException e)
            {
                Logger.exception(LegionLogger.STDERR, e);
            }
        } catch(IOException e)
        {
            Logger.exception(LegionLogger.STDERR, e);
        } catch(SQLException e)
        {
            Logger.exception(LegionLogger.STDERR, e);
        }
    }

    public static LegionDatabase getDatabase()
    {
        return Server.DATABASE;
    }

    public static List<Communicator> getCommunicators()
    {
        return COMMUNICATORS;
    }
}