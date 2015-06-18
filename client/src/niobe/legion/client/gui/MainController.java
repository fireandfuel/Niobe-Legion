package niobe.legion.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import niobe.legion.client.Client;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;

public class MainController
{
	protected double initialX;
	protected double initialY;

	private Stage     stage;

	@FXML
	private Label     topLabel;
	@FXML
	private Pane      draggableTopPane;
	@FXML
	private ImageView icon;
	@FXML
	private Label     userlabel;

	@FXML
	private AnchorPane mainPane;

	private Object     currentController;


	@FXML
	private void initialize() throws IOException
	{
		this.addDraggableNode(this.draggableTopPane);

		this.loadMask("/niobe/legion/client/fxml/connect/Connect.fxml");
	}

	public Object loadMask(final String maskURI) throws IOException
	{
		if (Platform.isFxApplicationThread())
		{
			URL location = MainController.class.getResource(maskURI);

			FXMLLoader loader = new FXMLLoader(location);
			Node node = loader.load();
			Object controller = loader.getController();
			this.setCurrentContent(node, controller);

			return controller;
		} else
		{
			Platform.runLater(() -> {
				URL location = MainController.class.getResource(maskURI);

				if (location != null)
				{
					FXMLLoader loader = new FXMLLoader(location);
					try
					{
						MainController.this.setCurrentContent(loader.load(), loader.getController());
					}
					catch (IOException e)
					{
						e.printStackTrace();
						// Logger.exception(LegionLogger.STDERR, e);
					}
				} else
				{
					Logger.error(LegionLogger.STDERR, "FXML mask " + maskURI + " not found.");
				}
			});
			return null;
		}
	}

	public void setCurrentContent(final Node node, final Object controller)
	{
		if (Platform.isFxApplicationThread())
		{
			this.mainPane.getChildren().clear();
			this.mainPane.getChildren().add(node);
			this.currentController = controller;
			AnchorPane.setBottomAnchor(node, 7.0d);
			AnchorPane.setLeftAnchor(node, 7.0d);
			AnchorPane.setRightAnchor(node, 7.0d);
			AnchorPane.setTopAnchor(node, 7.0d);
		} else
		{
			Platform.runLater(() -> MainController.this.setCurrentContent(node, controller));
		}
	}

	public Object getCurrentController()
	{
		return this.currentController;
	}

	public Stage getStage()
	{
		return this.stage;
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;
		this.topLabel.setText(stage.getTitle());

		if (this.icon != null && this.stage != null && !this.stage.getIcons().isEmpty())
		{
			this.icon.setImage(this.stage.getIcons().get(0));
		}
	}

	public Object showFatDialog(final String maskURI, final String title) throws IOException
	{
		if (Platform.isFxApplicationThread())
		{
			final Stage dialog = new Stage();
			dialog.initOwner(this.stage);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.setResizable(false);

			URL location = MainController.class.getResource("/niobe/legion/client/fxml/DialogScene.fxml");

			FXMLLoader loader = new FXMLLoader(location);
			Scene scene = loader.load();
			DialogController controller = loader.getController();
			controller.setTitle(title);
			ICloseableDialogController childrenController = (ICloseableDialogController) controller.loadMask(maskURI);

			childrenController.setStage(dialog);

			scene.getStylesheets()
				 .add(MainController.class.getResource("/niobe/legion/client/css/theme.css").toExternalForm());

			dialog.setScene(scene);
			dialog.show();

			dialog.setX(this.stage.getX() + this.stage.getWidth() / 2 - dialog.getWidth() / 2);
			dialog.setY(this.stage.getY() + this.stage.getHeight() / 2 - dialog.getHeight() / 2);

			return childrenController;
		} else
		{
			Platform.runLater(() -> {
				try
				{
					MainController.this.showFatDialog(maskURI, title);
				}
				catch (IOException e)
				{
					Logger.exception(LegionLogger.STDERR, e);
				}
			});
			return null;
		}
	}

	public void showDialog(final String message)
	{
		if (Platform.isFxApplicationThread())
		{
			final Stage dialog = new Stage();
			dialog.initOwner(this.stage);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.setResizable(false);

			Button okButton = new Button("OK");
			okButton.setOnAction(arg0 -> dialog.close());
			okButton.requestFocus();

			Label messageLabel = new Label(message);
			messageLabel.setTextAlignment(TextAlignment.CENTER);

			VBox content = new VBox(35, messageLabel, okButton);
			content.setAlignment(Pos.CENTER);
			content.setPadding(new Insets(10));
			content.setId("mainPane");

			AnchorPane rootPane = new AnchorPane();
			rootPane.setId("rootPane");
			rootPane.getChildren().add(content);

			Scene scene = new Scene(rootPane);

			scene.getStylesheets()
				 .add(MainController.class.getResource("/niobe/legion/client/css/theme.css").toExternalForm());

			dialog.setScene(scene);

			dialog.show();

			dialog.setX(this.stage.getX() + this.stage.getWidth() / 2 - dialog.getWidth() / 2);
			dialog.setY(this.stage.getY() + this.stage.getHeight() / 2 - dialog.getHeight() / 2);
		} else
		{
			Platform.runLater(() -> MainController.this.showDialog(message));
		}
	}

	private void addDraggableNode(final Node node)
	{

		node.setOnMousePressed(me -> {
			if (me.getButton() != MouseButton.MIDDLE)
			{
				MainController.this.initialX = me.getSceneX();
				MainController.this.initialY = me.getSceneY();
			}

			if (me.getClickCount() == 2)
			{
				MainController.this.maximize();
			}
		});

		node.setOnMouseDragged(me -> {
			if (me.getButton() != MouseButton.MIDDLE && !MainController.this.stage.isMaximized())
			{
				node.getScene().getWindow().setX(me.getScreenX() - MainController.this.initialX);
				node.getScene().getWindow().setY(me.getScreenY() - MainController.this.initialY);
			}
		});
	}

	@FXML
	private void maximize()
	{
		this.stage.setMaximized(!this.stage.isMaximized());
	}

	@FXML
	private void iconfy()
	{
		this.stage.setIconified(true);
	}

	@FXML
	private void close() throws IOException, XMLStreamException
	{
		Client.close();
	}
}
