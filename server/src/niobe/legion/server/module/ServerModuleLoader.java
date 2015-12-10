package niobe.legion.server.module;

import niobe.legion.server.Server;
import niobe.legion.shared.Communicator;
import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.data.IRight;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.module.ModuleInstance;
import niobe.legion.shared.module.ModuleLoader;
import niobe.legion.shared.module.ModuleRightManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class ServerModuleLoader extends ModuleLoader<ServerModuleInstance>
{
	private static ServerModuleLoader moduleLoader;
	private static List<String> usedDatabases = new ArrayList<String>();

	private ServerModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
	{
		super(hostModuleName, hostModuleVersion, modulePath);
	}

	public static ServerModuleLoader getModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
	{
		if (hostModuleName != null && hostModuleVersion != null)
		{
			if (moduleLoader == null)
			{
				moduleLoader = new ServerModuleLoader(hostModuleName, hostModuleVersion, modulePath);
			}
			return moduleLoader;
		}
		return null;
	}

	protected void initModule(ServerModuleInstance instance) throws
															 ClassNotFoundException,
															 InstantiationException,
															 IllegalAccessException,
															 IOException
	{
		instance.setState(ModuleInstance.LOADING);
		URLClassLoader loader = new URLClassLoader(new URL[]{
				instance.getModuleFile().toURI().toURL()});

		Class<?> clazz = loader.loadClass(instance.getModuleClass());

		if (IServerModule.class.isAssignableFrom(clazz))
		{
			IServerModule module = (IServerModule) clazz.newInstance();

			instance.setModule(module);
			instance.setLoader(loader);

			String databaseName = module.getDatabaseName();
			if (databaseName != null && !databaseName.isEmpty())
			{
				if (usedDatabases.contains(databaseName))
				{
					instance.setState(ModuleInstance.DATABASE_CONFLICT);
					loader.close();
					return;
				} else
				{
					instance.setDatabase(module.initDatabase());
				}
			}

			if ( module.getNamespace() != null && !module.getNamespace().isEmpty() &&
				module.getNamespaceURI() != null && !module.getNamespaceURI().isEmpty())
			{
				Server.getCommunicators().forEach(module::newCommunicator);
			}

			IRight[] rights = module.getRights();
			if (rights != null && rights.length > 0)
			{
				ModuleRightManager.addRights(module.getRights());
			}
		} else
		{
			instance.setState(ModuleInstance.UNINITIALIZED);
			Logger.warn(LegionLogger.MODULE, instance.getName() + ": module class " + clazz.getCanonicalName() +
											 " does not implement IModule! Unload module ...");
			loader.close();
		}
	}

	protected void loadModule(File jarFile) throws IOException
	{
		if (jarFile.exists())
		{
			JarInputStream jis = new JarInputStream(new FileInputStream(jarFile));
			Manifest manifest = jis.getManifest();

			if (manifest != null && manifest.getEntries() != null)
			{
				Attributes attributes = manifest.getMainAttributes();
				String apiLevel = attributes.getValue("Module-API-Level");
				if (apiLevel != null && apiLevel.matches("\\d") && Integer.parseInt(apiLevel) == MODULE_API_LEVEL)
				{
					ServerModuleInstance instance = new ServerModuleInstance(attributes.getValue("Module-Dependencies"),
																			 attributes.getValue("Module-Conflicts"),
																			 attributes.getValue("Module-Name"),
																			 attributes.getValue("Module-Version"),
																			 attributes.getValue("Module-Author"),
																			 attributes.getValue("Module-Description"),
																			 jarFile,
																			 attributes.getValue("Module-Class"));

					if (instance.getName() != null && !instance.getName().isEmpty() && instance.getVersion() != null &&
						!instance.getVersion().isEmpty() && instance.getModuleClass() != null &&
						!instance.getModuleClass().isEmpty())
					{
						moduleInstances.add(instance);
					}
				}
			}
		}
	}

}