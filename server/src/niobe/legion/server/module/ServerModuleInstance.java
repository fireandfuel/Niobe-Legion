/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ServerModuleInstance.java) is part of Niobe Legion (module niobe-legion-server).
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

package niobe.legion.server.module;

import java.io.File;
import niobe.legion.server.AbstractDatabase;
import niobe.legion.server.Server;
import niobe.legion.shared.module.ModuleInstance;
import niobe.legion.shared.module.ModuleRightManager;

public class ServerModuleInstance extends ModuleInstance
{

    private AbstractDatabase database;

    public ServerModuleInstance(String dependencies, String conflicts, String name, String version, String contact,
                                String description, File moduleFile, String moduleClass)
    {
        super(dependencies, conflicts, name, version, contact, description, moduleFile, moduleClass);
    }

    @Override
    public synchronized void stop()
    {
        if(this.module != null && this.state == ModuleInstance.RUNNING)
        {
            this.state = ModuleInstance.STOPPING;
            if(this.module.stopModule())
            {
                if(this.module.getNamespaceURI() != null && !this.module.getNamespaceURI().isEmpty())
                {
                    Server.getCommunicators().forEach(communicator -> communicator
                            .removeModuleCommunicator(this.module.getNamespaceURI()));
                }
                if(this.module.getRights() != null && this.module.getRights().length > 0)
                {
                    ModuleRightManager.removeRights(this.module.getRights());
                }
                this.state = ModuleInstance.INITIALIZED;
            }
        }
    }

    public void setDatabase(AbstractDatabase database)
    {
        this.database = database;
    }

    public AbstractDatabase getDatabase()
    {
        return database;
    }
}
