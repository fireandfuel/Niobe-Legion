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

public class ModuleAdminController implements DatasetReceiver
{
	private static final FxDatasetColumn[]                columns = new FxDatasetColumn[] {
			new FxDatasetColumn<Long>("id", "Nr."), new FxDatasetColumn<String>("name", "Name"),
			new FxDatasetColumn<Long>("qualified_name", "vollständige Bezeichnung"),
			new FxDatasetColumn<String>("version", "Version"),
			new FxDatasetColumn<String>("activated", "aktiviert") };
	private              ObservableList<FxDatasetWrapper> modules = FXCollections
			.observableList(new ArrayList<FxDatasetWrapper>());

	@FXML
	private TableView moduleTable;

	@FXML
	private void initialize()
	{
		this.moduleTable.setItems(this.modules);
		this.moduleTable.getColumns().addAll(ModuleAdminController.columns);

		this.reloadModules();
	}

	@FXML
	private void reloadModules()
	{
		try
		{
			this.modules.clear();
			Client.getCommunicator().getDataset(DatasetType.MODULE, this, true);
		} catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	@Override
	public void add(Dataset dbObject)
	{
		FxDatasetWrapper wrapper = new FxDatasetWrapper(dbObject);
		this.modules.addAll(wrapper);
	}

	@Override
	public void addAll(final List<Dataset> dbObjects)
	{
		FxDatasetWrapper[] wrappers = new FxDatasetWrapper[dbObjects.size()];
		for(int i = 0; i < wrappers.length; i++)
		{
			wrappers[i] = new FxDatasetWrapper(dbObjects.get(i));
		}
		this.modules.addAll(wrappers);
	}

	@Override
	public void clear()
	{
		this.modules.clear();
	}

	@Override
	public void remove(Dataset dbObject)
	{
		int index = -1;

		for(int i = 0; i < this.modules.size(); i++)
		{
			FxDatasetWrapper wrapper = this.modules.get(i);
			if(wrapper.getData().equals(dbObject))
			{
				index = i;
				break;
			}
		}
		if(index > -1)
		{
			this.modules.remove(index);
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
