/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ClientModuleLoader.java) is part of Niobe Legion (module niobe-legion-client).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.client.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import niobe.legion.client.Client;
import niobe.legion.client.gui.tab.TabViewController;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.module.ModuleInstance;
import niobe.legion.shared.module.ModuleLoader;
import niobe.legion.shared.module.ModuleRightManager;

public class ClientModuleLoader extends ModuleLoader<ClientModuleInstance>
{
    private static ClientModuleLoader moduleLoader;

    private ClientModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
    {
        super(hostModuleName, hostModuleVersion, modulePath);
    }

    public static ClientModuleLoader getModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
    {
        if(hostModuleName != null && hostModuleVersion != null && modulePath != null)
        {
            if(moduleLoader == null)
            {
                moduleLoader = new ClientModuleLoader(hostModuleName, hostModuleVersion, modulePath);
            }
            return moduleLoader;
        }
        return null;
    }

    protected void initModule(ClientModuleInstance instance) throws ClassNotFoundException, InstantiationException,
                                                                    IllegalAccessException, IOException
    {
        instance.setState(ModuleInstance.LOADING);
        URLClassLoader loader = new URLClassLoader(instance.buildClassLoaderURLs());

        Class<?> clazz = loader.loadClass(instance.getModuleClass());

        if(IClientModule.class.isAssignableFrom(clazz))
        {
            IClientModule module = (IClientModule) clazz.newInstance();

            instance.setModule(module);
            instance.setLoader(loader);

            if(module.getNamespace() != null && !module.getNamespace().isEmpty() &&
                    module.getNamespaceURI() != null && !module.getNamespaceURI().isEmpty())
            {
                Client.getCommunicator().addModuleCommunicator(module.newCommunicator(Client.getCommunicator()));
            }

            if(module.getRights() != null)
            {
                ModuleRightManager.addRights(module.getRights());
            }

            if(module.getMainTabs() != null)
            {
                TabViewController.addTabs(ModuleTabWrapper.wrap(module.getMainTabs(), clazz));
            }
        } else
        {
            instance.setState(ModuleInstance.UNINITIALIZED);
            Logger.warn(LegionLogger.MODULE, instance.getName() + ": module class " + clazz.getCanonicalName() +
                    " does not implement IModule! Unload module ...");
            loader.close();
        }
    }

    @Override
    protected void loadModule(File jarFile) throws IOException
    {
        if(jarFile.exists())
        {
            JarInputStream jis = new JarInputStream(new FileInputStream(jarFile));
            Manifest manifest = jis.getManifest();

            if(manifest != null && manifest.getEntries() != null)
            {
                Attributes attributes = manifest.getMainAttributes();
                String apiLevel = attributes.getValue("Module-API-Level");
                if(apiLevel != null && apiLevel.matches("\\d") && Integer.parseInt(apiLevel) == MODULE_API_LEVEL)
                {
                    ClientModuleInstance instance = new ClientModuleInstance(attributes.getValue("Module-Dependencies"),
                                                                             attributes.getValue("Module-Conflicts"),
                                                                             attributes.getValue("Module-Name"),
                                                                             attributes.getValue("Module-Version"),
                                                                             attributes.getValue("Module-Author"),
                                                                             attributes.getValue("Module-Description"),
                                                                             jarFile,
                                                                             attributes.getValue("Module-Class"),
                                                                             attributes.getValue("Module-Libraries"));

                    if(instance.getName() != null && !instance.getName().isEmpty() && instance.getVersion() != null &&
                            !instance.getVersion().isEmpty() && instance.getModuleClass() != null &&
                            !instance.getModuleClass().isEmpty())
                    {
                        MODULE_INSTANCES.add(instance);
                    }
                }
            }
        }
    }
}
