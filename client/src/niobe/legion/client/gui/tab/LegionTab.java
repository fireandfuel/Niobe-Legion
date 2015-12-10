package niobe.legion.client.gui.tab;

import niobe.legion.shared.data.IRight;
import niobe.legion.shared.data.LegionRight;

import java.net.URL;

public enum LegionTab implements ITab
{
	WELCOME("/niobe/legion/client/fxml/tab/Welcome.fxml", true, LegionRight.LOGIN),
	ADMIN("/niobe/legion/client/fxml/tab/admin/AdminTab.fxml", false, LegionRight.ADMINISTRATION);

	private String tabURI;
	private boolean closeable;
	private IRight[] neededRights;

	LegionTab(String tabURI, boolean closeable, IRight... neededRights)
	{
		this.tabURI = tabURI;
		this.closeable = closeable;
		this.neededRights = neededRights;
	}

	@Override
	public URL getTabURL()
	{
		return LegionTab.class.getResource(this.tabURI);
	}

	@Override
	public boolean isCloseable()
	{
		return this.closeable;
	}

	public IRight[] getNeededGroupRights()
	{
		return this.neededRights;
	}
}
