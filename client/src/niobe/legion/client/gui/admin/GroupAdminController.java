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
import niobe.legion.shared.model.GroupEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupAdminController implements DatasetReceiver<GroupEntity>
{
	private static final FxDatasetColumn[] columns = new FxDatasetColumn[]{
			new FxDatasetColumn<Long>("id", "Nr."), new FxDatasetColumn<String>("name", "Name")};

	private ObservableList<FxDatasetWrapper<GroupEntity>> groups =
			FXCollections.observableList(new ArrayList<FxDatasetWrapper<GroupEntity>>());

	@FXML
	private TableView<FxDatasetWrapper<GroupEntity>> groupTable;

	@FXML
	private void initialize()
	{
		this.groupTable.setItems(this.groups);
		this.groupTable.getColumns().addAll(GroupAdminController.columns);

		this.reloadGroups();
	}

	@FXML
	private void reloadGroups()
	{
		try
		{
			this.groups.clear();
			Client.getCommunicator().getDataset(GroupEntity.class, this, null, null);
		}
		catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	@FXML
	private void newGroup() throws IOException
	{
		GroupEditorController controller = (GroupEditorController) Client.getFxController().showHeavyheightDialog(
				"/niobe/legion/client/fxml/tab/admin/GroupEditor.fxml",
				"Neue Gruppe",
				Modality.WINDOW_MODAL,
				true);
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void editGroup() throws IOException
	{
		FxDatasetWrapper wrapper = this.groupTable.getSelectionModel().getSelectedItem();
		GroupEditorController controller = (GroupEditorController) Client.getFxController().showHeavyheightDialog(
				"/niobe/legion/client/fxml/tab/admin/GroupEditor.fxml",
				"Gruppe editieren",
				Modality.WINDOW_MODAL,
				true);
		controller.setData(wrapper);
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void deleteGroup() throws IOException
	{
		FxDatasetWrapper<GroupEntity> wrapper = this.groupTable.getSelectionModel().getSelectedItem();
		this.groups.remove(wrapper);
		Client.getCommunicator().removeDataset(wrapper.getData(), this);
	}

	@Override
	public void set(GroupEntity dataset)
	{
		for (FxDatasetWrapper<GroupEntity> groupWrapper : this.groups)
		{
			if (groupWrapper.getData().getId() == dataset.getId())
			{
				groupWrapper.setData(dataset);
				return;
			}
		}

		this.groups.add(new FxDatasetWrapper<GroupEntity>(dataset));
	}

	@Override
	public void clear()
	{
		this.groups.clear();
	}

	@Override
	public void remove(GroupEntity dbObject)
	{
		int index = -1;

		for (int i = 0; i < this.groups.size(); i++)
		{
			FxDatasetWrapper<GroupEntity> wrapper = this.groups.get(i);
			if (wrapper.getData().equals(dbObject))
			{
				index = i;
				break;
			}
		}
		if (index > -1)
		{
			this.groups.remove(index);
		}
	}

	@Override
	public void removeAll(List<GroupEntity> dbObjects)
	{
		dbObjects.forEach(this::remove);
	}
}
