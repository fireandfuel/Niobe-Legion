package niobe.legion.client.module;

import niobe.legion.shared.Communicator;
import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.module.IModule;
import niobe.legion.shared.module.ModuleInstance;
import niobe.legion.shared.module.ModuleLoader;
import niobe.legion.shared.module.ModuleRightManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by nk on 04.11.14.
 */
public class ClientModuleLoader extends ModuleLoader
{
	private static ClientModuleLoader moduleLoader;

	private ClientModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
	{
		super(hostModuleName, hostModuleVersion, modulePath);
	}

	public static ClientModuleLoader getModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
	{
		if (hostModuleName != null && hostModuleVersion != null)
		{
			if (moduleLoader == null)
			{
				moduleLoader = new ClientModuleLoader(hostModuleName, hostModuleVersion, modulePath);
			}
			return moduleLoader;
		}
		return null;
	}

	protected void initModule(ModuleInstance instance) throws
													   ClassNotFoundException,
													   InstantiationException,
													   IllegalAccessException,
													   IOException
	{
		URLClassLoader loader = new URLClassLoader(new URL[]{
				instance.getModuleFile().toURI().toURL()});

		Class<?> clazz = loader.loadClass(instance.getModuleClass());

		if (IModule.class.isAssignableFrom(clazz))
		{
			IModule module = (IModule) clazz.newInstance();

			ICommunicator communicator = module.getCommunicator();
			if (communicator != null && communicator.getNamespace() != null && !communicator.getNamespace().isEmpty() &&
				communicator.getNamespaceURI() != null && !communicator.getNamespaceURI().isEmpty())
			{
				Communicator.addModuleCommunicator(communicator);
			}

			if (module.getRights() != null)
			{
				ModuleRightManager.addRights(module.getRights());
			}

			instance.setModule(module);
		} else
		{
			Logger.warn(LegionLogger.MODULE, instance.getName() + ": module class " + clazz.getCanonicalName() +
											 " does not implement IModule! Unload module ...");
			loader.close();
		}
	}
}
