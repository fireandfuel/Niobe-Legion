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

public class GroupAdminController implements DatasetReceiver
{
	private static final FxDatasetColumn[]                columns = new FxDatasetColumn[] {
			new FxDatasetColumn<Long>("id", "Nr."), new FxDatasetColumn<String>("name", "Name"),
			new FxDatasetColumn<String>("active", "aktiviert"),
			new FxDatasetColumn<String>("can_administrate", "Admin") };
	private              ObservableList<FxDatasetWrapper> groups  = FXCollections
			.observableList(new ArrayList<FxDatasetWrapper>());
	@FXML
	private TableView<FxDatasetWrapper> groupTable;

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
			Client.getCommunicator().getDataset(DatasetType.GROUP, this, false);
		} catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	@FXML
	private void newGroup() throws IOException
	{
		GroupEditorController controller = (GroupEditorController) Client.getFxController()
				.showFatDialog("/cuina/legion/client/fxml/admin/GroupEditor.fxml", "Neue Gruppe");
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void editGroup() throws IOException
	{
		FxDatasetWrapper wrapper = this.groupTable.getSelectionModel().getSelectedItem();
		GroupEditorController controller = (GroupEditorController) Client.getFxController()
				.showFatDialog("/cuina/legion/client/fxml/admin/GroupEditor.fxml",
						"Gruppe editieren");
		controller.setData(wrapper);
		controller.setDatasetRetriever(this);
	}

	@FXML
	private void deleteGroup() throws IOException
	{
		FxDatasetWrapper wrapper = this.groupTable.getSelectionModel().getSelectedItem();
		this.groups.remove(wrapper);
		Client.getCommunicator().removeDataset(DatasetType.GROUP, wrapper.getData().getLong("id"),
				this);
	}

	@Override
	public void add(Dataset dbObject)
	{
		FxDatasetWrapper wrapper = new FxDatasetWrapper(dbObject);
		this.groups.addAll(wrapper);
	}

	@Override
	public void addAll(final List<Dataset> dbObjects)
	{
		FxDatasetWrapper[] wrappers = new FxDatasetWrapper[dbObjects.size()];
		for(int i = 0; i < wrappers.length; i++)
		{
			wrappers[i] = new FxDatasetWrapper(dbObjects.get(i));
		}
		this.groups.addAll(wrappers);
	}

	@Override
	public void clear()
	{
		this.groups.clear();
	}

	@Override
	public void remove(Dataset dbObject)
	{
		int index = -1;

		for(int i = 0; i < this.groups.size(); i++)
		{
			FxDatasetWrapper wrapper = this.groups.get(i);
			if(wrapper.getData().equals(dbObject))
			{
				index = i;
				break;
			}
		}
		if(index > -1)
		{
			this.groups.remove(index);
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
