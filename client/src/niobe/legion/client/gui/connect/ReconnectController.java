package niobe.legion.client.gui.connect;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import niobe.legion.client.Client;
import niobe.legion.client.gui.ICloseableDialogController;

public class ReconnectController implements ICloseableDialogController
{
	@FXML private ProgressIndicator progress;

	@FXML private Label progressLabel;

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
		Platform.exit();
	}

	@Override
	public void close()
	{
		this.stage.close();
	}
}
