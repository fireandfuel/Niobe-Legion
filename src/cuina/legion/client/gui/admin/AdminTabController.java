package cuina.legion.client.gui.admin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import cuina.legion.client.gui.IMaskType;
import cuina.legion.client.gui.MainController;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;

public class AdminTabController
{
	private TreeItem<IMaskType> rootItem = new TreeItem<IMaskType>(new AdminType("Administration",
			null));

	private static final List<IMaskType> children = new ArrayList<IMaskType>(Arrays.asList(
			new AdminType("Nutzer", "/cuina/legion/client/fxml/admin/UserAdmin.fxml"),
			new AdminType("Gruppen", "/cuina/legion/client/fxml/admin/GroupAdmin.fxml"),
			new AdminType("Module", "/cuina/legion/client/fxml/admin/ModuleAdmin.fxml")));

	private Object childController;

	@FXML
	private TreeView<IMaskType> selectionTree;

	@FXML
	private AnchorPane adminRoot;

	@FXML
	private void initialize()
	{
		if(this.selectionTree != null)
		{
			this.selectionTree.setRoot(this.rootItem);

			for(IMaskType child : AdminTabController.children)
			{
				this.rootItem.getChildren().add(new TreeItem<IMaskType>(child));
			}
			this.rootItem.setExpanded(true);
		}

		this.selectionTree.setOnMouseClicked(new EventHandler<MouseEvent>()
		{

			@Override
			public void handle(MouseEvent arg0)
			{
				TreeItem<IMaskType> selected = AdminTabController.this.selectionTree
						.getSelectionModel().getSelectedItem();
				if(selected != null && selected.getValue() != null
						&& selected.getValue().getMaskURI() != null)
				{
					String maskURI = selected.getValue().getMaskURI();
					AdminTabController.this.adminRoot.getChildren().clear();

					URL location = MainController.class.getResource(maskURI);

					if(location != null)
					{

						FXMLLoader loader = new FXMLLoader(location);
						try
						{
							Node node = (Node) loader.load();
							AnchorPane.setBottomAnchor(node, 0.0d);
							AnchorPane.setLeftAnchor(node, 0.0d);
							AnchorPane.setRightAnchor(node, 0.0d);
							AnchorPane.setTopAnchor(node, 0.0d);

							AdminTabController.this.adminRoot.getChildren().addAll(node);
							AdminTabController.this.childController = loader.getController();
						} catch (IOException e)
						{
							Logger.exception(LegionLogger.STDERR, e);
						}
					}
				}
			}
		});
	}

	public Object getChildController()
	{
		return this.childController;
	}
}
