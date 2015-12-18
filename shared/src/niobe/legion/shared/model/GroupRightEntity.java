/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (GroupRightEntity.java) is part of Niobe Legion (module niobe-legion-shared).
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

package niobe.legion.shared.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity(name = "legion_group_right")
@NamedQueries({@NamedQuery(name = "group_right.get",
                           query = "SELECT entity FROM legion_group_right entity where entity.id = :id")})
public class GroupRightEntity implements IEntity
{
    @Id
    @Column(name = "id")
    @GeneratedValue
    private int id;

    private String name;
    @Transient
    private String displayName;
    private boolean active;

    @OneToMany(cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId",
                referencedColumnName = "id")
    private List<GroupRightEntity> children;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        if(name != null)
        {
            String[] splittedName = name.split("\\.");
            this.displayName = splittedName[splittedName.length - 1];
        }
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public List<GroupRightEntity> getChildren()
    {
        return children;
    }

    public void setChildren(List<GroupRightEntity> children)
    {
        this.children = children;
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public void setId(int id)
    {
        this.id = id;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }
}
