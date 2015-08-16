package niobe.legion.shared;

import niobe.legion.shared.data.XmlStanza;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public interface ICommunicator
{
	public void consumeStartElement(XmlStanza currentStanza) throws IOException;

	public void consumeCharacters(XmlStanza currentStanza) throws IOException;

	public void consumeEndElement(XmlStanza currentStanza) throws IOException;

	public void write(XmlStanza stanza) throws IOException, SocketException;

	public String getNamespace();

	public String getNamespaceURI();

	public default InetAddress getAddress()
	{
		return null;
	}

	public default int getPort()
	{
		return -1;
	}
}
