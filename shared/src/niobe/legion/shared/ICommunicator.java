package niobe.legion.shared;

import niobe.legion.shared.data.XmlStanza;

import java.io.IOException;

public interface ICommunicator
{
	public void consumeStartElement(XmlStanza currentStanza) throws IOException;

	public void consumeCharacters(XmlStanza currentStanza) throws IOException;

	public void consumeEndElement(XmlStanza currentStanza) throws IOException;

	public String getNamespace();

	public String getNamespaceURI();
}
