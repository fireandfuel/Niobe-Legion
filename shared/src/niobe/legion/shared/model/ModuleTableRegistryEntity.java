package niobe.legion.shared.model;

import javax.persistence.*;

@Entity(name = "legion_module_table_registry")
@NamedQueries({
					  @NamedQuery(name = "moduleTableRegistry.get",
								  query = "SELECT c FROM legion_module_table_registry c")})
public class ModuleTableRegistryEntity extends AbstractEntity
{
	private String name;
	private String version;
	private String createStatement;

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

	public String getCreateStatement()
	{
		return createStatement;
	}

	public void setCreateStatement(String createStatement)
	{
		this.createStatement = createStatement;
	}
}
