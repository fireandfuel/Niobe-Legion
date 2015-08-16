package niobe.legion.client.gui.tab;

public enum LegionTab implements ITab
{
	ADMIN("/niobe/legion/client/fxml/tab/admin/AdminTab.fxml");

	private String tabURI;

	private LegionTab(String tabURI)
	{
		this.tabURI = tabURI;
	}

	@Override
	public String getTabURI()
	{
		return this.tabURI;
	}
}
