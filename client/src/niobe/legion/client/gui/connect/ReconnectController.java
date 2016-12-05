/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ReconnectController.java) is part of Niobe Legion (module niobe-legion-client_main).
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

package niobe.legion.client.gui.connect;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import niobe.legion.client.Client;
import niobe.legion.client.gui.ICloseableDialogController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReconnectController implements ICloseableDialogController
{
    private final static Logger LOG = LogManager.getLogger(ReconnectController.class);

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Label progressLabel;

    private Stage stage;

    @FXML
    private void initialize()
    {
        Client.bindConnectionProgress(this.progressLabel.textProperty(), this);
    }

    @Override
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @FXML
    private void cancel()
    {
        try
        {
            Client.close();
        } catch(IOException e)
        {
            LOG.catching(e);
        }
    }

    @Override
    public void close()
    {
        this.stage.close();
    }
}
