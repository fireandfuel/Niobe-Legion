package cuina.legion.client.gui.admin;

import cuina.legion.client.Client;
import cuina.legion.client.DatasetReceiver;
import cuina.legion.client.gui.FxDatasetColumn;
import cuina.legion.client.gui.FxDatasetWrapper;
import cuina.legion.shared.data.Dataset;
import cuina.legion.shared.data.DatasetType;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserAdminController implements DatasetReceiver
{
	private static FxDatasetColumn[]                columns = new FxDatasetColumn[] {
			new FxDatasetColumn<Long>("id", "Nr."), new FxDatasetColumn<String>("name", "Name"),
			new FxDatasetColumn<String>("group::name", "Gruppe") };
	private        ObservableList<FxDatasetWrapper> users   = FXCollections
			.observableList(new ArrayList<FxDatasetWrapper>());
	@FXML
	private TableView<FxDatasetWrapper> userTable;

	@FXML
	private void initialize()
	{
		this.userTable.setItems(this.users);
		this.userTable.getColumns().addAll(UserAdminController.columns);

		this.reloadUsers();
	}

	@FXML
	private void reloadUsers()
	{
		try
		{
			this.users.clear();
			Client.getCommunicator().getDataset(DatasetType.USER, this, true);
		} catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	@FXML
	private void newUser() throws IOException
	{
		UserEditorController controller = (UserEditorController) Client.getFxController()
				.showFatDialog("/cuina/legion/client/fxml/admin/UserEditor.fxml", "Neuer Benutzer");
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void editUser() throws IOException
	{
		FxDatasetWrapper wrapper = this.userTable.getSelectionModel().getSelectedItem();
		UserEditorController controller = (UserEditorController) Client.getFxController()
				.showFatDialog("/cuina/legion/client/fxml/admin/UserEditor.fxml",
						"Benutzer editieren");
		controller.setData(wrapper);
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void deleteUser() throws IOException
	{
		FxDatasetWrapper wrapper = this.userTable.getSelectionModel().getSelectedItem();
		this.users.remove(wrapper);
		Client.getCommunicator().removeDataset(DatasetType.USER, wrapper.getData().getLong("id"),
				this);
	}

	@Override
	public void add(Dataset dbObject)
	{
		FxDatasetWrapper wrapper = new FxDatasetWrapper(dbObject);
		this.users.addAll(wrapper);
	}

	@Override
	public void addAll(final List<Dataset> dbObjects)
	{
		FxDatasetWrapper[] wrappers = new FxDatasetWrapper[dbObjects.size()];
		for(int i = 0; i < wrappers.length; i++)
		{
			wrappers[i] = new FxDatasetWrapper(dbObjects.get(i));
		}
		this.users.addAll(wrappers);
	}

	@Override
	public void clear()
	{
		this.users.clear();
	}

	@Override
	public void remove(Dataset dbObject)
	{
		int index = -1;

		for(int i = 0; i < this.users.size(); i++)
		{
			FxDatasetWrapper wrapper = this.users.get(i);
			if(wrapper.getData().equals(dbObject))
			{
				index = i;
				break;
			}
		}
		if(index > -1)
		{
			this.users.remove(index);
		}
	}

	@Override
	public void removeAll(List<Dataset> dbObjects)
	{
		for(Dataset dataset : dbObjects)
		{
			this.remove(dataset);
		}
	}
}
