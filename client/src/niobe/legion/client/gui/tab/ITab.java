package niobe.legion.client.gui.tab;

import java.net.URL;
import niobe.legion.shared.data.IRight;

/**
 * Only for internal use
 */
public interface ITab
{
    URL getTabURL();

    boolean isCloseable();

    IRight[] getNeededGroupRights();
}
