package niobe.legion.shared.model;

import javax.persistence.Access;
import javax.persistence.AccessType;

@Access(value = AccessType.PROPERTY)
public interface IEntity
{
	public int getId();

	public void setId(int id);
}
