package niobe.legion.client.gui.admin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import niobe.legion.client.Client;
import niobe.legion.client.DatasetReceiver;
import niobe.legion.client.gui.ICloseableDialogController;
import niobe.legion.client.gui.databinding.FxDatasetTreeColumn;
import niobe.legion.client.gui.databinding.FxDatasetWrapper;
import niobe.legion.shared.data.IRight;
import niobe.legion.shared.model.GroupEntity;
import niobe.legion.shared.model.GroupRightEntity;
import niobe.legion.shared.module.ModuleRightManager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupEditorController implements ICloseableDialogController, DatasetReceiver<GroupRightEntity>
{
	private final static FxDatasetTreeColumn[] columns = new FxDatasetTreeColumn[]{
			new FxDatasetTreeColumn<String>("displayName", Client.getLocalisation("name")),
			new FxDatasetTreeColumn<Boolean>("active", Client.getLocalisation("activated"), true)};

	@FXML
	private TextField groupName;

	@FXML
	private CheckBox active;

	@FXML
	private Button saveButton;

	@FXML
	private TreeTableView<FxDatasetWrapper<GroupRightEntity>> groupRights;

	private DatasetReceiver<GroupEntity> datasetRetriever;
	private GroupEntity                  dataset;

	private final EventHandler<KeyEvent> keyHandler = (event) -> GroupEditorController.this.saveButton.setDisable(
			GroupEditorController.this.groupName == null ||
			GroupEditorController.this.groupName.getText().length() < 2);

	private Stage stage;

	@FXML
	private void initialize() throws IOException
	{
		this.groupName.addEventHandler(KeyEvent.KEY_RELEASED, this.keyHandler);

		this.groupRights.getColumns().addAll(GroupEditorController.columns);
		this.groupRights.setShowRoot(false);
		this.groupRights.setRoot(this.createRightNodes(ModuleRightManager.getRights()));
		this.groupRights.setEditable(true);

		Client.getCommunicator().getDataset(GroupRightEntity.class, this, null, null);
	}

	public void setData(FxDatasetWrapper<GroupEntity> wrapper)
	{
		this.dataset = wrapper.getData();
		this.groupName.setText(this.dataset.getName());
		this.active.setSelected(this.dataset.isActive());

		this.setRightNodes(this.dataset.getRights());

		this.saveButton.setDisable(this.groupName.getText() == null || this.groupName.getText().length() < 2);
	}

	private TreeItem<FxDatasetWrapper<GroupRightEntity>> createRightNodes(IRight[] rights)
	{
		if (rights != null)
		{
			List<TreeItem<FxDatasetWrapper<GroupRightEntity>>> groupRightEntities =
					Stream.of(rights).filter(IRight::isRoot).map(this::createRightNode).collect(Collectors.toList());

			TreeItem<FxDatasetWrapper<GroupRightEntity>> root = new TreeItem<>();
			root.getChildren().addAll(groupRightEntities);

			return root;
		}
		return null;
	}

	private TreeItem<FxDatasetWrapper<GroupRightEntity>> createRightNode(IRight right)
	{
		GroupRightEntity rightEntity = new GroupRightEntity();
		rightEntity.setName(right.getName());

		FxDatasetWrapper<GroupRightEntity> wrapper = new FxDatasetWrapper<GroupRightEntity>(rightEntity);

		TreeItem<FxDatasetWrapper<GroupRightEntity>> item = new TreeItem<FxDatasetWrapper<GroupRightEntity>>(wrapper);

		ChangeListener<Boolean> activeChangeListener =
				(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					// change will automatically be bubbled up (select) or down (deselect)
					if (!oldValue && newValue)
					{
						if (item.getParent() != null && item.getParent().getValue() != null)
						{
							item.getParent().getValue().getProperty("active").setValue(newValue);
						}
					} else if (!newValue && oldValue)
					{
						if (item.getChildren() != null)
						{
							item.getChildren().forEach(child -> {
								if (child.getValue() != null)
								{
									child.getValue().getProperty("active").setValue(newValue);
								}
							});
						}
					}

				};

		wrapper.getProperty("active").addListener((ChangeListener) activeChangeListener);
		if (!right.isLeaf())
		{
			right.getChildren().forEach(child -> {
				TreeItem<FxDatasetWrapper<GroupRightEntity>> childItem = this.createRightNode(child);
				item.getChildren().add(childItem);
			});
		}

		return item;
	}

	private void setRightNodes(List<GroupRightEntity> rights)
	{
		if (rights != null)
		{
			rights.forEach(rightNode -> this.setRightNode(rightNode, this.groupRights.getRoot()));
		}
	}

	private boolean setRightNode(GroupRightEntity rightNode, TreeItem<FxDatasetWrapper<GroupRightEntity>> item)
	{
		if (item.getValue() != null && item.getValue().getProperty("name") != null &&
			rightNode.getName().equals(item.getValue().getProperty("name").getValue()))
		{
			item.getValue().setData(rightNode);
			return true;
		}
		for (TreeItem<FxDatasetWrapper<GroupRightEntity>> child : item.getChildren())
		{
			if (setRightNode(rightNode, child))
			{
				return true;
			}
		}
		return false;
	}

	@FXML
	private void save() throws IOException
	{
		if (this.dataset == null)
		{
			this.dataset = new GroupEntity();
		}
		this.dataset.setName(this.groupName.getText());
		this.dataset.setActive(this.active.isSelected());
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
		if (this.stage != null)
		{
			this.stage.close();
		}
	}

	@Override
	public void set(GroupRightEntity dataset)
	{

	}

	@Override
	public void clear()
	{

	}

	@Override
	public void remove(GroupRightEntity dataset)
	{

	}
}
