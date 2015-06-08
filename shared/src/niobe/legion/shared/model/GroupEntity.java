package niobe.legion.shared.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import java.util.List;

@Entity(name = "legion_group")
@NamedQueries({
					  @NamedQuery(name = "group.get",
								  query = "SELECT c FROM legion_group c"), @NamedQuery(name = "group.getWithId",
																					   query = "SELECT c FROM legion_group c WHERE c.id = :id"),
					  @NamedQuery(name = "group.delete",
								  query = "DELETE FROM legion_group c WHERE c.id = :id")})
public class GroupEntity extends AbstractEntity
{
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
}
