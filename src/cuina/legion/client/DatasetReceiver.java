package cuina.legion.client;

import java.util.List;

import cuina.legion.shared.data.Dataset;

public interface DatasetReceiver
{
	public void add(Dataset dataset);

	public void addAll(List<Dataset> datasets);

	public void clear();

	public void remove(Dataset dataset);

	public void removeAll(List<Dataset> datasets);
}