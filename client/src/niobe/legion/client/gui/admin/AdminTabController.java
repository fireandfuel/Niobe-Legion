package niobe.legion.client.gui.admin;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import niobe.legion.client.gui.IMaskType;
import niobe.legion.client.gui.MainController;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminTabController
{
	private static final List<IMaskType>     children = new ArrayList<IMaskType>(Arrays.asList(new AdminType("Nutzer",
																											 "/niobe/legion/client/fxml/admin/UserAdmin.fxml"),
																							   new AdminType("Gruppen",
																											 "/niobe/legion/client/fxml/admin/GroupAdmin.fxml"),
																							   new AdminType(
																									   "Server-Module",
																									   "/niobe/legion/client/fxml/admin/ModuleAdmin.fxml")));
	private              TreeItem<IMaskType> rootItem = new TreeItem<IMaskType>(new AdminType("Administration", null));
	private Object childController;

	@FXML private TreeView<IMaskType> selectionTree;

	@FXML private AnchorPane adminRoot;

	@FXML
	private void initialize()
	{
		if (this.selectionTree != null)
		{
			this.selectionTree.setRoot(this.rootItem);

			for (IMaskType child : AdminTabController.children)
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
				TreeItem<IMaskType> selected =
						AdminTabController.this.selectionTree.getSelectionModel().getSelectedItem();
				if (selected != null && selected.getValue() != null && selected.getValue().getMaskURI() != null)
				{
					String maskURI = selected.getValue().getMaskURI();
					AdminTabController.this.adminRoot.getChildren().clear();

					URL location = MainController.class.getResource(maskURI);

					if (location != null)
					{

						FXMLLoader loader = new FXMLLoader(location);
						try
						{
							Node node = loader.load();
							AnchorPane.setBottomAnchor(node, 0.0d);
							AnchorPane.setLeftAnchor(node, 0.0d);
							AnchorPane.setRightAnchor(node, 0.0d);
							AnchorPane.setTopAnchor(node, 0.0d);

							AdminTabController.this.adminRoot.getChildren().addAll(node);
							AdminTabController.this.childController = loader.getController();
						}
						catch (IOException e)
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
