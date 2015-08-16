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
import javax.persistence.Transient;
import java.util.List;

@Entity(name = "legion_group_right")
@NamedQueries({
					  @NamedQuery(name = "group_right.get",
								  query = "SELECT c FROM legion_group_right c where c.id = :id")})
public class GroupRightEntity implements IEntity
{
	@Id
	@Column(name = "id")
	@GeneratedValue
	private int id;

	private String  name;
	@Transient
	private String  displayName;
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
		if (name != null)
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
