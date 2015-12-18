/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ModuleRightManager.java) is part of Niobe Legion (module niobe-legion-shared).
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
import niobe.legion.shared.data.IRight;

public class ModuleRightManager
{
    final static List<IRight> rights = new ArrayList<IRight>();

    private ModuleRightManager()
    {

    }

    public static void addRights(IRight... rightsToAdd)
    {
        if(rightsToAdd != null && rightsToAdd.length > 0)
        {
            Stream.of(rightsToAdd).filter(right -> !rights.contains(right)).forEach(rights::add);
        }
    }

    public static void removeRights(IRight... rightsToRemove)
    {
        if(rightsToRemove != null && rightsToRemove.length > 0)
        {
            Stream.of(rightsToRemove).forEach(rights::remove);
        }
    }

    public static IRight[] getRights()
    {
        return rights.toArray(new IRight[rights.size()]);
    }
}
