package cuina.legion.client.gui.connect;

import cuina.legion.client.Client;
import cuina.legion.client.ClientCommunicator;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.security.sasl.SaslException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class LoginController
{
	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private Button login;

	@FXML
	private Label status;

	@FXML
	private void initialize()
	{

		if(this.username == null || this.password == null)
		{
			this.login.setDisable(true);
		} else
		{
			Platform.runLater(() -> LoginController.this.username.requestFocus());
		}
	}

	@FXML
	private void login() throws UnsupportedEncodingException, SaslException
	{
		if(this.username != null && this.username.getText() != null
				&& !this.username.getText().isEmpty())
		{
			if(this.password != null && this.password.getText() != null
					&& !this.password.getText().isEmpty())
			{
				this.login.setDisable(true);
				this.username.setEditable(false);
				this.password.setEditable(false);
				try
				{
					this.status.setText("Authenfiziere Client");
					Client.getCommunicator().login(this.username.getText(),
							this.password.getText().toCharArray());
				} catch (IOException e)
				{
					Logger.exception(LegionLogger.STDERR, e);
				}
			} else
			{
				Client.getFxController().showDialog("Bitte geben Sie ihr Passwort ein!");
			}
		} else
		{
			Client.getFxController().showDialog(
					"Bitte geben Sie ihren Benutzernamen und Passwort ein!");
		}
	}

	public void loginFailed(final int sender)
	{
		if(Platform.isFxApplicationThread())
		{
			this.status.setText("Authenfizierung ist auf Grund eines\n"
					+ (sender == ClientCommunicator.CLIENT_AUTH_ERR ? "Clientfehlers"
					: "Serverfehlers") + " fehlgeschlagen.");
			this.login.setDisable(false);
			this.username.setEditable(true);
			this.password.setEditable(true);
		} else
		{
			Platform.runLater(() -> LoginController.this.loginFailed(sender));
		}

	}
}
