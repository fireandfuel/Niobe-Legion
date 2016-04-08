/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (TestModuleInstance.java) is part of Niobe Legion (module niobe-legion-shared).
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

package niobe.legion.shared.module;

import java.io.File;

public class TestModuleInstance extends ModuleInstance
{
    public TestModuleInstance(String dependencies, String conflicts, String name, String version, String contact,
                              String description, File moduleFile, String moduleClass)
    {
        super(dependencies, conflicts, name, version, contact, description, moduleFile, moduleClass, null);
    }

    @Override
    public void stop()
    {

    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof TestModuleInstance)
        {
            TestModuleInstance other = (TestModuleInstance) obj;

            return this.getName().equals(other.getName()) && this.getVersion().equals(other.getVersion());
        }
        return super.equals(obj);
    }
}
