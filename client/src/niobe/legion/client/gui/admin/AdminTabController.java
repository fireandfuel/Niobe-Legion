/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (AdminTabController.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.admin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import niobe.legion.client.Client;
import niobe.legion.client.gui.IMaskType;
import niobe.legion.client.gui.MainController;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

public class AdminTabController
{
    private static final List<IMaskType> children = new ArrayList<IMaskType>(Arrays.asList(new AdminType(Client.getLocalisation(
            "userAdministration"), "/niobe/legion/client/fxml/tab/admin/UserAdmin.fxml"),
                                                                                           new AdminType(Client.getLocalisation(
                                                                                                   "groupAdministration"),
                                                                                                         "/niobe/legion/client/fxml/tab/admin/GroupAdmin.fxml"),
                                                                                           new AdminType(Client.getLocalisation(
                                                                                                   "moduleAdministration"),
                                                                                                         "/niobe/legion/client/fxml/tab/admin/ModuleAdmin.fxml")));
    private TreeItem<IMaskType> rootItem = new TreeItem<IMaskType>(new AdminType(Client.getLocalisation("administration"),
                                                                                 null));
    private Object childController;

    @FXML
    private TreeView<IMaskType> selectionTree;

    @FXML
    private AnchorPane adminRoot;

    @FXML
    private void initialize()
    {
        if(this.selectionTree != null)
        {
            this.selectionTree.setRoot(this.rootItem);

            for(IMaskType child : AdminTabController.children)
            {
                this.rootItem.getChildren().add(new TreeItem<IMaskType>(child));
            }
            this.rootItem.setExpanded(true);
        }

        this.selectionTree.setOnMouseClicked(new EventHandler<MouseEvent>()
        {

            @Override
            public void handle(MouseEvent arg0)
            {
                TreeItem<IMaskType> selected = AdminTabController.this.selectionTree.getSelectionModel()
                        .getSelectedItem();
                if(selected != null && selected.getValue() != null && selected.getValue().getMaskURI() != null)
                {
                    String maskURI = selected.getValue().getMaskURI();
                    AdminTabController.this.adminRoot.getChildren().clear();

                    URL location = MainController.class.getResource(maskURI);

                    if(location != null)
                    {

                        FXMLLoader loader = new FXMLLoader(location, Client.getLocalBundle());
                        try
                        {
                            Node node = loader.load();
                            AnchorPane.setBottomAnchor(node, 0.0d);
                            AnchorPane.setLeftAnchor(node, 0.0d);
                            AnchorPane.setRightAnchor(node, 0.0d);
                            AnchorPane.setTopAnchor(node, 0.0d);

                            AdminTabController.this.adminRoot.getChildren().addAll(node);
                            AdminTabController.this.childController = loader.getController();
                        } catch(IOException e)
                        {
                            Logger.exception(LegionLogger.STDERR, e);
                        }
                    }
                }
            }
        });
    }

    public Object getChildController()
    {
        return this.childController;
    }
}
