package niobe.legion.client.module;

import niobe.legion.client.Client;
import niobe.legion.shared.module.ModuleInstance;
import niobe.legion.shared.module.ModuleRightManager;

import java.io.File;

public class ClientModuleInstance extends ModuleInstance
{
	public ClientModuleInstance(String dependencies,
								String conflicts,
								String name,
								String version,
								String contact,
								String description,
								File moduleFile,
								String moduleClass)
	{
		super(dependencies, conflicts, name, version, contact, description, moduleFile, moduleClass);
	}

	@Override
	public void stop()
	{
		if (this.module != null && this.state == ModuleInstance.RUNNING)
		{
			this.state = ModuleInstance.STOPPING;
			if (this.module.stopModule())
			{
				if (this.module.getNamespaceURI() != null && !this.module.getNamespaceURI().isEmpty())
				{
					Client.getCommunicator().removeModuleCommunicator(this.module.getNamespaceURI());
				}
				if (this.module.getRights() != null && this.module.getRights().length > 0)
				{
					ModuleRightManager.removeRights(this.module.getRights());
				}
				this.state = ModuleInstance.INITIALIZED;
			}
		}
	}

	public IModuleTab[] getMainTabs()
	{
		if (!(this.module instanceof IClientModule) || this.state == MISSING_DEPENDENCIES || this.state == IN_CONFLICT ||
			this.state == UNINITIALIZED)
		{
			return null;
		}
		return ((IClientModule) this.module).getMainTabs();
	}
}
