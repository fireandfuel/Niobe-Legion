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
import javax.persistence.OneToOne;

@Entity(name = "legion_user")
@NamedQueries({
					  @NamedQuery(name = "user.get",
								  query = "SELECT entity FROM legion_user entity"), @NamedQuery(name = "user.getByName",
																								query = "SELECT entity FROM legion_user entity WHERE entity.name = :name")})
public class UserEntity implements IEntity
{
	@Id
	@Column(name = "id")
	@GeneratedValue
	private int id;

	private String name;
	private String password;

	@OneToOne(cascade = CascadeType.ALL,
			  fetch = FetchType.EAGER)
	@JoinColumn(name = "groupId",
				referencedColumnName = "id")
	private GroupEntity group;

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

	public GroupEntity getGroup()
	{
		return group;
	}

	public void setGroup(GroupEntity group)
	{
		this.group = group;
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
