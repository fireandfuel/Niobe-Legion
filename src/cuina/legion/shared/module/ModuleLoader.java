package cuina.legion.shared.module;

import cuina.legion.shared.Communicator;
import cuina.legion.shared.ICommunicator;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;

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

public class ModuleLoader
{
	private static final List<ModuleInstance> MODULE_INSTANCES = new ArrayList<ModuleInstance>();

	private static ModuleLoader moduleLoader;

	private final String hostModuleName;
	private final String hostModuleVersion;
	private final String modulePath;

	private ModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
	{
		this.hostModuleName = hostModuleName;
		this.hostModuleVersion = hostModuleVersion;
		this.modulePath = modulePath;

		File[] moduleFiles = this.findModuleFiles(this.modulePath);
		for(File moduleFile : moduleFiles)
		{
			try
			{
				this.loadModule(moduleFile);
			} catch (IOException e)
			{
				Logger.exception(LegionLogger.MODULE, e);
			}
		}

		for(ModuleInstance instance : ModuleLoader.MODULE_INSTANCES)
		{
			if(this.checkRequirements(instance))
			{
				try
				{
					this.initModule(instance);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| IOException e)
				{
					Logger.exception(LegionLogger.MODULE, e);
				}
			}
		}
	}

	public static ModuleLoader getModuleLoader(String hostModuleName, String hostModuleVersion,
			String modulePath)
	{
		if(hostModuleName != null && hostModuleVersion != null)
		{
			if(ModuleLoader.moduleLoader == null)
			{
				ModuleLoader.moduleLoader = new ModuleLoader(hostModuleName, hostModuleVersion,
						modulePath);
			}
			return ModuleLoader.moduleLoader;
		}
		return null;
	}

	private void initModule(ModuleInstance instance) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, IOException
	{
		URLClassLoader loader = new URLClassLoader(new URL[] { instance.getModuleFile().toURI()
				.toURL() });

		Class<?> clazz = loader.loadClass(instance.getModuleClass());

		Object obj = clazz.newInstance();

		if(obj instanceof IModule)
		{
			IModule module = (IModule) obj;

			ICommunicator communicator = module.getCommunicator();
			if(communicator != null && communicator.getNamespace() != null
					&& !communicator.getNamespace().isEmpty()
					&& communicator.getNamespaceURI() != null
					&& !communicator.getNamespaceURI().isEmpty())
			{
				Communicator.addModuleCommunicator(communicator);
			}

			// plugin.setProvider(this);
			// String[] dbStructures = plugin.getDatabaseTableStructures();

			instance.setModule(module);
		} else
		{
			loader.close();
		}
	}

	public File[] findModuleFiles(String rootFolder)
	{

		if(rootFolder != null)
		{
			File root = new File(rootFolder);
			if(root.exists())
			{
				return root.listFiles(pathname -> pathname.getName().endsWith(".jar"));
			}
		}

		// Folder doesn't exists ...
		return new File[0];
	}

	private void loadModule(File jarFile) throws IOException
	{
		if(jarFile.exists())
		{
			JarInputStream jis = new JarInputStream(new FileInputStream(jarFile));
			Manifest manifest = jis.getManifest();

			if(manifest != null && manifest.getEntries() != null)
			{
				Attributes attributes = manifest.getMainAttributes();
				ModuleInstance instance = new ModuleInstance(
						attributes.getValue("Module-Dependencies"),
						attributes.getValue("Module-Conflicts"),
						attributes.getValue("Module-Name"), attributes.getValue("Module-Version"),
						attributes.getValue("Created-By"),
						attributes.getValue("Module-Description"), jarFile,
						attributes.getValue("Module-Class"));

				if(instance.getName() != null && !instance.getName().isEmpty()
						&& instance.getVersion() != null && !instance.getVersion().isEmpty()
						&& instance.getModuleClass() != null
						&& !instance.getModuleClass().isEmpty())
				{
					ModuleLoader.MODULE_INSTANCES.add(instance);
				}
			}
		}
	}

	/**
	 * Check all requirements of the plugin interface
	 *
	 * @param module
	 * @return true, all requirements fulfilled; false else
	 */
	private boolean checkRequirements(ModuleInstance module)
	{
		String depends = this.checkDependencies(module.getDependencies());
		String conflict = this.checkConflicts(module.getConflicts());

		if(depends != null)
		{
			Logger.error(LegionLogger.MODULE, "Dependency check for plugin " + module.getName()
					+ " failed. It depends on " + depends + " !");
			module.setStatus(ModuleInstance.MISSING_DEPENDENCIES);
			return false;
		}
		if(conflict != null)
		{
			Logger.error(LegionLogger.MODULE, "Conflict check for plugin " + module.getName()
					+ " failed. It conflicts with " + conflict + " !");
			module.setStatus(ModuleInstance.IN_CONFLICT);
			return false;
		}

		return true;
	}

	/**
	 * Checks the dependencies for the plugin
	 *
	 * @param list
	 * @return null, dependencies fulfilled; first entry which is missing, else
	 */
	private String checkDependencies(String[] list)
	{
		for(String entry : list)
		{
			if(!this.check(entry))
			{
				return entry;
			}
		}

		return null;
	}

	/**
	 * Checks for conflicts of the plugin
	 *
	 * @param list
	 * @return null, no conflict; first entry which conflicts, else
	 */
	private String checkConflicts(String[] list)
	{
		for(String entry : list)
		{
			if(this.check(entry))
			{
				return entry;
			}
		}

		return null;
	}

	/**
	 * Check whether the plugin is in the loadedPlugin list
	 *
	 * @param entry
	 *            should be formatted in the manner name_of_plugin$version,
	 *            version is a regex
	 * @return true, plugin found; false, else
	 */
	private boolean check(String entry)
	{
		String[] values = entry.split("\\$");

		String name = values[0];
		String versionRegex = null;
		if(values.length == 2)
		{
			versionRegex = values[1];
		}

		for(ModuleInstance module : ModuleLoader.MODULE_INSTANCES)
		{
			if(module.getName().equals(name))
			{
				if(versionRegex != null)
				{
					return module.getVersion().matches(versionRegex)
							&& module.getStatus() != ModuleInstance.MISSING_DEPENDENCIES
							&& module.getStatus() != ModuleInstance.IN_CONFLICT;
				} else
				{
					return true;
				}
			}
		}

		if(name.equals(this.hostModuleName) && this.hostModuleVersion.matches(versionRegex))
		{
			return true;
		}

		return false;
	}

	public String[] getModuleNames()
	{
		List<String> moduleNames = new ArrayList<String>();

		for(ModuleInstance instance : ModuleLoader.MODULE_INSTANCES)
		{
			moduleNames.add(instance.getName());
		}
		return (moduleNames.isEmpty()) ? null : moduleNames.toArray(new String[moduleNames.size()]);
	}

	public void startModule(String moduleName)
	{
		ModuleInstance instance = this.getModule(moduleName);
		if(instance != null)
		{
			instance.start();
		}
	}

	public void stopModule(String moduleName)
	{
		ModuleInstance instance = this.getModule(moduleName);
		if(instance != null)
		{
			instance.stop();
		}
	}

	public void unloadModule(String moduleName)
	{
		ModuleInstance instance = this.getModule(moduleName);
		if(instance != null)
		{
			instance.unload();
		}
	}

	private ModuleInstance getModule(String moduleName)
	{
		if(moduleName != null)
		{
			for(ModuleInstance instance : ModuleLoader.MODULE_INSTANCES)
			{
				if(moduleName.equals(instance.getName()))
				{
					return instance;
				}
			}
		}
		return null;
	}
}
