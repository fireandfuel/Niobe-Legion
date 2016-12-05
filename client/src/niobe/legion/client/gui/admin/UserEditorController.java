/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (UserEditorController.java) is part of Niobe Legion (module niobe-legion-client_main).
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import niobe.legion.client.Client;
import niobe.legion.client.DatasetReceiver;
import niobe.legion.client.gui.ICloseableDialogController;
import niobe.legion.client.gui.databinding.FxDatasetWrapper;
import niobe.legion.shared.model.GroupEntity;
import niobe.legion.shared.model.UserEntity;

public class UserEditorController implements ICloseableDialogController, DatasetReceiver<GroupEntity>
{
    @FXML
    private TextField userName;

    @FXML
    private PasswordField password;

    private final EventHandler eventHandler = (event) -> UserEditorController.this.saveButton
            .setDisable((UserEditorController.this.password == null || UserEditorController.this.password.getText()
                    .length() <= 7) || (UserEditorController.this.userName == null || UserEditorController.this.userName
                    .getText().length() <= 1) || UserEditorController.this.group.getSelectionModel()
                    .getSelectedItem() == null);

    @FXML
    private ComboBox<FxDatasetWrapper<GroupEntity>> group;

    @FXML
    private Button saveButton;
    private DatasetReceiver datasetRetriever;
    private UserEntity dataset;
    private Stage stage;

    @FXML
    private void initialize() throws IOException
    {
        this.userName.addEventHandler(KeyEvent.KEY_RELEASED, this.eventHandler);
        this.password.addEventHandler(KeyEvent.KEY_RELEASED, this.eventHandler);

        this.saveButton.setDisable(true);

        this.group.getItems().clear();

        this.group.setOnAction(this.eventHandler);

        Client.getCommunicator().getDataset(GroupEntity.class, this, null, null);

        this.group.converterProperty().setValue(new StringConverter<FxDatasetWrapper<GroupEntity>>()
        {
            @Override
            public String toString(FxDatasetWrapper<GroupEntity> object)
            {
                return object.getData().getName();
            }

            @Override
            public FxDatasetWrapper<GroupEntity> fromString(String string)
            {
                return null;
            }
        });
    }

    public void setData(FxDatasetWrapper<UserEntity> wrapper)
    {
        this.dataset = wrapper.getData();
        this.userName.setText(this.dataset.getName());
        this.password.setText(this.dataset.getPassword());
        this.group.getSelectionModel().select(wrapper.getNested("group"));

        this.saveButton
                .setDisable((this.password.getText() == null || this.password.getText().length() < 8) || (this.userName
                        .getText() == null || this.userName.getText().length() < 2) || this.group.getSelectionModel()
                        .getSelectedItem() == null);
    }

    @FXML
    private void save() throws IOException
    {
        if(this.dataset == null)
        {
            this.dataset = new UserEntity();
        }
        this.dataset.setName(this.userName.getText());
        this.dataset.setPassword(this.password.getText());
        this.dataset.setGroup(this.group.getSelectionModel().getSelectedItem().getData());
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
    public void set(GroupEntity dataset)
    {
        this.group.getItems().add(new FxDatasetWrapper<GroupEntity>(dataset, "name"));
    }

    @Override
    public void clear()
    {
        this.group.getItems().clear();
    }

    @Override
    public void remove(GroupEntity dbObject)
    {
        int index = -1;

        for(int i = 0; i < this.group.getItems().size(); i++)
        {
            FxDatasetWrapper<GroupEntity> wrapper = this.group.getItems().get(i);
            if(wrapper.getData().equals(dbObject))
            {
                index = i;
                break;
            }
        }
        if(index > -1)
        {
            this.group.getItems().remove(index);
        }
    }
}
