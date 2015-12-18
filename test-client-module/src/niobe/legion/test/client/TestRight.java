/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (TestRight.java) is part of Niobe Legion (module niobe-legion-test-client-module).
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

package niobe.legion.test.client;

import java.util.ArrayList;
import java.util.List;
import niobe.legion.shared.data.IRight;

public class TestRight implements IRight
{

    private String name;
    private IRight parent;

    TestRight(String name, IRight parent)
    {
        this.name = name;
        this.parent = parent;
    }

    private transient List<IRight> children;

    @Override
    public String getName()
    {
        return (this.getParent() != null) ? (this.getParent().getName() + "." + this.name) : this.name;
    }

    @Override
    public IRight getParent()
    {
        return this.parent;
    }

    @Override
    public List<IRight> getChildren()
    {
        return this.children;
    }

    @Override
    public void addChildren(IRight right)
    {
        if(this.children == null)
        {
            this.children = new ArrayList<IRight>();
        }
        this.children.add(right);
    }
}