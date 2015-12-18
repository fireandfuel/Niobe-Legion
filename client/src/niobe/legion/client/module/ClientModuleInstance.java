/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ClientModuleInstance.java) is part of Niobe Legion (module niobe-legion-client).
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
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.client.module;

import java.io.File;
import niobe.legion.client.Client;
import niobe.legion.shared.module.ModuleInstance;
import niobe.legion.shared.module.ModuleRightManager;

public class ClientModuleInstance extends ModuleInstance
{
    public ClientModuleInstance(String dependencies, String conflicts, String name, String version, String contact,
                                String description, File moduleFile, String moduleClass)
    {
        super(dependencies, conflicts, name, version, contact, description, moduleFile, moduleClass);
    }

    @Override
    public void stop()
    {
        if(this.module != null && this.state == ModuleInstance.RUNNING)
        {
            this.state = ModuleInstance.STOPPING;
            if(this.module.stopModule())
            {
                if(this.module.getNamespaceURI() != null && !this.module.getNamespaceURI().isEmpty())
                {
                    Client.getCommunicator().removeModuleCommunicator(this.module.getNamespaceURI());
                }
                if(this.module.getRights() != null && this.module.getRights().length > 0)
                {
                    ModuleRightManager.removeRights(this.module.getRights());
                }
                this.state = ModuleInstance.INITIALIZED;
            }
        }
    }

    public IModuleTab[] getMainTabs()
    {
        if(!(this.module instanceof IClientModule) || this.state == MISSING_DEPENDENCIES || this.state == IN_CONFLICT ||
                this.state == UNINITIALIZED)
        {
            return null;
        }
        return ((IClientModule) this.module).getMainTabs();
    }
}
