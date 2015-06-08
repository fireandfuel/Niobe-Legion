package niobe.legion.shared.module;

import niobe.legion.shared.ICommunicator;

public interface IModule
{
	boolean startModule();

	public void stopModule();

	public ICommunicator getCommunicator();

	//	public List<IDatasetType> getDataTypes();
}
