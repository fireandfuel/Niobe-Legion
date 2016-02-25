/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ModuleDependencyItem.java) is part of Niobe Legion (module niobe-legion-shared).
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ModuleDependencyItem<MI extends ModuleInstance>
{
    private MI moduleInstance;

    private boolean visited;
    private int priority;
    private List<ModuleDependencyItem<MI>> parents = new ArrayList<ModuleDependencyItem<MI>>();
    private List<ModuleDependencyItem<MI>> children = new ArrayList<ModuleDependencyItem<MI>>();

    public ModuleDependencyItem(MI moduleInstance)
    {
        this.moduleInstance = moduleInstance;
    }

    public MI getModuleInstance()
    {
        return this.moduleInstance;
    }

    public String getName()
    {
        return this.moduleInstance.getName();
    }

    public String getVersion()
    {
        return this.moduleInstance.getVersion();
    }

    public String[] getDependencies()
    {
        return this.moduleInstance.getDependencies();
    }

    public int getState()
    {
        return this.moduleInstance.getState();
    }

    public void addParent(ModuleDependencyItem<MI> parent)
    {
        this.parents.add(parent);
    }

    public void removeParent(ModuleDependencyItem<MI> parent)
    {
        this.parents.remove(parent);
    }

    public boolean containsParent(ModuleDependencyItem<MI> parent)
    {
        return this.parents.contains(parent);
    }

    public Stream<ModuleDependencyItem<MI>> getParents()
    {
        return this.parents.stream();
    }

    public void addChild(ModuleDependencyItem<MI> child)
    {
        this.children.add(child);
    }

    public void removeChild(ModuleDependencyItem<MI> child)
    {
        this.children.remove(child);
    }

    public boolean containsChild(ModuleDependencyItem<MI> child)
    {
        return this.children.contains(child);
    }

    public Stream<ModuleDependencyItem<MI>> getChildren()
    {
        return this.children.stream();
    }

    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }
}
