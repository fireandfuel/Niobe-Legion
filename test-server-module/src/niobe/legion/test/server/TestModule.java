package niobe.legion.test.server;

import niobe.legion.server.AbstractDatabase;
import niobe.legion.server.module.IServerModule;
import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.data.IRight;

public class TestModule implements IServerModule
{
	@Override
	public String getDatabaseName()
	{
		return "niobe-test-server-module";
	}

	@Override
	public AbstractDatabase initDatabase()
	{
		return null;
	}

	@Override
	public boolean startModule()
	{
		System.out.println("Starting Niobe Legion Server Test Module");
		return true;
	}

	@Override
	public boolean stopModule()
	{
		System.out.println("Stopping Niobe Legion Server Test Module");
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
