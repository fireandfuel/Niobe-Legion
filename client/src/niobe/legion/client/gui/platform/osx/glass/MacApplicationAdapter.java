/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MacApplicationAdapter.java) is part of Niobe Legion (module niobe-legion-client).
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

import com.sun.glass.ui.Application;
import java.lang.invoke.MethodHandle;
import niobe.legion.client.gui.platform.osx.util.ReflectionUtils;

public class MacApplicationAdapter
{
    private MethodHandle hide;
    private MethodHandle hideOtherApplications;
    private MethodHandle unhideAllApplications;

    private Application app;

    public MacApplicationAdapter() throws ReflectiveOperationException
    {
        app = Application.GetApplication();

        hide = ReflectionUtils.getHandle(app, "_hide");
        hideOtherApplications = ReflectionUtils.getHandle(app, "_hideOtherApplications");
        unhideAllApplications = ReflectionUtils.getHandle(app, "_unhideAllApplications");
    }

    public void hide()
    {
        ReflectionUtils.invokeQuietly(hide, app);
    }

    public void hideOtherApplications()
    {
        ReflectionUtils.invokeQuietly(hideOtherApplications, app);
    }

    public void unhideAllApplications()
    {
        ReflectionUtils.invokeQuietly(unhideAllApplications, app);
    }

    public void quit()
    {
        Application.EventHandler eh = app.getEventHandler();
        if(eh != null)
        {
            eh.handleQuitAction(Application.GetApplication(), System.nanoTime());
        }
    }
}
