/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ReloginController.java) is part of Niobe Legion (module niobe-legion-client_main).
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
import java.io.UnsupportedEncodingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.security.sasl.SaslException;
import niobe.legion.client.Client;
import niobe.legion.client.gui.ICloseableDialogController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloginController implements ICloseableDialogController
{
    private final static Logger LOG = LogManager.getLogger(ReloginController.class);

    @FXML
    private TextField userName;

    @FXML
    private PasswordField password;

    @FXML
    private Button login;

    @FXML
    private Label status;

    private Stage stage;

    @FXML
    private void initialize()
    {
        this.status.setText(Client.getLocalisation("loginEnterUserNameAndPassword"));
        if(this.userName == null || this.password == null)
        {
            this.login.setDisable(true);
        } else
        {
            Platform.runLater(() -> ReloginController.this.userName.requestFocus());
        }
    }

    @FXML
    private void login() throws UnsupportedEncodingException, SaslException
    {
        if(this.userName != null && this.userName.getText() != null && !this.userName.getText().isEmpty())
        {
            if(this.password != null && this.password.getText() != null && !this.password.getText().isEmpty())
            {
                this.login.setDisable(true);
                this.userName.setEditable(false);
                this.password.setEditable(false);
                try
                {
                    this.status.setText(Client.getLocalisation("loginAuthenticate"));
                    Client.getCommunicator().login(this.userName.getText(), this.password.getText().toCharArray());
                } catch(IOException e)
                {
                    LOG.catching(e);
                }
            } else
            {
                Client.getFxController().showLightweightDialog(Client.getLocalisation("loginEnterPassword"));
            }
        } else
        {
            Client.getFxController().showLightweightDialog(Client.getLocalisation("loginEnterUserNameAndPassword"));
        }
    }

    public void loginFailed(final int sender)
    {
        if(Platform.isFxApplicationThread())
        {
            this.status.setText(Client.getLocalisation("loginAuthenticationFailed"));
            this.login.setDisable(false);
            this.userName.setEditable(true);
            this.password.setEditable(true);
        } else
        {
            Platform.runLater(() -> ReloginController.this.loginFailed(sender));
        }
    }

    @FXML
    private void cancel()
    {
        Platform.exit();
    }

    @Override
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @Override
    public void close()
    {
        if(Platform.isFxApplicationThread())
        {
            if(this.stage != null)
            {
                this.stage.close();
            }
        } else
        {
            Platform.runLater(() -> ReloginController.this.close());
        }
    }
}
