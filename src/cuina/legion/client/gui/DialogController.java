package cuina.legion.client.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class DialogController
{
	protected double initialX;
	protected double initialY;
	@FXML
	private   Label  topLabel;
	@FXML
	private   Pane   draggableTopPane;

	@FXML
	private AnchorPane mainPane;

	public void setTitle(String title)
	{
		this.topLabel.setText(title);
	}

	@FXML
	public void initialize()
	{
		this.addDraggableNode(this.draggableTopPane);
	}

	private void addDraggableNode(final Node node)
	{
		node.setOnMousePressed(me -> {
			DialogController.this.initialX = me.getSceneX();
			DialogController.this.initialY = me.getSceneY();
		});

		node.setOnMouseDragged(me -> {
			node.getScene().getWindow().setX(me.getScreenX() - DialogController.this.initialX);
			node.getScene().getWindow().setY(me.getScreenY() - DialogController.this.initialY);
		});
	}

	public Object loadMask(final String maskURI) throws IOException
	{
		URL location = DialogController.class.getResource(maskURI);

		FXMLLoader loader = new FXMLLoader(location);
		Node mask = loader.load();
		Object controller = loader.getController();

		this.mainPane.getChildren().clear();
		this.mainPane.getChildren().add(mask);
		AnchorPane.setBottomAnchor(mask, 7.0d);
		AnchorPane.setLeftAnchor(mask, 7.0d);
		AnchorPane.setRightAnchor(mask, 7.0d);
		AnchorPane.setTopAnchor(mask, 7.0d);

		return controller;
	}

}
