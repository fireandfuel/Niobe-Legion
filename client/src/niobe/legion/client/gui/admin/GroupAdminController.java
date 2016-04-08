/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (GroupAdminController.java) is part of Niobe Legion (module niobe-legion-client).
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
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.client.gui.admin;

import java.io.IOException;
import java.util.ArrayList;
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

public class GroupAdminController implements DatasetReceiver<GroupEntity>
{
    private static final FxDatasetColumn[] columns = new FxDatasetColumn[]{new FxDatasetColumn<Long>("id",
                                                                                                     Client.getLocalisation(
                                                                                                             "id"),
                                                                                                     50), new FxDatasetColumn<String>(
            "name",
            Client.getLocalisation("name"),
            400)};

    private ObservableList<FxDatasetWrapper<GroupEntity>> groups = FXCollections
            .observableList(new ArrayList<FxDatasetWrapper<GroupEntity>>());

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
        } catch(IOException e)
        {
            Logger.exception(LegionLogger.STDERR, e);
        }
    }

    @FXML
    private void newGroup() throws IOException
    {
        GroupEditorController controller = (GroupEditorController) Client.getFxController().showHeavyWeightDialog(
                "/niobe/legion/client/fxml/tab/admin/GroupEditor.fxml",
                Client.getLocalisation("newGroup"),
                Modality.WINDOW_MODAL,
                true,
                600,
                400);
        controller.setDatasetRetriever(this);
    }

    @FXML
    private void editGroup() throws IOException
    {
        FxDatasetWrapper wrapper = this.groupTable.getSelectionModel().getSelectedItem();
        GroupEditorController controller = (GroupEditorController) Client.getFxController().showHeavyWeightDialog(
                "/niobe/legion/client/fxml/tab/admin/GroupEditor.fxml",
                Client.getLocalisation("editGroup"),
                Modality.WINDOW_MODAL,
                true,
                600,
                400);
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
        for(FxDatasetWrapper<GroupEntity> groupWrapper : this.groups)
        {
            if(groupWrapper.getData().getId() == dataset.getId())
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

        for(int i = 0; i < this.groups.size(); i++)
        {
            FxDatasetWrapper<GroupEntity> wrapper = this.groups.get(i);
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
}
