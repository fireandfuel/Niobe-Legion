package niobe.legion.client.gui;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FxDatasetWrapper<T>
{
	private T dataset;
	private HashMap<String, Property<?>>      properties       = new HashMap<String, Property<?>>();
	private HashMap<String, FxDatasetWrapper> nestedProperties = new HashMap<String, FxDatasetWrapper>();

	private String toStringProperty = null;

	public FxDatasetWrapper(T dataset, String toStringProperty)
	{
		this(dataset);
		this.toStringProperty = toStringProperty;
	}

	public FxDatasetWrapper(T dataset)
	{
		this.dataset = dataset;

		if (this.dataset.getClass().getDeclaredFields() != null)
		{
			Field[] fields = this.dataset.getClass().getDeclaredFields();
			if (fields != null)
			{
				List<Field> fieldList = Stream.of(fields).filter(field -> field != null).collect(Collectors.toList());

				fieldList.stream().filter(field -> !Modifier.isTransient(field.getModifiers()) &&
												   !(field.getType().isPrimitive() &&
													 Modifier.isStatic(field.getModifiers()) &&
													 Modifier.isFinal(field.getModifiers())) &&
												   !Modifier.isNative(field.getModifiers())).forEach(field -> {
					Object value = null;
					try
					{
						String key = field.getName();
						field.setAccessible(true);
						value = field.get(this.dataset);

						if (value != null)
						{
							if (value instanceof Integer)
							{
								this.properties.put(key, new SimpleIntegerProperty((Integer) value));
							} else if (value instanceof Long)
							{
								this.properties.put(key, new SimpleLongProperty((Long) value));
							} else if (value instanceof Float)
							{
								this.properties.put(key, new SimpleFloatProperty((Float) value));
							} else if (value instanceof Double)
							{
								this.properties.put(key, new SimpleDoubleProperty((Double) value));
							} else if (value instanceof Boolean)
							{
								this.properties.put(key, new SimpleBooleanProperty((Boolean) value));
							} else if (value instanceof String)
							{
								this.properties.put(key, new SimpleStringProperty((String) value));
							} else if (value instanceof LocalDate || value instanceof LocalDateTime ||
									   value instanceof LocalTime)
							{
								this.properties.put(key, new SimpleObjectProperty<>(value));
							} else if (!(value instanceof Map || value instanceof Collection))
							{
								this.nestedProperties.put(key, new FxDatasetWrapper<>(value));
							}
						}
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
				});
			}
		}
	}

	public static <T> List<FxDatasetWrapper<T>> asList(List<T> datasets)
	{
		return FxDatasetWrapper.asList(datasets, null);
	}

	public static <T> List<FxDatasetWrapper<T>> asList(List<T> datasets, String toStringProperty)
	{
		if (datasets != null)
		{
			List<FxDatasetWrapper<T>> wrappers =
					datasets.stream().map(dataset -> new FxDatasetWrapper<T>(dataset, toStringProperty))
							.collect(Collectors.toList());
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
		if (key.contains("::"))
		{
			String[] nestedKeys = key.split("::", 2);

			if (this.nestedProperties.containsKey(nestedKeys[0]))
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

	public void set(String key, Object value) throws NoSuchFieldException, IllegalAccessException
	{
		Class<?> datasetClass = this.dataset.getClass();
		Field keyField = datasetClass.getField(key);
		keyField.setAccessible(true);
		keyField.set(this.dataset, value);
	}

	public T getData()
	{
		return this.dataset;
	}

	@Override
	public String toString()
	{
		if (this.toStringProperty != null)
		{
			return this.getProperty(this.toStringProperty).getValue().toString();
		}
		return this.dataset.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FxDatasetWrapper)
		{
			FxDatasetWrapper wrapper = (FxDatasetWrapper) obj;

			for (String key : this.getKeys())
			{
				if (!wrapper.getKeys().contains(key))
				{
					return false;
				}

				if (!this.getProperty(key).getValue().equals(wrapper.getProperty(key).getValue()))
				{
					return false;
				}
			}

			for (String key : this.getNestedKeys())
			{
				if (!wrapper.getNestedKeys().contains(key))
				{
					return false;
				}

				if (!this.getNested(key).equals(wrapper.getNested(key)))
				{
					return false;
				}
			}

			return true;
		}
		return super.equals(obj);
	}
}
