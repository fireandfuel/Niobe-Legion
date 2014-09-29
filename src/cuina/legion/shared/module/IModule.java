package cuina.legion.shared.module;

import cuina.legion.shared.ICommunicator;

public interface IModule
{
	boolean startModule();

	public void stopModule();

	public ICommunicator getCommunicator();
}
