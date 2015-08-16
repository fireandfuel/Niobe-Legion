package niobe.legion.shared.module;

import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.data.IRight;

public interface IModule
{
	public boolean startModule();

	public void stopModule();

	public ICommunicator getCommunicator();

	public IRight[] getRights();
}
