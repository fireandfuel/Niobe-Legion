package niobe.legion.shared.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public abstract class AbstractEntity
{

	@Id
	@Column(name = "id")
	@GeneratedValue
	private int id;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

}
