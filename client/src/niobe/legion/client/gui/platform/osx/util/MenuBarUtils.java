/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MenuBarUtils.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.platform.osx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MenuBarUtils
{
    public static MenuBar createMenuBar(List<Menu> menus)
    {
        MenuBar bar = new MenuBar();
        bar.setUseSystemMenuBar(true);
        bar.getMenus().addAll(menus);
        return bar;
    }

    public static void removeExistingMenuBar(Pane pane)
    {
        ObservableList<Node> children = pane.getChildren();
        children.removeAll(children.stream().filter(node -> node instanceof MenuBar).collect(Collectors.toList()));
    }

    public static void setMenuBar(Stage stage, MenuBar menuBar)
    {
        Parent parent = stage.getScene().getRoot();
        if(parent instanceof Pane)
        {
            setMenuBar((Pane) parent, menuBar);
        }
    }

    public static void setMenuBar(Pane pane, MenuBar menuBar)
    {
        replaceMenuBar(pane, createMenuBar(extractSubMenus(menuBar)));
    }

    private static void replaceMenuBar(Pane pane, MenuBar createMenuBar)
    {
        removeExistingMenuBar(pane);
        pane.getChildren().add(createMenuBar);
    }

    private static List<Menu> extractSubMenus(MenuBar bar)
    {
        if(bar.getMenus().size() <= 1)
        {
            return new ArrayList<>();
        }
        return bar.getMenus().subList(1, bar.getMenus().size());
    }
}
