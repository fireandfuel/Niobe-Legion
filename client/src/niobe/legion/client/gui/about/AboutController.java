/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (AboutController.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.about;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import niobe.legion.client.Client;
import niobe.legion.client.communicator.ClientCommunicator;
import niobe.legion.client.gui.ICloseableDialogController;

public class AboutController implements ICloseableDialogController
{
    @FXML
    private Label topLabel;
    @FXML
    private WebView aboutView;
    @FXML
    private WebView authorsView;
    @FXML
    private WebView contributeView;
    @FXML
    private TextArea configuration;
    @FXML
    private TabPane aboutTabs;

    private Stage stage;

    public void setTitle(String title)
    {
        this.topLabel.setText(title);
    }

    @FXML
    public void initialize()
    {
        initWebView(this.aboutView,
                    this.getClass().getResource("/niobe/legion/client/html/about.html").toExternalForm(),
                    null);
        initWebView(this.authorsView,
                    this.getClass().getResource("/niobe/legion/client/html/authors.html").toExternalForm(),
                    null);
        initWebView(this.contributeView, null, Client.getLocalisation("contributeText"));

        StringBuilder text = new StringBuilder();
        text.append("Client authentication:\n" + ClientCommunicator.getClientNameAndVersion() + "\n\n");
        text.append("Client features:\n" + ClientCommunicator.getClientFeatures() + "\n\n");
        if(Client.getCommunicator() != null)
        {
            text.append("Server IP address/Port:\n" + Client.getCommunicator().getServerAddress() + "\n\n");
            text.append("Server authentication:\n" + Client.getCommunicator().getServerNameAndVersion() + "\n\n");
            text.append("Server features:\n" + Client.getCommunicator().getServerFeatures() + "\n\n");
            text.append("Connection type to server:\n" + Client.getCommunicator().getCommunicationType() + "\n\n");
        } else
        {
            text.append("Server IP address/Port:\nNot connected\n\n");
        }

        text.append("VM Properties:");
        System.getProperties().forEach((key, value) -> text
                .append("\n" + key + "=" + value.toString().replace("\n", "\\n").replace("\r", "\\r")
                        .replace("\t", "\\t")));

        this.configuration.setText(text.toString());
    }

    @FXML
    @Override
    public void close()
    {
        if(this.stage != null)
        {
            this.stage.close();
        }
    }

    @Override
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    private void initWebView(WebView view, String url, String content)
    {
        view.setCache(true);
        view.setContextMenuEnabled(false);
        view.getEngine().setJavaScriptEnabled(false);
        view.getEngine().setUserStyleSheetLocation(this.getClass().getResource("/niobe/legion/client/css/html.css")
                                                           .toExternalForm());
        if(url != null)
        {
            view.getEngine().load(url);
        } else if(content != null)
        {
            view.getEngine().loadContent(content);
        }
        Platform.runLater(() -> view.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            try
            {
                URI address = new URI(newValue);

                if(newValue.startsWith("http://") || newValue.startsWith("https://"))
                {
                    // workaround for https://bugs.openjdk.java.net/browse/JDK-8087652
                    Platform.runLater(() -> {
                        if(url != null)
                        {
                            view.getEngine().load(url);
                        } else if(content != null)
                        {
                            view.getEngine().loadContent(content);
                        }
                    });
                    Desktop.getDesktop().browse(address);
                }
            } catch(IOException | URISyntaxException e)
            {
                System.out.println(e);
            }
        }));
    }
}
