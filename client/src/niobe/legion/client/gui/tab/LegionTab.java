/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LegionTab.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.tab;

import java.net.URL;
import niobe.legion.shared.data.IRight;
import niobe.legion.shared.data.LegionRight;

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
