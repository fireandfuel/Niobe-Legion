/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (AdapterContext.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.platform.osx.glass;

import com.sun.javafx.tk.Toolkit;

public class AdapterContext
{

    private static AdapterContext instance;

    private TKSystemMenuAdapter systemMenuAdapter;
    private MacApplicationAdapter applicationAdapter;

    private AdapterContext(TKSystemMenuAdapter systemMenuAdapter, MacApplicationAdapter applicationAdapter)
    {
        this.systemMenuAdapter = systemMenuAdapter;
        this.applicationAdapter = applicationAdapter;
    }

    public static AdapterContext getContext()
    {
        if(instance == null)
        {
            instance = createContext();
        }

        return instance;
    }

    public MacApplicationAdapter getApplicationAdapter()
    {
        return applicationAdapter;
    }

    public TKSystemMenuAdapter getSystemMenuAdapter()
    {
        return systemMenuAdapter;
    }

    private static AdapterContext createContext()
    {
        if(!Toolkit.getToolkit().getSystemMenu().isSupported())
        {
            return null;
        }

        try
        {
            return new AdapterContext(new TKSystemMenuAdapter(), new MacApplicationAdapter());
        } catch(ReflectiveOperationException e)
        {
            throw new GlassAdaptionException(e);
        }
    }
}
