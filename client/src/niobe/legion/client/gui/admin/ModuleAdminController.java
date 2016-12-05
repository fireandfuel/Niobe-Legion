/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ModuleAdminController.java) is part of Niobe Legion (module niobe-legion-client_main).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.client.gui.admin;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import niobe.legion.client.Client;
import niobe.legion.client.DatasetReceiver;
import niobe.legion.client.gui.databinding.FxDatasetColumn;
import niobe.legion.client.gui.databinding.FxDatasetWrapper;
import niobe.legion.shared.model.ModuleEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModuleAdminController implements DatasetReceiver<ModuleEntity>
{
    private final static Logger LOG = LogManager.getLogger(ModuleAdminController.class);

    private final static FxDatasetColumn[] columns = new FxDatasetColumn[]{new FxDatasetColumn<Long>("id",
                                                                                                     Client.getLocalisation(
                                                                                                             "id"),
                                                                                                     50), new FxDatasetColumn<String>(
            "name",
            Client.getLocalisation("name"),
            200), new FxDatasetColumn<Long>("qualified_name",
                                            Client.getLocalisation("qualifiedName"),
                                            200), new FxDatasetColumn<String>("version",
                                                                              Client.getLocalisation("version"),
                                                                              100), new FxDatasetColumn<String>(
            "activated",
            Client.getLocalisation("activated"),
            50)};

    private ObservableList<FxDatasetWrapper<ModuleEntity>> modules = FXCollections
            .observableList(new ArrayList<FxDatasetWrapper<ModuleEntity>>());

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
        } catch(IOException e)
        {
            LOG.catching(e);
        }
    }

    @Override
    public void set(ModuleEntity dataset)
    {
        for(FxDatasetWrapper<ModuleEntity> moduleWrapper : this.modules)
        {
            if(moduleWrapper.getData().getId() == dataset.getId())
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

        for(int i = 0; i < this.modules.size(); i++)
        {
            FxDatasetWrapper<ModuleEntity> wrapper = this.modules.get(i);
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
}
