/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MenuToolkit.java) is part of Niobe Legion (module niobe-legion-client_main).
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

package niobe.legion.client.gui.platform.osx;

import com.sun.javafx.scene.control.GlobalMenuAdapter;
import com.sun.javafx.stage.StageHelper;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import niobe.legion.client.Client;
import niobe.legion.client.gui.platform.osx.glass.AdapterContext;
import niobe.legion.client.gui.platform.osx.glass.GlassAdaptionException;
import niobe.legion.client.gui.platform.osx.glass.MacApplicationAdapter;
import niobe.legion.client.gui.platform.osx.glass.TKSystemMenuAdapter;
import niobe.legion.client.gui.platform.osx.listener.MenuBarSyncListener;
import niobe.legion.client.gui.platform.osx.listener.WindowMenuUpdateListener;
import niobe.legion.client.gui.platform.osx.util.MenuBarUtils;
import niobe.legion.client.gui.platform.osx.util.StageUtils;

public class MenuToolkit
{
    private final static String APP_NAME = "Apple";

    private final static String HIDE = "hide";
    private final static String QUIT = "quit";
    private final static String ABOUT = "about";
    private final static String SHOW_ALL = "show_all";
    private final static String HIDE_OTHERS = "hide_others";
    private final static String MINIMIZE = "minimize";
    private final static String ZOOM = "zoom";
    private final static String CLOSE_WINDOW = "close_window";
    private final static String BRING_ALL_TO_FRONT = "bring_all_to_front";
    private final static String CYCLE_THROUGH_WINDOWS = "cycle_through_windows";

    private final TKSystemMenuAdapter systemMenuAdapter;
    private final MacApplicationAdapter applicationAdapter;

    private MenuToolkit(AdapterContext adapterContext)
    {
        this.systemMenuAdapter = adapterContext.getSystemMenuAdapter();
        this.applicationAdapter = adapterContext.getApplicationAdapter();
    }

    public static MenuToolkit toolkit()
    {

        AdapterContext context = AdapterContext.getContext();
        if(context == null)
        {
            return null;
        }

        return new MenuToolkit(context);
    }

    public Menu createDefaultApplicationMenu(String appName)
    {
        return new Menu(APP_NAME,
                        null,
                        createHideMenuItem(appName),
                        createHideOthersMenuItem(),
                        createUnhideAllMenuItem(),
                        new SeparatorMenuItem(),
                        createQuitMenuItem(appName));
    }

    public MenuItem createQuitMenuItem(String appName)
    {
        MenuItem quit = new MenuItem(Client.getLocalisation(MenuToolkit.QUIT, appName));
        quit.setOnAction(event -> applicationAdapter.quit());
        quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));
        return quit;
    }

    public MenuItem createUnhideAllMenuItem()
    {
        MenuItem unhideAll = new MenuItem(Client.getLocalisation(MenuToolkit.SHOW_ALL));
        unhideAll.setOnAction(event -> applicationAdapter.unhideAllApplications());
        return unhideAll;
    }

    public MenuItem createHideOthersMenuItem()
    {
        MenuItem hideOthers = new MenuItem(Client.getLocalisation(MenuToolkit.HIDE_OTHERS));
        hideOthers.setOnAction(event -> applicationAdapter.hideOtherApplications());
        hideOthers.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.META_DOWN, KeyCombination.ALT_DOWN));
        return hideOthers;
    }

    public MenuItem createHideMenuItem(String appName)
    {
        MenuItem hide = new MenuItem(Client.getLocalisation(MenuToolkit.HIDE, appName));
        hide.setOnAction(event -> applicationAdapter.hide());
        hide.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.META_DOWN));
        return hide;
    }

    public MenuItem createMinimizeMenuItem()
    {
        MenuItem menuItem = new MenuItem(Client.getLocalisation(MenuToolkit.MINIMIZE));
        menuItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.META_DOWN));
        menuItem.setOnAction(event -> StageUtils.minimizeFocusedStage());
        return menuItem;
    }

    public MenuItem createZoomMenuItem()
    {
        MenuItem menuItem = new MenuItem(Client.getLocalisation(MenuToolkit.ZOOM));
        menuItem.setOnAction(event -> StageUtils.zoomFocusedStage());
        return menuItem;
    }

    public MenuItem createCloseWindowMenuItem()
    {
        MenuItem menuItem = new MenuItem(Client.getLocalisation(MenuToolkit.CLOSE_WINDOW));
        menuItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.META_DOWN));
        menuItem.setOnAction(event -> StageUtils.closeCurrentStage());
        return menuItem;
    }

    public MenuItem createBringAllToFrontItem()
    {
        MenuItem menuItem = new MenuItem(Client.getLocalisation(MenuToolkit.BRING_ALL_TO_FRONT));
        menuItem.setOnAction(event -> StageUtils.bringAllToFront());
        return menuItem;
    }

    public MenuItem createCycleWindowsItem()
    {
        MenuItem menuItem = new MenuItem(Client.getLocalisation(MenuToolkit.CYCLE_THROUGH_WINDOWS));
        menuItem.setAccelerator(new KeyCodeCombination(KeyCode.BACK_QUOTE, KeyCombination.META_DOWN));
        menuItem.setOnAction(event -> StageUtils.focusNextStage());
        return menuItem;
    }

    public void setApplicationMenu(Menu menu)
    {
        try
        {
            systemMenuAdapter.setAppleMenu(GlobalMenuAdapter.adapt(menu));
        } catch(Throwable e)
        {
            throw new GlassAdaptionException(e);
        }
    }

    public void setGlobalMenuBar(MenuBar menuBar)
    {
        setMenuBar(menuBar);
        MenuBarSyncListener.register(menuBar);
    }

    public void unsetGlobalMenuBar()
    {
        MenuBarSyncListener.unregister();
    }

    public void setMenuBar(MenuBar menuBar)
    {
        StageHelper.getStages().forEach(stage -> setMenuBar(stage, menuBar));
    }

    public void setMenuBar(Stage stage, MenuBar menuBar)
    {
        Parent parent = stage.getScene().getRoot();
        if(parent instanceof Pane)
        {
            setMenuBar((Pane) parent, menuBar);
        }
    }

    public void setMenuBar(Pane pane, MenuBar menuBar)
    {
        setApplicationMenu(extractApplicationMenu(menuBar));
        MenuBarUtils.setMenuBar(pane, menuBar);
    }

    public void autoAddWindowMenuItems(Menu menu)
    {
        menu.getItems().add(new SeparatorMenuItem());
        StageHelper.getStages().addListener(new WindowMenuUpdateListener(menu));
    }

    protected Menu extractApplicationMenu(MenuBar menuBar)
    {
        return menuBar.getMenus().get(0);
    }
}
