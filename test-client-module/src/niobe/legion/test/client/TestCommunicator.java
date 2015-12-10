package niobe.legion.test.client;

import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.data.XmlStanza;

import java.io.IOException;

public class TestCommunicator implements ICommunicator
{
	public static final String NAMESPACE     = "niobe.module.test";
	public static final String NAMESPACE_URI = "ashnurazg.de/niobe/legion-module-test";

	private final ICommunicator parent;

	public TestCommunicator(ICommunicator parent)
	{
		this.parent = parent;
	}

	@Override
	public void consumeStartElement(XmlStanza currentStanza) throws IOException
	{

	}

	@Override
	public void consumeCharacters(XmlStanza currentStanza) throws IOException
	{

	}

	@Override
	public void consumeEndElement(XmlStanza currentStanza) throws IOException
	{

	}

	@Override
	public void write(XmlStanza stanza) throws IOException
	{
		this.parent.write(stanza);
	}

	@Override
	public String getNamespace()
	{
		return NAMESPACE;
	}

	@Override
	public String getNamespaceURI()
	{
		return NAMESPACE_URI;
	}
}
