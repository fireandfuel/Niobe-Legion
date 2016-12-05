/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (DebugController.java) is part of Niobe Legion (module niobe-legion-client_main).
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

package niobe.legion.client.gui.debug;


import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javax.xml.stream.XMLStreamConstants;
import niobe.legion.client.Client;
import niobe.legion.client.gui.ICloseableDialogController;
import niobe.legion.client.gui.latency.LatencyService;
import niobe.legion.shared.communicator.Communicator;
import niobe.legion.shared.communicator.ICommunicator;
import niobe.legion.shared.data.Stanza;

public class DebugController implements ICommunicator, ICloseableDialogController, XMLStreamConstants
{
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("'Ping: '0.##'ms'");

    @FXML
    private TextArea messages;
    @FXML
    private Label latencyLabel;

    private Stage stage;

    @FXML
    private void initialize()
    {
        this.messages.setText("-- Connected " + LocalDateTime.now().toString() + "\n");

        LatencyService service = LatencyService.getInstance();
        service.setOnSucceeded(event -> this.latencyLabel.setText(DECIMAL_FORMAT.format(service.getValue())));
    }

    @Override
    public void consumeStartElement(Stanza currentStanza) throws IOException
    {
        this.startElement("[IN]", currentStanza);
    }

    private void startElement(String type, Stanza currentStanza)
    {
        if(currentStanza != null && currentStanza.getName() != null)
        {
            StringBuilder message = new StringBuilder(type + " <" + currentStanza.getName());

            if(!currentStanza.hasNoAttributes())
            {
                currentStanza.forEachAttribute((key, value) -> message.append(" " + key + "=\"" + value + "\""));
            }

            if(currentStanza.isEmptyElement())
            {
                message.append("/>\n");
            } else
            {
                message.append(">\n");
            }

            if("legion:proceedtls".equals(currentStanza.getName()))
            {
                message.append("-- Start TLS connection\n");
            }

            if("legion:proceedcompression".equals(currentStanza.getName()))
            {
                message.append("-- Start compressed connection\n");
            }

            Platform.runLater(() -> messages.appendText(message.toString()));
        }
    }

    @Override
    public void consumeCharacters(Stanza currentStanza) throws IOException
    {
        this.characters("[IN]", currentStanza);
    }

    private void characters(String type, Stanza currentStanza)
    {
        if(currentStanza != null && currentStanza.getValue() != null)
        {
            Platform.runLater(() -> messages
                    .appendText(type + " " + currentStanza.getName() + " CDATA: \"" + currentStanza
                            .getValue() + "\"\n"));
        }
    }

    @Override
    public void consumeEndElement(Stanza currentStanza) throws IOException
    {
        this.endElement("[IN]", currentStanza);
    }

    private void endElement(String type, Stanza currentStanza)
    {
        if(currentStanza != null && currentStanza.getName() != null)
        {
            Platform.runLater(() -> messages.appendText(type + " </" + currentStanza.getName() + ">\n"));
        }
    }

    @Override
    public void write(Stanza stanza) throws IOException
    {
        if(stanza != null && stanza.getName() != null)
        {
            switch(stanza.getEventType())
            {
                case START_ELEMENT:
                    this.startElement("[OUT]", stanza);
                    break;
                case CHARACTERS:
                    this.startElement("[OUT]", stanza);
                    this.characters("[OUT]", stanza);
                case END_ELEMENT:
                    this.endElement("[OUT]", stanza);
                    break;
            }
        }
    }

    @Override
    public String getNamespace()
    {
        return Communicator.DEBUG_NAMESPACE;
    }

    @Override
    public String getNamespaceURI()
    {
        return Communicator.DEBUG_NAMESPACE_URI;
    }

    @Override
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @Override
    public void close()
    {
        if(this.stage != null)
        {
            this.stage.close();
        }
    }

    public boolean isClosed()
    {
        return this.stage == null || !this.stage.isShowing();
    }

    @FXML
    private void exit()
    {
        Platform.exit();
    }

    @FXML
    public void closeConnection() throws IOException
    {
        this.writeMessage("close Connection by watchdog");
        Client.getCommunicator().close();
    }

    @FXML
    public void ping() throws IOException
    {
        Client.getCommunicator().ping(null);
    }

    public void writeMessage(String message)
    {
        Platform.runLater(() -> messages.appendText("-- " + message + "\n"));
    }
}
