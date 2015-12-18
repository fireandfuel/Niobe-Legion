/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ModuleEntity.java) is part of Niobe Legion (module niobe-legion-shared).
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

@Entity(name = "legion_module")
@NamedQueries({@NamedQuery(name = "module.get",
                           query = "SELECT entity FROM legion_module entity"), @NamedQuery(name = "module.getByName",
                                                                                           query = "SELECT entity FROM legion_module entity WHERE entity.name = :name AND " + "entity.version = :version"),})
public class ModuleEntity implements IEntity
{
    @Id
    @Column(name = "id")
    @GeneratedValue
    private int id;

    private String name;
    private String version;
    private String fileName;

    private boolean activated;

    @OneToMany(cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    @JoinColumn(name = "moduleId",
                referencedColumnName = "id")
    private List<ModuleDatabaseEntity> moduleTables;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public boolean isActivated()
    {
        return activated;
    }

    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }

    public List<ModuleDatabaseEntity> getModuleTables()
    {
        return moduleTables;
    }

    public void setModuleTables(List<ModuleDatabaseEntity> moduleTables)
    {
        this.moduleTables = moduleTables;
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
