package niobe.legion.shared.module;

import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ModuleLoader<MI extends ModuleInstance>
{
	protected final static int MODULE_API_LEVEL = 1;
	protected final List<MI> moduleInstances = new ArrayList<MI>();

	private       String hostModuleName;
	private       String hostModuleVersion;
	private final String modulePath;


	private final List<ModuleDependencyItem<MI>> moduleDependencyItems;

	protected ModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
	{
		this.hostModuleName = hostModuleName;
		this.hostModuleVersion = hostModuleVersion;
		this.modulePath = modulePath;

		this.moduleDependencyItems = new ArrayList<ModuleDependencyItem<MI>>();

		File[] moduleFiles = this.findModuleFiles(this.modulePath);
		for (File moduleFile : moduleFiles)
		{
			try
			{
				Logger.debug(LegionLogger.MODULE, "load module " + moduleFile.getName());
				this.loadModule(moduleFile);
			}
			catch (IOException e)
			{
				Logger.exception(LegionLogger.MODULE, e);
			}
		}

		moduleInstances.stream().filter(this::checkRequirements).forEach(instance -> {
			try
			{
				this.initModule(instance);
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| IOException e)
			{
				instance.setState(ModuleInstance.UNINITIALIZED);
				Logger.exception(LegionLogger.MODULE, e);
			}
		});

		this.startModules();
	}

	protected abstract void initModule(MI instance) throws
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

	protected abstract void loadModule(File jarFile) throws IOException;

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
			module.setState(ModuleInstance.MISSING_DEPENDENCIES);
			return false;
		}
		if (conflict.isPresent())
		{
			Logger.error(LegionLogger.MODULE,
						 "Conflict check for plugin " + module.getName() + " failed. It conflicts with " + conflict +
						 " !");
			module.setState(ModuleInstance.IN_CONFLICT);
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
	 * @param entry should be formatted in the manner name_of_plugin$version or name_of_plugin$version$minimal_status,
	 * version is a regex
	 * @return true, plugin found; false, else
	 */
	private Predicate<String> check = (String entry) -> {
		String[] values = entry.split("\\$");

		String name = values[0];
		String versionRegex;
		if (values.length >= 2)
		{
			versionRegex = values[1];
		} else
		{
			versionRegex = null;
		}

		Optional<String> found = moduleInstances.stream().filter(module -> module.getName().equals(name) &&
																		   versionRegex != null &&
																		   module.getVersion().matches(versionRegex) &&
																		   module.getState() !=
																		   ModuleInstance.MISSING_DEPENDENCIES &&
																		   module.getState() !=
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
		List<String> moduleNames = moduleInstances.stream().map(ModuleInstance::getName).collect(Collectors.toList());

		return moduleNames;
	}

	private PriorityQueue<MI> buildLoadingQueue()
	{
		return null;
	}

	private PriorityQueue<List<MI>> buildStartingQueue()
	{
		return null;
	}

	protected void buildDependencies()
	{
		moduleInstances.forEach(module -> moduleDependencyItems.add(new ModuleDependencyItem<MI>(module)));

		moduleDependencyItems.forEach(this::setDependencyItem);
	}

	private void setDependencyItem(ModuleDependencyItem<MI> item)
	{
		List<String> dependencies = Arrays.asList(item.getDependencies());

		moduleDependencyItems.stream().forEach(miModuleDependencyItem -> {
			if (dependencies.contains(miModuleDependencyItem.getName() + "$" + miModuleDependencyItem.getVersion()))
			{
				item.addChild(miModuleDependencyItem);
				miModuleDependencyItem.addParent(item);
			}
		});
	}

	protected void startModules()
	{
		this.moduleInstances.forEach(MI::start);
	}

	public void startModule(String moduleName)
	{
		Optional<MI> instance = this.getModule(moduleName);
		if (instance.isPresent())
		{
			instance.get().start();
		}
	}

	public void stopModule(String moduleName)
	{
		Optional<MI> instance = this.getModule(moduleName);
		if (instance.isPresent())
		{
			instance.get().stop();
		}
	}

	public void unloadModule(String moduleName)
	{
		Optional<MI> instance = this.getModule(moduleName);
		if (instance.isPresent())
		{
			instance.get().unload();
			moduleInstances.remove(instance.get());
		}
	}

	private Optional<MI> getModule(String moduleName)
	{
		return moduleInstances.stream().filter(instance -> moduleName.equals(instance.getName())).findFirst();
	}
}
