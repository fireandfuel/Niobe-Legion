/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (GroupEntity.java) is part of Niobe Legion (module niobe-legion-shared).
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

@Entity(name = "legion_group")
@NamedQueries({@NamedQuery(name = "group.get",
                           query = "SELECT entity FROM legion_group entity"), @NamedQuery(name = "group.getWithId",
                                                                                          query = "SELECT entity FROM legion_group entity WHERE entity.id = :id"), @NamedQuery(name = "group.delete",
                                                                                                                                                                               query = "DELETE FROM legion_group entity WHERE entity.id = :id")})
public class GroupEntity implements IEntity
{
    @Id
    @Column(name = "id")
    @GeneratedValue
    private int id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    @JoinColumn(name = "groupId",
                referencedColumnName = "id")
    private List<GroupRightEntity> rights;

    private boolean active;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<GroupRightEntity> getRights()
    {
        return rights;
    }

    public void setRights(List<GroupRightEntity> rights)
    {
        this.rights = rights;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
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
}
