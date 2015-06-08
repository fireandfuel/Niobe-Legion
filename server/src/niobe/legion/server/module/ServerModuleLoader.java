package niobe.legion.server.module;

import niobe.legion.shared.Communicator;
import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.module.IModule;
import niobe.legion.shared.module.ModuleInstance;
import niobe.legion.shared.module.ModuleLoader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by nk on 03.11.14.
 */
public class ServerModuleLoader extends ModuleLoader
{
	private static ServerModuleLoader moduleLoader;

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

	protected void initModule(ModuleInstance instance) throws
													   ClassNotFoundException,
													   InstantiationException,
													   IllegalAccessException,
													   IOException
	{
		URLClassLoader loader = new URLClassLoader(new URL[]{
				instance.getModuleFile().toURI().toURL()});

		Class<?> clazz = loader.loadClass(instance.getModuleClass());

		Object obj = clazz.newInstance();

		if (obj instanceof IModule)
		{
			IModule module = (IModule) obj;

			ICommunicator communicator = module.getCommunicator();
			if (communicator != null && communicator.getNamespace() != null && !communicator.getNamespace().isEmpty() &&
				communicator.getNamespaceURI() != null && !communicator.getNamespaceURI().isEmpty())
			{
				Communicator.addModuleCommunicator(communicator);
			}

			instance.setModule(module);

			//			new ModuleDatabaseManager(instance);
		} else
		{
			loader.close();
		}
	}

}