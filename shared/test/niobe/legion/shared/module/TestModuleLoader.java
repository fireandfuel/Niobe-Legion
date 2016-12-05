/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (TestModuleLoader.java) is part of Niobe Legion (module niobe-legion-shared_test).
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

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by nk on 04.02.16.
 */
public class TestModuleLoader extends ModuleLoader<TestModuleInstance>
{
    protected TestModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath,
                               List<TestModuleInstance> testModuleInstances)
    {
        super(hostModuleName, hostModuleVersion, modulePath);

        MODULE_INSTANCES.addAll(testModuleInstances);
    }

    @Override
    protected void initModule(TestModuleInstance instance) throws ClassNotFoundException, InstantiationException,
                                                                  IllegalAccessException, IOException
    {

    }

    @Override
    protected void loadModule(File jarFile) throws IOException
    {

    }
}
