/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LegionRight.java) is part of Niobe Legion (module niobe-legion-shared).
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
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared.data;

import java.util.ArrayList;
import java.util.List;

public enum LegionRight implements IRight
{
    USER_RIGHT("user_right", null),
    LOGIN("login", USER_RIGHT),
    CHANGE_OWN_PASSWORD("change_own_password", USER_RIGHT),

    ADMINISTRATION("administration", null),
    SERVER_ADMINISTRATION("server_administration", ADMINISTRATION),
    STOP_SERVER("stop_server", SERVER_ADMINISTRATION),
    RESTART_SERVER("restart_server", SERVER_ADMINISTRATION),

    USER_ADMINISTRATION("user_administration", ADMINISTRATION),
    ADD_USER("add_user", USER_ADMINISTRATION),
    RENAME_USER("rename_user", USER_ADMINISTRATION),
    DELETE_USER("delete_user", USER_ADMINISTRATION),
    SET_USER_PASSWORD("set_user_password", USER_ADMINISTRATION),
    SET_USER_GROUP("set_user_group", USER_ADMINISTRATION),

    GROUP_ADMINISTRATION("group_administration", ADMINISTRATION),
    ADD_GROUP("add_group", GROUP_ADMINISTRATION),
    RENAME_GROUP("rename_group", GROUP_ADMINISTRATION),
    DELETE_GROUP("delete_group", GROUP_ADMINISTRATION),

    RIGHT_ADMINISTRATION("right_administration", GROUP_ADMINISTRATION),
    SET_RIGHT("set_right", RIGHT_ADMINISTRATION),
    UNSET_RIGHT("unset_right", RIGHT_ADMINISTRATION);

    private String name;
    private IRight parent;

    private transient List<IRight> children;

    LegionRight(String name, IRight parent)
    {
        this.name = name;
        this.parent = parent;

        if(this.parent != null)
        {
            this.parent.addChildren(this);
        }
    }

    @Override
    public String getName()
    {
        return (this.getParent() != null) ? (this.getParent().getName() + "." + this.name) : this.name;
    }

    @Override
    public IRight getParent()
    {
        return this.parent;
    }

    @Override
    public List<IRight> getChildren()
    {
        return this.children;
    }

    @Override
    public void addChildren(IRight right)
    {
        if(this.children == null)
        {
            this.children = new ArrayList<IRight>();
        }
        this.children.add(right);
    }
}