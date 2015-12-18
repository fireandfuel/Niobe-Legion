/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (DialogController.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import niobe.legion.client.Client;

public class DialogController
{
    @FXML
    private Label topLabel;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button close;

    private ICloseableDialogController controller;

    public void setTitle(String title)
    {
        this.topLabel.setText(title);
    }

    @FXML
    public void initialize()
    {

    }

    @FXML
    public void close()
    {
        if(controller != null)
        {
            this.controller.close();
        }
    }

    public ICloseableDialogController loadMask(final String maskURI, boolean closeable) throws IOException
    {
        URL location = DialogController.class.getResource(maskURI);

        FXMLLoader loader = new FXMLLoader(location, Client.getLocalBundle());
        Node mask = loader.load();
        this.controller = loader.getController();

        if(!closeable)
        {
            close.setVisible(false);
        }

        this.mainPane.getChildren().clear();
        this.mainPane.getChildren().add(mask);
        AnchorPane.setBottomAnchor(mask, 7.0d);
        AnchorPane.setLeftAnchor(mask, 7.0d);
        AnchorPane.setRightAnchor(mask, 7.0d);
        AnchorPane.setTopAnchor(mask, 7.0d);

        return this.controller;
    }

}
