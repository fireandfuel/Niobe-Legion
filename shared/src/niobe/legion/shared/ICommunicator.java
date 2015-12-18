package niobe.legion.shared;

import java.io.IOException;
import java.net.InetAddress;
import niobe.legion.shared.data.XmlStanza;

public interface ICommunicator
{
    void consumeStartElement(XmlStanza currentStanza) throws IOException;

    void consumeCharacters(XmlStanza currentStanza) throws IOException;

    void consumeEndElement(XmlStanza currentStanza) throws IOException;

    void write(XmlStanza stanza) throws IOException;

    String getNamespace();

    String getNamespaceURI();

    default InetAddress getAddress()
    {
        return null;
    }

    default int getPort()
    {
        return -1;
    }
}
