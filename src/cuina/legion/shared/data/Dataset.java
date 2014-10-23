package cuina.legion.shared.data;

import javax.xml.stream.XMLStreamConstants;
import java.util.*;

public class Dataset
{
	private IDatasetType classType;

	private LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

	public Dataset(IDatasetType type)
	{
		this.classType = type;
	}

	public Dataset(List<XmlStanza> stanzas)
	{
		this.fromXML(stanzas);
	}

	public Set<String> getKeys()
	{
		return this.map.keySet();
	}

	public String getString(String column)
	{
		Object object = this.map.get(column);

		return (object instanceof String) ? (String) object : (object != null) ? object.toString()
				: null;
	}

	public Date getDate(String column)
	{
		Object object = this.map.get(column);

		return (object instanceof Date) ? (Date) object : (object instanceof Long) ? new Date(
				(Long) object) : (object instanceof java.sql.Date) ? new Date(
				((java.sql.Date) object).getTime())
				: (object instanceof java.sql.Timestamp) ? new Date(
				((java.sql.Timestamp) object).getTime()) : null;
	}

	public Integer getInteger(String column)
	{
		Object object = this.map.get(column);

		return (object instanceof Long || object instanceof Integer) ? (Integer) object : null;
	}

	public Long getLong(String column)
	{
		Object object = this.map.get(column);

		return (object instanceof Long || object instanceof Integer) ? (Long) object : null;
	}

	public Boolean getBoolean(String column)
	{
		Object object = this.map.get(column);

		return (object instanceof Boolean) ? (Boolean) object
				: (object instanceof Integer) ? ((Integer) object).intValue() == 1
				: (object instanceof String) ? "true".equals(object) || "1".equals(object)
				: null;
	}

	public Double getDouble(String column)
	{
		Object object = this.map.get(column);

		return (object instanceof Double || object instanceof Float) ? (Double) object : null;
	}

	public Float getFloat(String column)
	{
		Object object = this.map.get(column);

		return (object instanceof Double || object instanceof Float) ? (Float) object : null;
	}

	public Object get(String column)
	{
		return this.map.get(column);
	}

	public <T> T get(Class<T> clazz, String column)
	{
		Object object = this.map.get(column);

		return object.getClass().isInstance(clazz) ? (T) object : null;
	}

	public void set(String key, Object object)
	{
		this.map.put(key, object);
	}

	public List<XmlStanza> toXML(long id)
	{
		return this.toXML(Long.toString(id));
	}

	public List<XmlStanza> toXML(String sequenceId)
	{
		List<XmlStanza> list = new ArrayList<XmlStanza>();

		XmlStanza stanza = new XmlStanza();
		stanza.setName(this.classType.getXmlStanzaName());
		stanza.setSequenceId(sequenceId);
		stanza.setEventType(XMLStreamConstants.START_ELEMENT);
		list.add(stanza);

		for(String key : this.map.keySet())
		{
			stanza = new XmlStanza();
			stanza.setName("legion:column");
			stanza.setSequenceId(sequenceId);
			stanza.setEventType(XMLStreamConstants.CHARACTERS);
			stanza.getAttributes().put("name", key);

			Object value = this.map.get(key);
			stanza.getAttributes().put("type",
					value != null ? value.getClass().getSimpleName() : "legion:void");

			if(value != null)
			{
				if(value instanceof String)
				{
					stanza.setValue((String) value);
				} else if(value instanceof java.util.Date)
				{
					stanza.setValue(Long.toString(((java.util.Date) value).getTime()));
				} else if(value instanceof java.sql.Date)
				{
					stanza.setValue(Long.toString(((java.sql.Date) value).getTime()));
				} else if(value instanceof java.sql.Timestamp)
				{
					stanza.setValue(Long.toString(((java.sql.Timestamp) value).getTime()));
				} else if(value instanceof Integer)
				{
					stanza.setValue(Integer.toString((Integer) value));
				} else if(value instanceof Long)
				{
					stanza.setValue(Long.toString((Long) value));
				} else if(value instanceof Boolean)
				{
					stanza.setValue(Boolean.toString((Boolean) value));
				} else if(value instanceof Float)
				{
					stanza.setValue(Float.toString((Float) value));
				} else if(value instanceof Double)
				{
					stanza.setValue(Double.toString((Double) value));
				} else if(value instanceof Dataset)
				{
					stanza.setEventType(XMLStreamConstants.START_ELEMENT);
					list.add(stanza);
					list.addAll(((Dataset) value).toXML(sequenceId));
					stanza = new XmlStanza();
					stanza.setName("legion:column");
					stanza.setSequenceId(sequenceId);
					stanza.setEventType(XMLStreamConstants.END_ELEMENT);
				}
			} else
			{
				stanza.setValue("legion:null");
			}
			list.add(stanza);
		}

		stanza = new XmlStanza();
		stanza.setName(this.classType.getXmlStanzaName());
		stanza.setEventType(XMLStreamConstants.END_ELEMENT);
		list.add(stanza);

		return list;
	}

	public void fromXML(List<XmlStanza> stanzas)
	{
		for(XmlStanza stanza : stanzas)
		{
			this.fromXML(stanza);
		}
	}

	public void fromXML(XmlStanza stanza)
	{
		if("legion:column".equals(stanza.getName()) && stanza.getAttributes() != null
				&& stanza.getAttributes().containsKey("name")
				&& stanza.getAttributes().containsKey("type"))
		{
			Object object = null;

			String type = stanza.getAttributes().get("type");
			String column = stanza.getAttributes().get("name");

			if(String.class.getSimpleName().equals(type))
			{
				object = stanza.getValue();
			} else if(java.util.Date.class.getSimpleName().equals(type)
					|| java.sql.Timestamp.class.getSimpleName().equals(type))
			{
				object = new java.util.Date(Long.parseLong(stanza.getValue()));
			} else if(Integer.class.getSimpleName().equals(type))
			{
				object = Integer.parseInt(stanza.getValue());
			} else if(Long.class.getSimpleName().equals(type))
			{
				object = Long.parseLong(stanza.getValue());
			} else if(Boolean.class.getSimpleName().equals(type))
			{
				object = Boolean.parseBoolean(stanza.getValue());
			} else if(Float.class.getSimpleName().equals(type))
			{
				object = Float.parseFloat(stanza.getValue());
			} else if(Double.class.getSimpleName().equals(type))
			{
				object = Double.parseDouble(stanza.getValue());
			} else if(Dataset.class.getSimpleName().equals(type))
			{
				XmlStanza embedded = new XmlStanza();
				embedded.setValue(stanza.getValue());
				object = embedded;
			} else if("legion:void".equals(type) && "legion:null".equals(stanza.getValue()))
			{
				this.set(column, null);
			}

			if(type != null && !type.isEmpty() && object != null)
			{
				this.set(column, object);
			}
		}
	}

	public Map<String, Object> getDatabaseValues()
	{
		return new HashMap<String, Object>(this.map);
	}

	/**
	 * @return the classType
	 */
	public IDatasetType getClassType()
	{
		return this.classType;
	}

	/**
	 * @param classType the classType to set
	 */
	public void setClassType(IDatasetType classType)
	{
		this.classType = classType;
	}

	public int getDatasetContentSize()
	{
		return (this.map != null) ? this.map.size() : 0;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer("Dataset (");
		sb.append((this.classType != null) ? this.classType.getXmlStanzaName() : "undefined type");
		sb.append("):\n");

		for(String key : this.map.keySet())
		{
			Object value = this.map.get(key);
			String className = value.getClass().getName();

			sb.append(key);
			sb.append(" (");
			sb.append(className);
			sb.append(") : \t'");
			sb.append(value.toString());
			sb.append("'\n");
		}

		return sb.toString();
	}
}
