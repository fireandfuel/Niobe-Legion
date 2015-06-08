package niobe.legion.client.gui.connect;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class ConnectController
{
	@FXML private ProgressIndicator progress;

	@FXML private Label progressLabel;

	@FXML
	private void initialize()
	{

	}

	public DoubleProperty getProgressStatusProperty()
	{
		return this.progress.progressProperty();
	}

	public StringProperty getProgressLabelProperty()
	{
		return this.progressLabel.textProperty();
	}
}
