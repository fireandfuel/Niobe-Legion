package niobe.legion.shared.data;

import java.util.List;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
public interface IRight
{
	String getName();

	List<IRight> getChildren();

	IRight getParent();

	void addChildren(IRight right);

	default boolean isRoot()
	{
		return this.getParent() == null;
	}

	default boolean isLeaf()
	{
		return this.getChildren() == null || this.getChildren().isEmpty();
	}
}