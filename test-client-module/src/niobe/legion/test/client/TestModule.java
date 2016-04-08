/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (TestModule.java) is part of Niobe Legion (module niobe-legion-test-client-module).
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

package niobe.legion.test.client;

import niobe.legion.client.module.IClientModule;
import niobe.legion.client.module.IModuleTab;
import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.data.IRight;

public class TestModule implements IClientModule
{
    @Override
    public IModuleTab[] getMainTabs()
    {
        return new IModuleTab[]{new TestTab()};
    }

    @Override
    public boolean startModule()
    {
        System.out.println("Starting Niobe Legion Client Test Module");
        return true;
    }

    @Override
    public boolean stopModule()
    {
        System.out.println("Stopping Niobe Legion Client Test Module");
        return true;
    }

    @Override
    public ICommunicator newCommunicator(ICommunicator parent)
    {
        return new TestCommunicator(parent);
    }

    @Override
    public String getNamespace()
    {
        return TestCommunicator.NAMESPACE;
    }

    @Override
    public String getNamespaceURI()
    {
        return TestCommunicator.NAMESPACE_URI;
    }

    @Override
    public IRight[] getRights()
    {
        return new IRight[]{new TestRight("test", null)};
    }

    @Override
    public String getLicenseText()
    {
        return null;
    }
}
