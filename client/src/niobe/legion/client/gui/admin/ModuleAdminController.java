package niobe.legion.client.gui.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import niobe.legion.client.Client;
import niobe.legion.client.DatasetReceiver;
import niobe.legion.client.gui.databinding.FxDatasetColumn;
import niobe.legion.client.gui.databinding.FxDatasetWrapper;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.ModuleEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleAdminController implements DatasetReceiver<ModuleEntity>
{
	private static final FxDatasetColumn[] columns = new FxDatasetColumn[]{
			new FxDatasetColumn<Long>("id", "Nr."), new FxDatasetColumn<String>("name", "Name"),
			new FxDatasetColumn<Long>("qualified_name", "vollständige Bezeichnung"),
			new FxDatasetColumn<String>("version", "Version"), new FxDatasetColumn<String>("activated", "aktiviert")};

	private ObservableList<FxDatasetWrapper<ModuleEntity>> modules =
			FXCollections.observableList(new ArrayList<FxDatasetWrapper<ModuleEntity>>());

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
			Client.getCommunicator().getDataset(ModuleEntity.class, this, null, null);
		}
		catch (IOException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}
	}

	@Override
	public void set(ModuleEntity dataset)
	{
		for (FxDatasetWrapper<ModuleEntity> moduleWrapper : this.modules)
		{
			if (moduleWrapper.getData().getId() == dataset.getId())
			{
				moduleWrapper.setData(dataset);
				return;
			}
		}

		this.modules.add(new FxDatasetWrapper<ModuleEntity>(dataset));
	}

	@Override
	public void clear()
	{
		this.modules.clear();
	}

	@Override
	public void remove(ModuleEntity dbObject)
	{
		int index = -1;

		for (int i = 0; i < this.modules.size(); i++)
		{
			FxDatasetWrapper<ModuleEntity> wrapper = this.modules.get(i);
			if (wrapper.getData().equals(dbObject))
			{
				index = i;
				break;
			}
		}
		if (index > -1)
		{
			this.modules.remove(index);
		}
	}

	@Override
	public void removeAll(List<ModuleEntity> dbObjects)
	{
		dbObjects.forEach(this::remove);
	}
}
