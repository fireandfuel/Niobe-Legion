package niobe.legion.client.module;

import niobe.legion.shared.module.IModule;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
public interface IClientModule extends IModule
{
	IModuleTab[] getMainTabs();
}
