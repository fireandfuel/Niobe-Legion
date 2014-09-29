package cuina.legion.client.gui.admin;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import cuina.legion.client.Client;
import cuina.legion.client.DatasetReceiver;
import cuina.legion.client.gui.FxDatasetWrapper;
import cuina.legion.client.gui.ICloseableDialogController;
import cuina.legion.shared.data.Dataset;
import cuina.legion.shared.data.DatasetType;

public class GroupEditorController implements ICloseableDialogController
{
	@FXML
	private TextField groupName;

	@FXML
	private CheckBox active;

	@FXML
	private Button saveButton;

	private DatasetReceiver datasetRetriever;
	private Dataset dataset;

	private final EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>()
	{
		@Override
		public void handle(KeyEvent arg0)
		{
			GroupEditorController.this.saveButton
					.setDisable(GroupEditorController.this.groupName == null
							|| GroupEditorController.this.groupName.getText().length() < 2);
		}
	};

	private Stage stage;

	@FXML
	private void initialize()
	{
		this.groupName.addEventHandler(KeyEvent.KEY_RELEASED, this.keyHandler);

		if(this.groupName.getText() == null || this.groupName.getText().length() < 2)
		{
			this.saveButton.setDisable(true);
		}
	}

	public void setData(FxDatasetWrapper wrapper)
	{
		this.dataset = wrapper.getData();
		this.groupName.setText(this.dataset.getString("name"));
		this.active.setSelected(this.dataset.getBoolean("active"));
	}

	@FXML
	private void save() throws IOException
	{
		if(this.dataset == null)
		{
			this.dataset = new Dataset(DatasetType.GROUP);
			this.dataset.set("id", null);
		}
		this.dataset.set("name", this.groupName.getText());
		this.dataset.set("active", this.active.isSelected());
		Client.getCommunicator().setDataset(this.dataset, this.datasetRetriever);
		this.close();
	}

	@FXML
	private void cancel()
	{
		this.close();
	}

	public void setDatasetRetriever(DatasetReceiver datasetRetriever)
	{
		this.datasetRetriever = datasetRetriever;
	}

	@Override
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	@Override
	public void close()
	{
		if(this.stage != null)
		{
			this.stage.close();
		}
	}

}
