package niobe.legion.client.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class DialogController
{
	@FXML
	private Label      topLabel;
	@FXML
	private AnchorPane mainPane;
	@FXML
	private Button     close;

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
		if (controller != null)
		{
			this.controller.close();
		}
	}

	public ICloseableDialogController loadMask(final String maskURI, boolean closeable) throws IOException
	{
		URL location = DialogController.class.getResource(maskURI);

		FXMLLoader loader = new FXMLLoader(location);
		Node mask = loader.load();
		this.controller = loader.getController();

		if (!closeable)
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
