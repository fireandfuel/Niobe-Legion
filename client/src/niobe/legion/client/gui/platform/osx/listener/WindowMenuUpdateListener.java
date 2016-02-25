/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (WindowMenuUpdateListener.java) is part of Niobe Legion (module niobe-legion-client).
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class WindowMenuUpdateListener implements ListChangeListener<Stage>
{

    private final WeakReference<Menu> windowMenu;
    private final List<MenuItem> createdMenuItems;

    public WindowMenuUpdateListener(Menu windowMenu)
    {
        this.windowMenu = new WeakReference<Menu>(windowMenu);
        createdMenuItems = new ArrayList<>();

        updateWindowMenuItems();
    }

    @Override
    public void onChanged(Change<? extends Stage> c)
    {
        updateWindowMenuItems();
    }

    protected void updateWindowMenuItems()
    {
        Menu menu = windowMenu.get();
        if(menu != null)
        {
            menu.getItems().removeAll(createdMenuItems);
            StageHelper.getStages().forEach(stage -> addWindowMenuItem(stage));
        }
    }

    private void addWindowMenuItem(Stage stage)
    {
        Menu menu = windowMenu.get();
        if(menu != null)
        {
            addWindowMenuItem(stage, menu);
        }
    }

    private void addWindowMenuItem(Stage stage, Menu menu)
    {
        MenuItem item = new MenuItem(stage.getTitle());
        item.setOnAction(event -> stage.toFront());
        createdMenuItems.add(item);
        menu.getItems().add(item);
    }

}
