package niobe.legion.test.client;

import niobe.legion.client.module.IClientModule;
import niobe.legion.client.module.IModuleTab;
import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.data.IRight;

public class TestModule implements IClientModule
{
	@Override
	public IModuleTab[] getMainTabs()
	{
		return new IModuleTab[]{
				new TestTab()};
	}

	@Override
	public boolean startModule()
	{
		System.out.println("Starting Niobe Legion Client Test Module");
		return true;
	}

	@Override
	public boolean stopModule()
	{
		System.out.println("Stopping Niobe Legion Client Test Module");
		return true;
	}

	@Override
	public ICommunicator newCommunicator(ICommunicator parent)
	{
		return new TestCommunicator(parent);
	}

	@Override
	public String getNamespace()
	{
		return TestCommunicator.NAMESPACE;
	}

	@Override
	public String getNamespaceURI()
	{
		return TestCommunicator.NAMESPACE_URI;
	}

	@Override
	public IRight[] getRights()
	{
		return new IRight[]{
				new TestRight("test", null)};
	}
}
