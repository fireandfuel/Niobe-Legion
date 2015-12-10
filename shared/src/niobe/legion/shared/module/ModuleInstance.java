package niobe.legion.shared.module;

import niobe.legion.shared.data.IRight;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

/**
 * Module state transitions:<br>
 * Load module: UNINITIALIZED -> LOADING -> INITIALIZED<br>
 * Unload module: INITIALIZED -> UNLOADING -> UNINITIALIZED<br>
 * Start module: INITIALIZED <-> STARTING -> RUNNING<br>
 * Stop module: RUNNING <-> STOPPING -> INITIALIZED<br>
 * Forced stop module: RUNNING -> TERMINATED<br>
 * Module with missing dependencies: UNINITIALIZED -> MISSING_DEPENDENCIES<br>
 * Module with conflicts: UNINITIALIZED -> IN_CONFLICT<br>
 * Module with database conflict (server module only): UNINITIALIZED -> DATABASE_CONFLICT
 */
public abstract class ModuleInstance
{
	public final static int UNINITIALIZED        = 0;
	public final static int LOADING              = 1;
	public final static int INITIALIZED          = 2;
	public final static int STARTING             = 3;
	public final static int RUNNING              = 4;
	public final static int STOPPING             = 5;
	public final static int UNLOADING            = 6;
	public final static int MISSING_DEPENDENCIES = -1;
	public final static int IN_CONFLICT          = -2;
	public final static int DATABASE_CONFLICT    = -3;
	public final static int TERMINATED           = -4;

	private String[] dependencies;
	private String[] conflicts;
	private String   name;
	private String   version;
	private String   contact;
	private String   description;

	private   URLClassLoader loader;
	protected IModule        module;

	private File   moduleFile;
	private String moduleClass;

	protected int state;

	public ModuleInstance(String dependencies,
						  String conflicts,
						  String name,
						  String version,
						  String contact,
						  String description,
						  File moduleFile,
						  String moduleClass)
	{
		this.dependencies = dependencies.split(", ");
		this.conflicts = conflicts.split(", ");
		this.name = name;
		this.version = version;
		this.contact = contact;
		this.description = description;
		this.moduleFile = moduleFile;
		this.moduleClass = moduleClass;
		this.state = ModuleInstance.UNINITIALIZED;
	}

	/**
	 * @param loader the loader to set,
	 *               only if this instance's loader is not set and parameter is not null
	 */
	public void setLoader(URLClassLoader loader)
	{
		if (loader != null && this.loader == null)
		{
			this.loader = loader;
		}
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(IModule module)
	{
		this.module = module;
		this.state = ModuleInstance.INITIALIZED;
	}

	/**
	 * @return the depends
	 */
	public String[] getDependencies()
	{
		return this.dependencies;
	}

	/**
	 * @return the conflicts
	 */
	public String[] getConflicts()
	{
		return this.conflicts;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return this.version;
	}

	/**
	 * @return the contact
	 */
	public String getContact()
	{
		return this.contact;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @return the moduleFile
	 */
	public File getModuleFile()
	{
		return this.moduleFile;
	}

	/**
	 * @return the moduleClass
	 */
	public String getModuleClass()
	{
		return this.moduleClass;
	}

	public synchronized void start()
	{
		if (this.module != null && this.state == ModuleInstance.INITIALIZED)
		{
			this.state = ModuleInstance.STARTING;
			if (this.module.startModule())
			{
				this.state = ModuleInstance.RUNNING;
			} else
			{
				this.state = INITIALIZED;
			}
		}
	}

	public abstract void stop();

	public synchronized void unload()
	{
		if (this.loader != null)
		{
			if (this.state == ModuleInstance.INITIALIZED)
			{
				this.state = ModuleInstance.UNLOADING;
				try
				{
					this.loader.close();
					this.state = ModuleInstance.UNINITIALIZED;
				}
				catch (IOException e)
				{
					Logger.exception(LegionLogger.MODULE, e);
				}
			}
		}
	}

	public synchronized int getState()
	{
		return this.state;
	}

	public synchronized void setState(int state)
	{
		if (this.state == ModuleInstance.UNINITIALIZED && (state == ModuleInstance.MISSING_DEPENDENCIES ||
														   state == ModuleInstance.IN_CONFLICT ||
														   state == ModuleInstance.DATABASE_CONFLICT))
		{
			this.state = state;
		}
	}

	protected String getStateAsString()
	{
		switch (this.state)
		{
			case TERMINATED:
				return "terminated";
			case MISSING_DEPENDENCIES:
				return "missing dependencies";
			case IN_CONFLICT:
				return "in conflict";
			case DATABASE_CONFLICT:
				return "database in conflict";
			case UNINITIALIZED:
				return "uninitialized";
			case INITIALIZED:
				return "initialized";
			case STARTING:
				return "starting";
			case RUNNING:
				return "running";
			case STOPPING:
				return "stopping";
			default:
				return "not set";
		}
	}

	public IRight[] getRights()
	{
		if (this.module == null || this.state == MISSING_DEPENDENCIES || this.state == IN_CONFLICT ||
			this.state == UNINITIALIZED || this.state == DATABASE_CONFLICT || this.state == TERMINATED)
		{
			return null;
		}
		return this.module.getRights();
	}

	@Override
	public String toString()
	{
		return "ModuleInstance name=" + this.name + ", version=" + this.version + ", class=" + this.moduleClass +
			   ", state=" + this.getStateAsString();
	}
}
