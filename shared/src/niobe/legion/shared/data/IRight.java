package niobe.legion.shared.data;

import java.util.List;

public interface IRight
{
	public String getName();

	public List<IRight> getChildren();

	public IRight getParent();

	public void addChildren(IRight right);

	public default boolean isRoot()
	{
		return this.getParent() == null;
	}

	public default boolean isLeaf()
	{
		return this.getChildren() == null || this.getChildren().isEmpty();
	}
}