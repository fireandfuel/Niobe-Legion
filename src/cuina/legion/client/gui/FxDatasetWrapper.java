package cuina.legion.client.gui;

import cuina.legion.shared.data.Dataset;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FxDatasetWrapper
{
	private Dataset dataset;
	private HashMap<String, Property<?>>      properties       = new HashMap<String, Property<?>>();
	private HashMap<String, FxDatasetWrapper> nestedProperties = new HashMap<String, FxDatasetWrapper>();

	private String toStringProperty = null;

	public FxDatasetWrapper(Dataset dataset, String toStringProperty)
	{
		this(dataset);
		this.toStringProperty = toStringProperty;
	}

	public FxDatasetWrapper(Dataset dataset)
	{
		this.dataset = dataset;

		for(String key : this.dataset.getKeys())
		{
			Object value = this.dataset.get(key);

			if(value instanceof Integer)
			{
				this.properties.put(key, new SimpleIntegerProperty((Integer) value));
			} else if(value instanceof Long)
			{
				this.properties.put(key, new SimpleLongProperty((Long) value));
			} else if(value instanceof Float)
			{
				this.properties.put(key, new SimpleFloatProperty((Float) value));
			} else if(value instanceof Double)
			{
				this.properties.put(key, new SimpleDoubleProperty((Double) value));
			} else if(value instanceof Boolean)
			{
				this.properties.put(key, new SimpleBooleanProperty((Boolean) value));
			} else if(value instanceof String)
			{
				this.properties.put(key, new SimpleStringProperty((String) value));
			} else if(value instanceof Dataset)
			{
				this.nestedProperties.put(key, new FxDatasetWrapper((Dataset) value));
			}
		}
	}

	public static List<FxDatasetWrapper> asList(List<Dataset> datasets)
	{
		return FxDatasetWrapper.asList(datasets, null);
	}

	public static List<FxDatasetWrapper> asList(List<Dataset> datasets, String toStringProperty)
	{
		if(datasets != null)
		{
			List<FxDatasetWrapper> wrappers = new ArrayList<FxDatasetWrapper>();
			for(Dataset dataset : datasets)
			{
				wrappers.add(new FxDatasetWrapper(dataset, toStringProperty));
			}
			return wrappers;
		}

		return null;
	}

	public Set<String> getKeys()
	{
		return this.properties.keySet();
	}

	public Set<String> getNestedKeys()
	{
		return this.nestedProperties.keySet();
	}

	public Property<?> getProperty(String key)
	{
		if(key.contains("::"))
		{
			String[] nestedKeys = key.split("::", 2);

			if(this.nestedProperties.containsKey(nestedKeys[0]))
			{
				return this.nestedProperties.get(nestedKeys[0]).getProperty(nestedKeys[1]);
			} else
			{
				return new SimpleStringProperty("");
			}
		}

		return this.properties.get(key);
	}

	public FxDatasetWrapper getNested(String key)
	{
		return this.nestedProperties.get(key);
	}

	public void set(String key, Object value)
	{
		this.dataset.set(key, value);
	}

	public Dataset getData()
	{
		return this.dataset;
	}

	@Override
	public String toString()
	{
		if(this.toStringProperty != null)
		{
			return this.getProperty(this.toStringProperty).getValue().toString();
		}
		return this.dataset.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof FxDatasetWrapper)
		{
			FxDatasetWrapper wrapper = (FxDatasetWrapper) obj;

			for(String key : this.getKeys())
			{
				if(!wrapper.getKeys().contains(key))
				{
					return false;
				}

				if(!this.getProperty(key).getValue().equals(wrapper.getProperty(key).getValue()))
				{
					return false;
				}
			}

			for(String key : this.getNestedKeys())
			{
				if(!wrapper.getNestedKeys().contains(key))
				{
					return false;
				}

				if(!this.getNested(key).equals(wrapper.getNested(key)))
				{
					return false;
				}
			}

			return true;
		}
		return super.equals(obj);
	}
}
