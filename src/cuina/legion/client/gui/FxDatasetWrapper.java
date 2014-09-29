package cuina.legion.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import cuina.legion.shared.data.Dataset;

public class FxDatasetWrapper
{
	private Dataset dataset;
	private HashMap<String, Property<?>> properties = new HashMap<String, Property<?>>();

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

	public Property<?> getProperty(String key)
	{
		return this.properties.get(key);
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
}
