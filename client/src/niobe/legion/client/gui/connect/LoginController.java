package niobe.legion.client.gui.connect;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import niobe.legion.client.Client;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

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
		this.status.setText("Geben Sie ihren Benutzernamen\n und Passwort ein.");
		if (this.username == null || this.password == null)
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
		if (this.username != null && this.username.getText() != null && !this.username.getText().isEmpty())
		{
			if (this.password != null && this.password.getText() != null && !this.password.getText().isEmpty())
			{
				this.login.setDisable(true);
				this.username.setEditable(false);
				this.password.setEditable(false);
				try
				{
					this.status.setText("Authentifiziere ...");
					Client.getCommunicator().login(this.username.getText(), this.password.getText().toCharArray());
				}
				catch (IOException e)
				{
					Logger.exception(LegionLogger.STDERR, e);
				}
			} else
			{
				Client.getFxController().showDialog("Geben Sie ihr Passwort ein.");
			}
		} else
		{
			Client.getFxController().showDialog("Geben Sie ihren Benutzernamen und Passwort ein.");
		}
	}

	public void loginFailed(final int sender)
	{
		if (Platform.isFxApplicationThread())
		{
			this.status
					.setText("Authentifizierung ist fehlgeschlagen.\nÜberprüfen Sie ihren Benutzernamen und Passwort.");
			this.login.setDisable(false);
			this.username.setEditable(true);
			this.password.setEditable(true);
		} else
		{
			Platform.runLater(() -> LoginController.this.loginFailed(sender));
		}

	}
}
