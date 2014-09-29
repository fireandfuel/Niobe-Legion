package cuina.legion.shared;

import java.io.IOException;

import cuina.legion.shared.data.XmlStanza;

public interface ICommunicator
{
	public void consumeStartElement(XmlStanza currentStanza) throws IOException;

	public void consumeCharacters(XmlStanza currentStanza) throws IOException;

	public void consumeEndElement(XmlStanza currentStanza) throws IOException;

	public String getNamespace();

	public String getNamespaceURI();
}
