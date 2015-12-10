package niobe.legion.shared.model;

import javax.persistence.Access;
import javax.persistence.AccessType;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
@Access(value = AccessType.PROPERTY)
public interface IEntity
{
	int getId();

	void setId(int id);
}
