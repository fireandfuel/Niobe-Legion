/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ModuleTabWrapper.java) is part of Niobe Legion (module niobe-legion-client).
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

import java.net.URL;
import java.util.stream.Stream;
import niobe.legion.client.gui.tab.ITab;
import niobe.legion.shared.data.IRight;

/**
 * Wrap a IModuleTab implementation inside this ITab implementation.
 * With URL getTabURL() it's possible to load a fxml file from the internet and do some nasty things.
 * <p>
 * You can only get a location inside the module's jar file with String getTabFileName().
 */
public class ModuleTabWrapper implements ITab
{
    private IModuleTab moduleTab;
    private Class<?> moduleClass;

    private ModuleTabWrapper(IModuleTab moduleTab, Class<?> moduleClass)
    {
        this.moduleTab = moduleTab;
        this.moduleClass = moduleClass;
    }

    @Override
    public URL getTabURL()
    {
        if(moduleTab != null && moduleClass != null)
        {
            return moduleClass.getResource(moduleTab.getTabFileName());
        }
        return null;
    }

    @Override
    public boolean isCloseable()
    {
        return moduleTab != null && moduleTab.isCloseable();
    }

    @Override
    public IRight[] getNeededGroupRights()
    {
        return moduleTab != null ? moduleTab.getNeededGroupRights() : null;
    }

    protected static ITab[] wrap(IModuleTab[] moduleTabs, Class<?> moduleClass)
    {
        return Stream.of(moduleTabs).map(moduleTab -> new ModuleTabWrapper(moduleTab, moduleClass))
                .toArray(ITab[]::new);
    }
}
