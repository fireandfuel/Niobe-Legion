package cuina.legion.client.gui.tab;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import cuina.legion.client.gui.MainController;

public class TabViewController
{
	private Object tabController;

	@FXML
	private TabPane tabPane;

	@FXML
	private void initialize() throws IOException
	{
		this.tabPane.getTabs().add(this.loadTab(LegionTab.ADMIN));
	}

	private Tab loadTab(ITab tab) throws IOException
	{
		URL location = MainController.class.getResource(tab.getTabURI());
		FXMLLoader loader = new FXMLLoader(location);
		this.tabController = loader.getController();
		Object tabObject = loader.load();
		return (tabObject instanceof Tab) ? (Tab) tabObject : null;
	}
}
