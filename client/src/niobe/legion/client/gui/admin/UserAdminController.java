package niobe.legion.client.gui.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import niobe.legion.client.Client;
import niobe.legion.client.DatasetReceiver;
import niobe.legion.client.gui.FxDatasetColumn;
import niobe.legion.client.gui.FxDatasetWrapper;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.UserEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserAdminController implements DatasetReceiver<UserEntity>
{
	private static final FxDatasetColumn[] columns = new FxDatasetColumn[]{
			new FxDatasetColumn<Long>("id", "Nr."), new FxDatasetColumn<String>("name", "Name"),
			new FxDatasetColumn<String>("group::name", "Gruppe")};

	private ObservableList<FxDatasetWrapper> users = FXCollections.observableList(new ArrayList<FxDatasetWrapper>());

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
			Client.getCommunicator().getDataset(UserEntity.class, this, null, null);
		}
		catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	@FXML
	private void newUser() throws IOException
	{
		UserEditorController controller = (UserEditorController) Client.getFxController().showFatDialog(
				"/niobe/legion/client/fxml/admin/UserEditor.fxml",
				"Neuer Benutzer");
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void editUser() throws IOException
	{
		FxDatasetWrapper<UserEntity> wrapper = this.userTable.getSelectionModel().getSelectedItem();
		UserEditorController controller = (UserEditorController) Client.getFxController().showFatDialog(
				"/niobe/legion/client/fxml/admin/UserEditor.fxml",
				"Benutzer editieren");
		controller.setData(wrapper);
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void deleteUser() throws IOException
	{
		FxDatasetWrapper<UserEntity> wrapper = this.userTable.getSelectionModel().getSelectedItem();
		this.users.remove(wrapper);
		Client.getCommunicator().removeDataset(wrapper.getData(), this);
	}

	@Override
	public void add(UserEntity dbObject)
	{
		FxDatasetWrapper<UserEntity> wrapper = new FxDatasetWrapper<UserEntity>(dbObject);
		this.users.addAll(wrapper);
	}

	@Override
	public void addAll(final List<UserEntity> dbObjects)
	{
		FxDatasetWrapper<UserEntity>[] wrappers = new FxDatasetWrapper[dbObjects.size()];
		for (int i = 0; i < wrappers.length; i++)
		{
			wrappers[i] = new FxDatasetWrapper<UserEntity>(dbObjects.get(i));
		}
		this.users.addAll(wrappers);
	}

	@Override
	public void clear()
	{
		this.users.clear();
	}

	@Override
	public void remove(UserEntity dbObject)
	{
		int index = -1;

		for (int i = 0; i < this.users.size(); i++)
		{
			FxDatasetWrapper<UserEntity> wrapper = this.users.get(i);
			if (wrapper.getData().equals(dbObject))
			{
				index = i;
				break;
			}
		}
		if (index > -1)
		{
			this.users.remove(index);
		}
	}

	@Override
	public void removeAll(List<UserEntity> dbObjects)
	{
		dbObjects.forEach(this::remove);
	}
}
