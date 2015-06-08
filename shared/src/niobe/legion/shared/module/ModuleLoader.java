package niobe.legion.shared.module;

import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public abstract class ModuleLoader
{
	protected static final List<ModuleInstance> MODULE_INSTANCES = new ArrayList<ModuleInstance>();

	private       String hostModuleName;
	private       String hostModuleVersion;
	private final String modulePath;

	protected ModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
	{
		this.hostModuleName = hostModuleName;
		this.hostModuleVersion = hostModuleVersion;
		this.modulePath = modulePath;

		File[] moduleFiles = this.findModuleFiles(this.modulePath);
		for (File moduleFile : moduleFiles)
		{
			try
			{
				this.loadModule(moduleFile);
			}
			catch (IOException e)
			{
				Logger.exception(LegionLogger.MODULE, e);
			}
		}

		ModuleLoader.MODULE_INSTANCES.stream().filter(this::checkRequirements).forEach(instance -> {
			try
			{
				this.initModule(instance);
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| IOException e)
			{
				Logger.exception(LegionLogger.MODULE, e);
			}
		});
	}

	protected abstract void initModule(ModuleInstance instance) throws
																ClassNotFoundException,
																InstantiationException,
																IllegalAccessException,
																IOException;

	private File[] findModuleFiles(String rootFolder)
	{
		if (rootFolder != null)
		{
			File root = new File(rootFolder);
			if (root.exists())
			{
				return root.listFiles(pathname -> pathname.getName().endsWith(".jar"));
			}
		}

		// Folder doesn't exists ...
		return new File[0];
	}

	private void loadModule(File jarFile) throws IOException
	{
		if (jarFile.exists())
		{
			JarInputStream jis = new JarInputStream(new FileInputStream(jarFile));
			Manifest manifest = jis.getManifest();

			if (manifest != null && manifest.getEntries() != null)
			{
				Attributes attributes = manifest.getMainAttributes();
				ModuleInstance instance = new ModuleInstance(attributes.getValue("Module-Dependencies"),
															 attributes.getValue("Module-Conflicts"),
															 attributes.getValue("Module-Name"),
															 attributes.getValue("Module-Version"),
															 attributes.getValue("Created-By"),
															 attributes.getValue("Module-Description"),
															 jarFile,
															 attributes.getValue("Module-Class"));

				if (instance.getName() != null && !instance.getName().isEmpty() && instance.getVersion() != null &&
					!instance.getVersion().isEmpty() && instance.getModuleClass() != null &&
					!instance.getModuleClass().isEmpty())
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
		Optional<String> depends = this.checkDependencies(module.getDependencies());
		Optional<String> conflict = this.checkConflicts(module.getConflicts());

		if (depends.isPresent())
		{
			Logger.error(LegionLogger.MODULE,
						 "Dependency check for plugin " + module.getName() + " failed. It depends on " + depends +
						 " !");
			module.setStatus(ModuleInstance.MISSING_DEPENDENCIES);
			return false;
		}
		if (conflict.isPresent())
		{
			Logger.error(LegionLogger.MODULE,
						 "Conflict check for plugin " + module.getName() + " failed. It conflicts with " + conflict +
						 " !");
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
	private Optional<String> checkDependencies(String[] list)
	{
		return Arrays.stream(list).filter(check.negate()).findFirst();
	}

	/**
	 * Checks for conflicts of the plugin
	 *
	 * @param list
	 * @return null, no conflict; first entry which conflicts, else
	 */
	private Optional<String> checkConflicts(String[] list)
	{
		return Arrays.stream(list).filter(check.negate()).findFirst();
	}

	/**
	 * Check whether the plugin is in the loadedPlugin list
	 *
	 * @param entry should be formatted in the manner name_of_plugin$version,
	 * version is a regex
	 * @return true, plugin found; false, else
	 */
	private Predicate<String> check = (String entry) -> {
		String[] values = entry.split("\\$");

		String name = values[0];
		String versionRegex;
		if (values.length == 2)
		{
			versionRegex = values[1];
		} else
		{
			versionRegex = null;
		}

		Optional<String> found = MODULE_INSTANCES.stream().filter(module -> module.getName().equals(name) &&
																			versionRegex != null &&
																			module.getVersion().matches(versionRegex) &&
																			module.getStatus() !=
																			ModuleInstance.MISSING_DEPENDENCIES &&
																			module.getStatus() !=
																			ModuleInstance.IN_CONFLICT)
												 .map(ModuleInstance::getName).findFirst();

		if (found.isPresent())
		{
			return true;
		}

		return this.hostModuleName != null && this.hostModuleVersion != null &&
			   this.hostModuleName.equals(this.hostModuleName) && this.hostModuleVersion.matches(versionRegex);

	};

	public List<String> getModuleNames()
	{
		List<String> moduleNames =
				ModuleLoader.MODULE_INSTANCES.stream().map(ModuleInstance::getName).collect(Collectors.toList());

		return moduleNames;
	}

	public void startModule(String moduleName)
	{
		Optional<ModuleInstance> instance = this.getModule(moduleName);
		if (instance.isPresent())
		{
			instance.get().start();
		}
	}

	public void stopModule(String moduleName)
	{
		Optional<ModuleInstance> instance = this.getModule(moduleName);
		if (instance.isPresent())
		{
			instance.get().stop();
		}
	}

	public void unloadModule(String moduleName)
	{
		Optional<ModuleInstance> instance = this.getModule(moduleName);
		if (instance.isPresent())
		{
			instance.get().unload();
			MODULE_INSTANCES.remove(instance.get());
		}
	}

	private Optional<ModuleInstance> getModule(String moduleName)
	{
		return MODULE_INSTANCES.stream().filter(instance -> moduleName.equals(instance.getName())).findFirst();
	}
}
