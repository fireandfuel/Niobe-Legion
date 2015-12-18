/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (TabViewController.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.tab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import niobe.legion.client.Client;

public class TabViewController
{
    final static Predicate<ITab> hasRightToLoadTab = iTab -> {
        if(iTab.getNeededGroupRights() == null)
        {
            return true;
        }
        return Stream.of(iTab.getNeededGroupRights()).allMatch(right -> Client.getCommunicator().checkRight(right));
    };

    final static List<ITab> MAIN_TABS = new ArrayList<ITab>();

    static
    {
        MAIN_TABS.addAll(Arrays.asList(LegionTab.values()));
    }

    public static void addTabs(ITab... tabsToAdd)
    {
        if(tabsToAdd != null && tabsToAdd.length > 0)
        {
            Stream.of(tabsToAdd).filter(tab -> !MAIN_TABS.contains(tab)).forEach(MAIN_TABS::add);
        }
    }

    public static void removeTabs(ITab... rightsToRemove)
    {
        if(rightsToRemove != null && rightsToRemove.length > 0)
        {
            Stream.of(rightsToRemove).forEach(MAIN_TABS::remove);
        }
    }

    public static ITab[] getMainTabs()
    {
        return MAIN_TABS.toArray(new ITab[MAIN_TABS.size()]);
    }

    private Object tabController;

    @FXML
    private TabPane tabPane;

    @FXML
    private void initialize() throws IOException
    {
        MAIN_TABS.stream().filter(hasRightToLoadTab).forEach(tab -> {
            try
            {
                this.tabPane.getTabs().add(loadTab(tab));
            } catch(IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    private Tab loadTab(ITab tab) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(tab.getTabURL(), Client.getLocalBundle());
        this.tabController = loader.getController();
        Object tabObject = loader.load();

        Tab tabInstance = (tabObject instanceof Tab) ? (Tab) tabObject : null;
        tabInstance.setClosable(tab.isCloseable());

        return tabInstance;
    }
}
