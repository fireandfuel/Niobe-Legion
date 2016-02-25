/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MenuBarSyncListener.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.platform.osx.listener;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.ListChangeListener;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import niobe.legion.client.gui.platform.osx.util.MenuBarUtils;

public class MenuBarSyncListener implements ListChangeListener<Stage>
{

    private static MenuBar MENU_BAR;
    private static MenuBarSyncListener instance = null;

    public static void register(MenuBar menuBar)
    {
        MENU_BAR = menuBar;

        if(instance == null)
        {
            instance = new MenuBarSyncListener();
            StageHelper.getStages().addListener(instance);
        }
    }

    public static void unregister()
    {
        if(instance != null)
        {
            StageHelper.getStages().removeListener(instance);
            instance = null;
        }
    }

    private MenuBarSyncListener()
    {
    }

    @Override
    public void onChanged(Change<? extends Stage> stageChanges)
    {
        while(stageChanges.next())
        {
            stageChanges.getAddedSubList().forEach(stage -> MenuBarUtils.setMenuBar(stage, MENU_BAR));
        }
    }

}
