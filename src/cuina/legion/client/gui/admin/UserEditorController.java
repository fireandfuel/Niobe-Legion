package cuina.legion.client.gui.admin;

import java.io.IOException;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import cuina.legion.client.Client;
import cuina.legion.client.DatasetReceiver;
import cuina.legion.client.gui.FxDatasetWrapper;
import cuina.legion.client.gui.ICloseableDialogController;
import cuina.legion.shared.data.Dataset;
import cuina.legion.shared.data.DatasetType;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;

public class UserEditorController implements ICloseableDialogController, DatasetReceiver
{
	@FXML
	private TextField userName;

	@FXML
	private PasswordField password;

	@FXML
	private ComboBox<FxDatasetWrapper> group;

	@FXML
	private Button saveButton;

	private DatasetReceiver datasetRetriever;
	private Dataset dataset;

	private final EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>()
	{
		@Override
		public void handle(KeyEvent arg0)
		{
			UserEditorController.this.saveButton
					.setDisable((UserEditorController.this.password == null || UserEditorController.this.password
							.getText().length() <= 7)
							|| (UserEditorController.this.userName == null || UserEditorController.this.userName
									.getText().length() <= 1));
		}
	};

	private Stage stage;

	@FXML
	private void initialize()
	{
		this.userName.addEventHandler(KeyEvent.KEY_RELEASED, this.keyHandler);
		this.password.addEventHandler(KeyEvent.KEY_RELEASED, this.keyHandler);

		try
		{
			Client.getCommunicator().getDataset(DatasetType.GROUP, this);
		} catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}

		if((this.password.getText() == null || this.password.getText().length() < 8)
				|| (this.userName.getText() == null || this.userName.getText().length() < 2))
		{
			this.saveButton.setDisable(true);
		}
	}

	public void setData(FxDatasetWrapper wrapper)
	{
		this.dataset = wrapper.getData();
		this.userName.setText(this.dataset.getString("name"));
		this.password.setText(this.dataset.getString("password"));

	}

	@FXML
	private void save() throws IOException
	{
		if(this.dataset == null)
		{
			this.dataset = new Dataset(DatasetType.USER);
			this.dataset.set("id", null);
		}
		this.dataset.set("name", this.userName.getText());
		this.dataset.set("password", this.password.getText());
		this.dataset.set("group", this.group.getSelectionModel().getSelectedItem().getData()
				.getInteger("id"));
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

	@Override
	public void add(Dataset dataset)
	{
		this.group.getItems().addAll(new FxDatasetWrapper(dataset, "name"));
	}

	@Override
	public void addAll(List<Dataset> datasets)
	{
		this.group.getItems().addAll(FxDatasetWrapper.asList(datasets, "name"));
	}

	@Override
	public void clear()
	{
		this.group.getItems().clear();
	}

	@Override
	public void remove(Dataset dataset)
	{
	}

	@Override
	public void removeAll(List<Dataset> datasets)
	{
	}
}
