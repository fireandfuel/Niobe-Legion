package niobe.legion.client;

import java.util.List;

public interface DatasetReceiver<T>
{
	public void add(T dataset);

	public void addAll(List<T> datasets);

	public void clear();

	public void remove(T dataset);

	public void removeAll(List<T> datasets);
}