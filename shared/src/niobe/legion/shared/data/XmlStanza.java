package niobe.legion.shared.data;

import javax.xml.stream.XMLStreamConstants;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * this class represents a XML event, like START_ELEMENT, CHARACTERS and END_ELEMENT
 *
 * @author fireandfuel
 */
public class XmlStanza
{
	private int    eventType;
	private String name, localName;
	private String  nameSpaceURI;
	private String  value;
	private boolean emptyElement;
	private HashMap<String, String> attributes = new HashMap<String, String>();

	public XmlStanza()
	{

	}

	public XmlStanza(XmlStanza oldStanza)
	{
		if (oldStanza != null)
		{
			this.setName(oldStanza.getName());
			this.setLocalName(oldStanza.getLocalName());
			this.setNameSpaceURI(oldStanza.getNameSpaceURI());
			this.setValue(oldStanza.getValue());
			this.setEmptyElement(oldStanza.isEmptyElement());
			this.setEventType(oldStanza.getEventType());

			this.attributes.putAll(oldStanza.attributes);
		}
	}

	public int getEventType()
	{
		return this.eventType;
	}

	public void setEventType(int eventType)
	{
		this.eventType = eventType;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isEmptyElement()
	{
		return this.emptyElement;
	}

	/**
	 * @return the nameSpace
	 */
	public String getNameSpaceURI()
	{
		return this.nameSpaceURI;
	}

	/**
	 * @param nameSpaceURI the nameSpace to set
	 */
	public void setNameSpaceURI(String nameSpaceURI)
	{
		this.nameSpaceURI = nameSpaceURI;
	}

	public void setEmptyElement(boolean emptyElement)
	{
		this.emptyElement = emptyElement;
	}

	public String getValue()
	{
		return this.value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return the id
	 */
	public String getSequenceId()
	{
		return this.getAttribute("sequenceId");
	}

	/**
	 * @param id the id to set
	 */
	public void setSequenceId(long id)
	{
		this.putAttribute("sequenceId", Long.toString(id));
	}

	public void setSequenceId(String id)
	{
		if (id.matches("\\d+"))
		{
			this.putAttribute("sequenceId", id);
		}
	}

	public void putAttribute(String key, String value)
	{
		this.attributes.put(key, value);
	}

	public String getAttribute(String key)
	{
		return this.attributes.get(key);
	}

	public String removeAttribute(String key)
	{
		return this.attributes.remove(key);
	}

	public Set<String> getAttributeKeys()
	{
		return this.attributes.keySet();
	}

	public boolean hasNoAttributes()
	{
		return this.attributes.isEmpty();
	}

	public void forEachAttribute(BiConsumer<String, String> consumer)
	{
		this.attributes.forEach(consumer);
	}

	public boolean containsAttributeKey(String key)
	{
		return this.attributes.containsKey(key);
	}

	public String getLocalName()
	{
		return this.localName;
	}

	public void setLocalName(String localName)
	{
		this.localName = localName;
	}

	public String getEventTypeAsString()
	{
		switch (this.getEventType())
		{
			case XMLStreamConstants.START_ELEMENT:
				return "START_ELEMENT";
			case XMLStreamConstants.END_ELEMENT:
				return "END_ELEMENT";
			case XMLStreamConstants.CHARACTERS:
				return "CHARACTERS";
			default:
				return "not set";
		}
	}

	@Override
	public String toString()
	{
		String output = "XmlStanza name: " + this.getName() + ", localname: " + this.getLocalName() + ", eventType: " +
						this.getEventTypeAsString() + ", value: " + this.getValue();
		for (String key : this.attributes.keySet())
		{
			output += "\n, attribute " + key + " : " + this.attributes.get(key);
		}
		return output;
	}
}
