/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (IServerModule.java) is part of Niobe Legion (module niobe-legion-server).
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

import niobe.legion.server.database.AbstractDatabase;
import niobe.legion.shared.module.IModule;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
public interface IServerModule extends IModule
{
    /**
     * For checking if database is not in use my other modules
     *
     * @return name of the module's database
     */
    String getDatabaseName();

    /**
     * Initialize the module's database after database checks are done
     * <p>
     * This method is only for calling by the server, do not call it on your own.
     * <p>
     * You are not allowed to use the hibernate.archive.autodetection property in your hibernate persistence
     * configuration!
     *
     * @return the database of the module
     */
    AbstractDatabase initDatabase();
}
