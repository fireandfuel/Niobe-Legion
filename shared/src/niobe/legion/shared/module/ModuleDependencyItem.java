/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
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

    private int priority;
    private boolean visited;
    private List<ModuleDependencyItem<MI>> dependsOn = new ArrayList<ModuleDependencyItem<MI>>();
    private List<ModuleDependencyItem<MI>> dependencyOf = new ArrayList<ModuleDependencyItem<MI>>();

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

    public void addDependsOn(ModuleDependencyItem<MI> parent)
    {
        this.dependsOn.add(parent);
    }

    public void removeDependsOn(ModuleDependencyItem<MI> parent)
    {
        this.dependsOn.remove(parent);
    }

    public boolean dependsOn(ModuleDependencyItem<MI> parent)
    {
        return this.dependsOn.stream().anyMatch(item -> item.equals(parent) || item.dependsOn(parent));
    }

    public Stream<ModuleDependencyItem<MI>> getDependsOn()
    {
        return this.dependsOn.stream();
    }

    public boolean isDependsOnEmpty()
    {
        return dependsOn.isEmpty();
    }

    public void addDependencyOf(ModuleDependencyItem<MI> child)
    {
        this.dependencyOf.add(child);
    }

    public void removeDependencyOf(ModuleDependencyItem<MI> child)
    {
        this.dependencyOf.remove(child);
    }

    public boolean isDependencyOf(ModuleDependencyItem<MI> child)
    {
        return this.dependencyOf.stream().anyMatch(item -> item.equals(child) || item.isDependencyOf(child));
    }

    public Stream<ModuleDependencyItem<MI>> getDependencyOf()
    {
        return this.dependencyOf.stream();
    }

    public boolean isDependencyOfEmpty()
    {
        return dependencyOf.isEmpty();
    }

    public int getPriority()
    {
        return this.priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }
}
