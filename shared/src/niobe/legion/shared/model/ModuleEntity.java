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
								  query = "SELECT c FROM legion_module c")})
public class ModuleEntity implements IEntity
{
	@Id
	@Column(name = "id")
	@GeneratedValue
	private int id;

	private String  name;
	private String  password;
	private boolean activated;

	@OneToMany(cascade = CascadeType.ALL,
			   fetch = FetchType.EAGER)
	@JoinColumn(name = "moduleId",
				referencedColumnName = "id")
	private List<ModuleTableRegistryEntity> moduleTableRegistryList;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isActivated()
	{
		return activated;
	}

	public void setActivated(boolean activated)
	{
		this.activated = activated;
	}

	public List<ModuleTableRegistryEntity> getModuleTableRegistryList()
	{
		return moduleTableRegistryList;
	}

	public void setModuleTableRegistryList(List<ModuleTableRegistryEntity> moduleTableRegistryList)
	{
		this.moduleTableRegistryList = moduleTableRegistryList;
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
