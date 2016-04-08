/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (AdminType.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.admin;

import niobe.legion.client.gui.IMaskType;

public class AdminType implements IMaskType
{
    private String caption;
    private String maskURI;

    public AdminType(String caption, String maskURI)
    {
        this.caption = caption;
        this.maskURI = maskURI;
    }

    @Override
    public String getCaption()
    {
        return this.caption;
    }

    @Override
    public String getMaskURI()
    {
        return this.maskURI;
    }

    @Override
    public String toString()
    {
        return (this.caption != null) ? this.caption : super.toString();
    }
}
