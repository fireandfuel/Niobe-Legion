/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (IRight.java) is part of Niobe Legion (module niobe-legion-shared).
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

package niobe.legion.shared.data;

import java.util.List;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
public interface IRight
{
    String getName();

    List<IRight> getChildren();

    IRight getParent();

    void addChildren(IRight right);

    default boolean isRoot()
    {
        return this.getParent() == null;
    }

    default boolean isLeaf()
    {
        return this.getChildren() == null || this.getChildren().isEmpty();
    }
}