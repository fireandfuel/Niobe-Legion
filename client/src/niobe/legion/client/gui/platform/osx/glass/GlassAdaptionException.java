/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (GlassAdaptionException.java) is part of Niobe Legion (module niobe-legion-client_main).
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

package niobe.legion.client.gui.platform.osx.glass;

public class GlassAdaptionException extends RuntimeException
{

    private final static long serialVersionUID = 7315344041984700277L;

    public GlassAdaptionException(Throwable e)
    {
        super(getExceptionMessage(e), e);
    }

    private static String getExceptionMessage(Throwable e)
    {
        String description;
        if(e instanceof NoSuchFieldException)
        {
            description = "Unable to find field \"" + e.getMessage() + "\"";
        } else if(e instanceof NoSuchMethodException)
        {
            description = "Unable to find method \"" + e.getMessage() + "\"";
        } else
        {
            description = e.getMessage();
        }

        return description + " (" + getEnvDescription() + ")";
    }

    private static String getEnvDescription()
    {
        return "Using Java " + System.getProperty("java.version") + " on " + System.getProperty("os.name");
    }
}
