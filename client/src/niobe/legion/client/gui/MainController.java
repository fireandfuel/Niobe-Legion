package niobe.legion.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
import java.util.stream.Stream;

public class MainController
{
	private Stage              stage;
	private ResizeDragListener resizeDragListener;

	@FXML
	private Label      topLabel;
	@FXML
	private ImageView  icon;
	@FXML
	private Button     userButton;
	@FXML
	private AnchorPane mainPane;

	private Object currentController;

	@FXML
	private void initialize() throws IOException
	{
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
			Object[] wrapper = new Object[1];

			Platform.runLater(() -> {
				URL location = MainController.class.getResource(maskURI);

				if (location != null)
				{
					FXMLLoader loader = new FXMLLoader(location);
					try
					{
						Node node = loader.load();
						Object controller = loader.getController();
						this.setCurrentContent(node, controller);

						wrapper[0] = controller;
						synchronized (MainController.this)
						{
							MainController.this.notify();
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				} else
				{
					Logger.error(LegionLogger.STDERR, "FXML mask " + maskURI + " not found.");
				}
			});

			synchronized (MainController.this)
			{
				while (wrapper[0] == null)
				{
					try
					{
						MainController.this.wait();
					}
					catch (InterruptedException e)
					{
					}
				}
			}

			return wrapper[0];
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
		this.resizeDragListener = ResizeDragListener.addResizeListener(stage);
		this.topLabel.setText(stage.getTitle());

		if (this.icon != null && this.stage != null && !this.stage.getIcons().isEmpty())
		{
			this.icon.setImage(this.stage.getIcons().get(0));
		}
	}

	public ICloseableDialogController showHeavyheightDialog(final String maskURI,
															final String title,
															Modality modality,
															boolean closeable) throws IOException
	{
		if (Platform.isFxApplicationThread())
		{
			final Stage dialog = new Stage();
			if (modality != Modality.NONE)
			{
				dialog.initOwner(this.stage);
				dialog.initModality(modality);
			}
			dialog.setTitle(title);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.setResizable(false);

			URL location = MainController.class.getResource("/niobe/legion/client/fxml/DialogScene.fxml");

			FXMLLoader loader = new FXMLLoader(location);
			Scene scene = loader.load();
			DialogController controller = loader.getController();
			controller.setTitle(title);
			ICloseableDialogController childrenController = controller.loadMask(maskURI, closeable);

			childrenController.setStage(dialog);

			scene.getStylesheets()
				 .add(MainController.class.getResource("/niobe/legion/client/css/theme.css").toExternalForm());

			dialog.setScene(scene);
			ResizeDragListener.addResizeListener(dialog);
			dialog.show();

			dialog.setX(this.stage.getX() + this.stage.getWidth() / 2 - dialog.getWidth() / 2);
			dialog.setY(this.stage.getY() + this.stage.getHeight() / 2 - dialog.getHeight() / 2);

			return childrenController;
		} else
		{
			// array to wrap the result
			final ICloseableDialogController[] wrapper = new ICloseableDialogController[1];

			Platform.runLater(() -> {
				try
				{
					wrapper[0] = MainController.this.showHeavyheightDialog(maskURI, title, modality, closeable);
					synchronized (MainController.this)
					{
						MainController.this.notify();
					}
				}
				catch (IOException e)
				{
					Logger.exception(LegionLogger.STDERR, e);
				}
			});

			// wait for the dialog to load, we should be notified
			synchronized (MainController.this)
			{
				while (wrapper[0] == null)
				{
					try
					{
						this.wait();
					}
					catch (InterruptedException e)
					{
					}
				}
			}

			return wrapper[0];
		}
	}

	public void showDialog(final String message, ButtonType... buttons)
	{
		if (Platform.isFxApplicationThread())
		{
			final Stage dialog = new Stage();
			dialog.initOwner(this.stage);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initStyle(StageStyle.UNDECORATED);
			dialog.setResizable(false);

			HBox buttonBox = new HBox(5);

			if(buttons != null && buttons.length > 0)
			{
				Stream.of(buttons).forEach(button -> {

				});
			} else
			{
				Button okButton = new Button("OK");
				okButton.setOnAction(arg0 -> dialog.close());
				okButton.requestFocus();
				buttonBox.getChildren().add(okButton);
			}

			Label messageLabel = new Label(message);
			messageLabel.setTextAlignment(TextAlignment.CENTER);

			VBox content = new VBox(35, messageLabel, buttonBox);
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
			ResizeDragListener.addResizeListener(dialog);

			dialog.show();

			dialog.setX(this.stage.getX() + this.stage.getWidth() / 2 - dialog.getWidth() / 2);
			dialog.setY(this.stage.getY() + this.stage.getHeight() / 2 - dialog.getHeight() / 2);
		} else
		{
			Platform.runLater(() -> MainController.this.showDialog(message));
		}
	}

	@FXML
	private void maximize()
	{
		if (this.resizeDragListener != null)
		{
			this.resizeDragListener.toggleMaximized();
		}
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
