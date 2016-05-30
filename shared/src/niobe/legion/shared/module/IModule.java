/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (IModule.java) is part of Niobe Legion (module niobe-legion-shared).
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

package niobe.legion.shared.module;

import niobe.legion.shared.communicator.ICommunicator;
import niobe.legion.shared.data.IRight;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
public interface IModule
{
    /**
     * starts the module
     *
     * @return module is started successfully
     */
    boolean startModule();

    /**
     * stops the module
     *
     * @return module is stopped successfully
     */
    boolean stopModule();

    /**
     * @return a module communicator, may be null if not needed
     */
    ICommunicator newCommunicator(ICommunicator parent);

    /**
     * @return the module communicator namespace, may be null if not needed
     */
    String getNamespace();

    /**
     * @return the module communicator namespace uri, may be null if not needed
     */
    String getNamespaceURI();

    /**
     * @return the rights the module sets at the host
     */
    IRight[] getRights();

    /**
     * @return the full license text
     */
    String getLicenseText();
}
