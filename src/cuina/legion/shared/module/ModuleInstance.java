package cuina.legion.shared.module;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

import cuina.legion.shared.Communicator;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;

/**
 * Module status:<br>
 * UNINITIALIZED <-> INITIALIZED <-> RUNNING<br>
 * UNINITIALIZED -> MISSING_DEPENDENCIES<br>
 * UNINITIALIZED -> IN_CONFLICT
 */
public class ModuleInstance
{
	public final static int UNINITIALIZED = 0;
	public final static int INITIALIZED = 1;
	public final static int RUNNING = 2;
	public final static int MISSING_DEPENDENCIES = -1;
	public final static int IN_CONFLICT = -2;

	private String[] dependencies;
	private String[] conflicts;
	private String name;
	private String version;
	private String contact;
	private String description;

	private URLClassLoader loader;
	private IModule module;

	private File moduleFile;
	private String moduleClass;

	private int status;

	public ModuleInstance(String dependencies, String conflicts, String name, String version,
			String contact, String description, File moduleFile, String moduleClass)
	{
		this.dependencies = dependencies.split(", ");
		this.conflicts = conflicts.split(", ");
		this.name = name;
		this.version = version;
		this.contact = contact;
		this.description = description;
		this.moduleFile = moduleFile;
		this.moduleClass = moduleClass;
		this.status = ModuleInstance.UNINITIALIZED;
	}

	/**
	 * @return the loader
	 */
	public URLClassLoader getLoader()
	{
		return this.loader;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	public void setModule(IModule module)
	{
		this.module = module;
		this.status = ModuleInstance.INITIALIZED;
	}

	/**
	 * @return the module
	 */
	public IModule getModule()
	{
		return this.module;
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

	public void start()
	{
		if(this.module != null && this.status == ModuleInstance.INITIALIZED
				&& this.module.startModule())
		{
			this.status = ModuleInstance.RUNNING;
		}
	}

	public void stop()
	{
		if(this.module != null && this.status == ModuleInstance.RUNNING)
		{
			this.module.stopModule();
			if(this.module.getCommunicator() != null
					&& this.module.getCommunicator().getNamespaceURI() != null
					&& !this.module.getCommunicator().getNamespaceURI().isEmpty())
			{
				Communicator.removeModuleCommunicator(this.module.getCommunicator()
						.getNamespaceURI());
			}
			this.status = ModuleInstance.INITIALIZED;
		}
	}

	public void unload()
	{
		if(this.loader != null)
		{
			if(this.status == ModuleInstance.RUNNING)
			{
				this.stop();
			}
			if(this.status == ModuleInstance.INITIALIZED)
			{
				try
				{
					this.loader.close();
					this.status = ModuleInstance.UNINITIALIZED;
				} catch (IOException e)
				{
					Logger.exception(LegionLogger.MODULE, e);
				}
			}
		}
	}

	public int getStatus()
	{
		return this.status;
	}

	public void setStatus(int status)
	{
		if(this.status == ModuleInstance.UNINITIALIZED
				&& status == ModuleInstance.MISSING_DEPENDENCIES
				&& status == ModuleInstance.IN_CONFLICT)
		{
			this.status = status;
		}
	}

	private String getStatusAsString()
	{
		switch(this.status)
		{
			case MISSING_DEPENDENCIES:
				return "MISSING_DEPENDENCIES";
			case IN_CONFLICT:
				return "IN_CONFLICT";
			case UNINITIALIZED:
				return "UNINITIALIZED";
			case INITIALIZED:
				return "INITIALIZED";
			case RUNNING:
				return "RUNNING";
			default:
				return "not set";
		}
	}

	@Override
	public String toString()
	{
		return "ModuleInstance name=" + this.name + ", version=" + this.version + ", class="
				+ this.moduleClass + ", status=" + this.getStatusAsString();
	}
}
