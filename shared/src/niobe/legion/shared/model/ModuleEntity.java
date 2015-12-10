package niobe.legion.shared.model;

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
import java.util.List;

@Entity(name = "legion_module")
@NamedQueries({
					  @NamedQuery(name = "module.get",
								  query = "SELECT entity FROM legion_module entity"),
					  @NamedQuery(name = "module.getByName",
								  query = "SELECT entity FROM legion_module entity WHERE entity.name = :name AND " +
										  "entity.version = :version"),
			  })
public class ModuleEntity implements IEntity
{
	@Id
	@Column(name = "id")
	@GeneratedValue
	private int id;

	private String  name;
	private String  version;
	private String  fileName;

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
