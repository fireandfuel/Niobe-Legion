package niobe.legion.client.gui.admin;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import niobe.legion.client.Client;
import niobe.legion.client.DatasetReceiver;
import niobe.legion.client.gui.FxDatasetWrapper;
import niobe.legion.client.gui.ICloseableDialogController;
import niobe.legion.shared.model.GroupEntity;

import java.io.IOException;

public class GroupEditorController implements ICloseableDialogController
{
	@FXML
	private TextField groupName;

	@FXML
	private CheckBox active;

	@FXML
	private Button saveButton;

	private DatasetReceiver<GroupEntity> datasetRetriever;
	private GroupEntity                  dataset;

	private final EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>()
	{
		@Override
		public void handle(KeyEvent arg0)
		{
			GroupEditorController.this.saveButton.setDisable(GroupEditorController.this.groupName == null ||
															 GroupEditorController.this.groupName.getText().length() <
															 2);
		}
	};

	private Stage stage;

	@FXML
	private void initialize()
	{
		this.groupName.addEventHandler(KeyEvent.KEY_RELEASED, this.keyHandler);

		if (this.groupName.getText() == null || this.groupName.getText().length() < 2)
		{
			this.saveButton.setDisable(true);
		}
	}

	public void setData(FxDatasetWrapper<GroupEntity> wrapper)
	{
		this.dataset = wrapper.getData();
		this.groupName.setText(this.dataset.getName());
		this.active.setSelected(this.dataset.isActive());
	}

	@FXML
	private void save() throws IOException
	{
		if (this.dataset == null)
		{
			this.dataset = new GroupEntity();
		}
		this.dataset.setName(this.groupName.getText());
		this.dataset.setActive(this.active.isSelected());
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
		if (this.stage != null)
		{
			this.stage.close();
		}
	}

}
