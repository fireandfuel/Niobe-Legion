package niobe.legion.client.gui.tab;

import niobe.legion.shared.data.IRight;

import java.net.URL;

/**
 * Only for internal use
 */
public interface ITab
{
	URL getTabURL();

	boolean isCloseable();

	IRight[] getNeededGroupRights();
}
