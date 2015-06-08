package niobe.legion.shared.model;

import javax.persistence.*;
import java.util.List;

@Entity(name = "legion_group_right")
@NamedQueries({
					  @NamedQuery(name = "groupRight.get",
								  query = "SELECT c FROM legion_group_right c where ")})
public class GroupRightEntity extends AbstractEntity
{
	private String name;
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
}
