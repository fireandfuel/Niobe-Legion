package niobe.legion.client;

import java.util.List;

public interface DatasetReceiver<T>
{
	public void set(T dataset);

	public default void setAll(List<T> datasets)
	{
		datasets.forEach(this::set);
	}

	public void clear();

	public void remove(T dataset);

	public void removeAll(List<T> datasets);
}