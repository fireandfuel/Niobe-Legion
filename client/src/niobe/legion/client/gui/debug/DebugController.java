package niobe.legion.client.gui.debug;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import niobe.legion.client.Client;
import niobe.legion.client.gui.ICloseableDialogController;
import niobe.legion.shared.Communicator;
import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.data.XmlStanza;

import javax.xml.stream.XMLStreamConstants;
import java.io.IOException;
import java.time.LocalDateTime;

public class DebugController implements ICommunicator, ICloseableDialogController, XMLStreamConstants
{

	@FXML
	private TextArea messages;

	private Stage stage;

	@FXML
	private void initialize()
	{
		this.messages.setText("-- Connected " + LocalDateTime.now().toString() + "\n");
	}

	@Override
	public void consumeStartElement(XmlStanza currentStanza) throws IOException
	{
		this.startElement("[IN]", currentStanza);
	}

	private void startElement(String type, XmlStanza currentStanza)
	{
		if (currentStanza != null && currentStanza.getName() != null)
		{

			StringBuilder message = new StringBuilder(type + " <" + currentStanza.getName());

			if (!currentStanza.hasNoAttributes())
			{
				currentStanza.forEachAttribute((key, value) -> message.append(" " + key + "=\"" + value + "\""));
			}

			if (currentStanza.isEmptyElement())
			{
				message.append("/>\n");
			} else
			{
				message.append(">\n");
			}

			if ("legion:proceedtls".equals(currentStanza.getName()))
			{
				message.append("-- Start TLS connection\n");
			}

			Platform.runLater(() -> messages.appendText(message.toString()));
		}
	}

	@Override
	public void consumeCharacters(XmlStanza currentStanza) throws IOException
	{
		this.characters("[IN]", currentStanza);
	}

	private void characters(String type, XmlStanza currentStanza)
	{
		if (currentStanza != null && currentStanza.getValue() != null)
		{
			Platform.runLater(() -> messages.appendText(type + " " + currentStanza.getName() + " CDATA: \"" + currentStanza.getValue() + "\"\n"));
		}
	}

	@Override
	public void consumeEndElement(XmlStanza currentStanza) throws IOException
	{
		this.endElement("[IN]", currentStanza);
	}

	private void endElement(String type, XmlStanza currentStanza)
	{
		if (currentStanza != null && currentStanza.getName() != null)
		{
			Platform.runLater(() -> messages.appendText(type + " </" + currentStanza.getName() + ">\n"));
		}
	}

	@Override
	public void write(XmlStanza stanza) throws IOException
	{
		if (stanza != null && stanza.getName() != null)
		{
			switch (stanza.getEventType())
			{
				case START_ELEMENT:
					this.startElement("[OUT]", stanza);
					break;
				case CHARACTERS:
					this.startElement("[OUT]", stanza);
					this.characters("[OUT]", stanza);
				case END_ELEMENT:
					this.endElement("[OUT]", stanza);
					break;
			}
		}
	}

	@Override
	public String getNamespace()
	{
		return Communicator.DEBUG_NAMESPACE;
	}

	@Override
	public String getNamespaceURI()
	{
		return Communicator.DEBUG_NAMESPACE_URI;
	}

	@Override
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	@Override
	public void close()
	{
		if (this.stage != null)
		{
			this.stage.close();
		}
	}

	public boolean isClosed()
	{
		return this.stage == null || !this.stage.isShowing();
	}

	@FXML
	private void exit()
	{
		Platform.exit();
	}

	@FXML
	public void closeConnection() throws IOException
	{
		this.writeMessage("close Connection by watchdog");
		Client.getCommunicator().close();
	}

	@FXML
	public void ping() throws IOException
	{
		Client.getCommunicator().ping();
	}

	public void writeMessage(String message)
	{
		Platform.runLater(() -> messages.appendText("-- " + message + "\n"));
	}
}
