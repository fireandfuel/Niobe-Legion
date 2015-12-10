package niobe.legion.client.gui.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import niobe.legion.client.Client;
import niobe.legion.client.DatasetReceiver;
import niobe.legion.client.gui.databinding.FxDatasetColumn;
import niobe.legion.client.gui.databinding.FxDatasetWrapper;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.UserEntity;

import java.io.IOException;
import java.util.ArrayList;

public class UserAdminController implements DatasetReceiver<UserEntity>
{
	private static final FxDatasetColumn[] columns = new FxDatasetColumn[]{
			new FxDatasetColumn<Long>("id", Client.getLocalisation("id")),
			new FxDatasetColumn<String>("name", Client.getLocalisation("name")),
			new FxDatasetColumn<String>("group::name", Client.getLocalisation("group"))};

	private ObservableList<FxDatasetWrapper<UserEntity>> users =
			FXCollections.observableList(new ArrayList<FxDatasetWrapper<UserEntity>>());

	@FXML
	private TableView<FxDatasetWrapper<UserEntity>> userTable;

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
		UserEditorController controller = (UserEditorController) Client.getFxController().showHeavyWeightDialog(
				"/niobe/legion/client/fxml/tab/admin/UserEditor.fxml",
				Client.getLocalisation("newUser"),
				Modality.WINDOW_MODAL,
				true);
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void editUser() throws IOException
	{
		FxDatasetWrapper<UserEntity> wrapper = this.userTable.getSelectionModel().getSelectedItem();
		UserEditorController controller = (UserEditorController) Client.getFxController().showHeavyWeightDialog(
				"/niobe/legion/client/fxml/tab/admin/UserEditor.fxml",
				Client.getLocalisation("editUser"),
				Modality.WINDOW_MODAL,
				true);
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
	public void set(UserEntity dataset)
	{
		for (FxDatasetWrapper<UserEntity> userWrapper : this.users)
		{
			if (userWrapper.getData().getId() == dataset.getId())
			{
				userWrapper.setData(dataset);
				return;
			}
		}

		this.users.add(new FxDatasetWrapper<UserEntity>(dataset));
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
}
