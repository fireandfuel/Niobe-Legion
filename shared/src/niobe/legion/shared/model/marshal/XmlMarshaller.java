package niobe.legion.shared.model.marshal;

import niobe.legion.shared.data.XmlStanza;

import javax.xml.stream.XMLStreamConstants;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author fireandfuel, 07.04.15
 */
public class XmlMarshaller implements XMLStreamConstants
{
	private final static List<Class> WORKING_COLLECTIONS = new ArrayList<Class>();

	static
	{
		WORKING_COLLECTIONS.addAll(Arrays.asList(java.util.ArrayDeque.class,
												 java.util.ArrayList.class,
												 java.util.LinkedList.class,
												 java.util.PriorityQueue.class,
												 java.util.Stack.class,
												 java.util.Vector.class,
												 java.util.HashSet.class,
												 java.util.TreeSet.class,
												 java.util.concurrent.ArrayBlockingQueue.class,
												 java.util.concurrent.ConcurrentLinkedDeque.class,
												 java.util.concurrent.LinkedBlockingDeque.class,
												 java.util.concurrent.LinkedBlockingQueue.class,
												 java.util.concurrent.LinkedTransferQueue.class,
												 java.util.concurrent.PriorityBlockingQueue.class));
	}

	/**
	 * Marshall an object to a list of xml stanzas
	 *
	 * @param object
	 * @return
	 */
	public static List<XmlStanza> marshall(Object object, long sequenceId)
	{
		if (object == null)
		{
			return null;
		}

		// get object's class name
		String className = object.getClass().getCanonicalName();

		List<XmlStanza> results = new ArrayList<XmlStanza>();

		XmlStanza result = new XmlStanza();
		result.setName("legion:dataset");
		result.setEventType(START_ELEMENT);
		result.getAttributes().put("class", className);
		result.setSequenceId(sequenceId);
		results.add(result);

		if (object.getClass().isArray()) // Check if object is an array
		{
			// array needs the count of elements to initialize while unmarshalling
			result.getAttributes().put("arrayCount", Integer.toString(((Object[]) object).length));
			// marshal the children of array
			results.addAll(getStreamEntry(Stream.of((Object[]) object), sequenceId));

			result = new XmlStanza();
			result.setName("legion:dataset");
			result.setEventType(END_ELEMENT);
			results.add(result);
		} else if (object instanceof Collection) // Check if object is a collection
		{
			try
			{
				if (checkCollectionImplementation(object.getClass()))
				{
					// marshal the children of list
					results.addAll(getStreamEntry(((Collection) object).stream(), sequenceId));

					result = new XmlStanza();
					result.setName("legion:dataset");
					result.setEventType(END_ELEMENT);
					results.add(result);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		} else if (object instanceof Map) // Check if object is a map
		{
			// marshal the children of map
			results.addAll(getMapEntry((Map) object, sequenceId));

			result = new XmlStanza();
			result.setName("legion:dataset");
			result.setEventType(END_ELEMENT);
			results.add(result);
		} else if (object.getClass().isPrimitive() || object instanceof Integer || object instanceof Long ||
				   object instanceof Double || object instanceof Float || object instanceof Boolean ||
				   object instanceof LocalDate || object instanceof LocalTime || object instanceof LocalDateTime ||
				   object instanceof String || object instanceof Byte || object instanceof Short)
		{
			// object is a primitive type like int, OR date, time or string
			result.setEmptyElement(true);
			result.setValue(object.toString());
		} else // object is not an array, list map or primitive
		{
			// get all declared fields (includes private fields, too) of the object
			Field[] fields = object.getClass().getDeclaredFields();

			// for every non-transient, non-constant (primitive + static + final) and non-native field: marshal it
			// and add it to result list
			Stream.of(fields).filter(field -> !Modifier.isTransient(field.getModifiers()) &&
											  !(field.getType().isPrimitive() &&
												Modifier.isStatic(field.getModifiers()) &&
												Modifier.isFinal(field.getModifiers())) &&
											  !Modifier.isNative(field.getModifiers())).map(field -> {
				try
				{
					return getField(field, object, sequenceId);
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				return null;
			}).filter(list -> list != null && !list.isEmpty()).forEach(list -> results.addAll(list));

			result = new XmlStanza();
			result.setName("legion:dataset");
			result.setEventType(END_ELEMENT);
			results.add(result);
		}
		return results;
	}

	/**
	 * Unmarshall a list of xml stanzas to list of objects
	 *
	 * @param xml
	 * @return
	 */
	public static List<Object> unmarshall(List<XmlStanza> xml)
	{
		List<Object> results = new ArrayList<Object>();

		int index = 0;
		while (index < xml.size())
		{
			if (index + 1 < xml.size())
			{
				int closeIndex = searchCloseEntryIndexInStack(index + 1, xml);
				Object result = unmarshallDataset(xml.subList(index, closeIndex));
				if (result != null)
				{
					results.add(result);
				}
				index = closeIndex + 1;
			}
		}

		return results;
	}

	/**
	 * Unmarshall a list of xml stanzas to object
	 *
	 * @param xml
	 * @return
	 */
	private static Object unmarshallDataset(List<XmlStanza> xml)
	{
		if (xml == null)
		{
			return null;
		}

		Object object = null;
		String columnName = null;
		Class<?> columnClass = null;
		boolean isArrayColumn = false;
		int arrayIndex = 0;

		int index = 0;
		while (index < xml.size())
		{
			XmlStanza stanza = xml.get(index);

			switch (stanza.getName())
			{
				case "legion:dataset":
					if (stanza.getEventType() == START_ELEMENT)
					{
						try
						{
							// get the object's class name
							String className = stanza.getAttributes().get("class");
							// check if it is an array
							if (className.endsWith("[]"))
							{
								className = className.replace("[]", "");

								// instantiate the array
								int arrayCount = (stanza.getAttributes().get("arrayCount") != null) ?
												 Integer.parseInt(stanza.getAttributes().get("arrayCount")) : 0;
								object = Array.newInstance(Class.forName(className), arrayCount);
							} else
							{
								Class<?> objectClass = Class.forName(className);

								if (objectClass.isPrimitive() || objectClass == Integer.class ||
									objectClass == Long.class || objectClass == Double.class ||
									objectClass == Float.class || objectClass == Boolean.class ||
									objectClass == LocalDate.class || objectClass == LocalTime.class ||
									objectClass == LocalDateTime.class || objectClass == String.class ||
									objectClass == Byte.class || objectClass == Short.class)
								{
									String value = stanza.getValue();

									if (objectClass == Integer.class)
									{
										object = Integer.parseInt(value);
									} else if (objectClass == Byte.class)
									{
										object = Byte.parseByte(value);
									} else if (objectClass == Short.class)
									{
										object = Short.parseShort(value);
									} else if (objectClass == Long.class)
									{
										object = Long.parseLong(value);
									} else if (objectClass == Float.class)
									{
										object = Float.parseFloat(value);
									} else if (objectClass == Double.class)
									{
										object = Double.parseDouble(value);
									} else if (objectClass == Boolean.class)
									{
										object = Boolean.parseBoolean(value);
									} else if (objectClass == LocalDate.class)
									{
										object = LocalDate.parse(value);
									} else if (objectClass == LocalTime.class)
									{
										object = LocalTime.parse(value);
									} else if (objectClass == LocalDateTime.class)
									{
										object = LocalDateTime.parse(value);
									} else if (objectClass == String.class)
									{
										object = value;
									}
								} else
								{
									if (Collection.class.isAssignableFrom(objectClass))
									{
										if (checkCollectionImplementation(objectClass))
										{
											// instantiate collection
											object = objectClass.newInstance();
										}
									} else
									{
										// instantiate the object
										object = objectClass.newInstance();
									}
								}
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
							object = null;
						}
					}
					break;
				case "legion:column":
					if (object != null && stanza.getEventType() == START_ELEMENT)
					{
						try
						{
							// get the class name of a field
							String className = stanza.getAttributes().get("class");
							// check if it is an array
							if (className.endsWith("[]"))
							{
								isArrayColumn = true;
								className = className.replace("[]", "");
							}

							// get the field's class by its class name
							columnClass = Class.forName(className);
							// get the field's name
							columnName = stanza.getAttributes().get("name");

							// check if field class is a primitive class, date class, time class, string class,
							// list class, map class or is an array
							if (columnClass != null && (Collection.class.isAssignableFrom(columnClass) ||
														Map.class.isAssignableFrom(columnClass) || isArrayColumn))
							{
								// get the field value as a string
								Object value = stanza.getValue();
								if (value != null)
								{
									if (isArrayColumn)
									{
										// instantiate the array
										int arrayCount = (stanza.getAttributes().get("arrayCount") != null) ?
														 Integer.parseInt(stanza.getAttributes().get("arrayCount")) : 0;
										value = Array.newInstance(columnClass, arrayCount);
									} else if (Collection.class.isAssignableFrom(columnClass))
									{
										if (checkCollectionImplementation(columnClass))
										{
											// instantiate collection
											value = columnClass.newInstance();
										}
									} else if (Map.class.isAssignableFrom(columnClass))
									{
										// instantiate map
										value = columnClass.newInstance();
									}

									// set the object's field to the value
									setField(object.getClass().getDeclaredField(columnName), object, value);
								}
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
							columnClass = null;
							columnName = null;
						}
					} else if (stanza.getEventType() == CHARACTERS)
					{
						try
						{
							// get the class name of a field
							String className = stanza.getAttributes().get("class");
							// check if it is an array
							if (className.endsWith("[]"))
							{
								isArrayColumn = true;
								className = className.replace("[]", "");
							}

							// get the field's class by its class name
							columnClass = Class.forName(className);
							// get the field's name
							columnName = stanza.getAttributes().get("name");

							// check if field class is a primitive class, date class, time class, string class,
							// list class, map class or is an array
							if (columnClass != null && (columnClass.isPrimitive() || columnClass == Integer.class ||
														columnClass == Double.class || columnClass == Float.class ||
														columnClass == Boolean.class ||
														columnClass == LocalDate.class ||
														columnClass == LocalTime.class ||
														columnClass == LocalDateTime.class ||
														columnClass == String.class || columnClass == Byte.class ||
														columnClass == Short.class))
							{
								// get the field value as a string
								Object value = stanza.getValue();
								if (value != null)
								{
									if (columnClass == Integer.class)
									{
										// convert string to int
										value = Integer.parseInt((String) value);
									} else if (columnClass == Byte.class)
									{
										// convert string to byte
										value = Byte.parseByte((String) value);
									} else if (columnClass == Short.class)
									{
										// convert string to short
										value = Short.parseShort((String) value);
									} else if (columnClass == Long.class)
									{
										// convert string to long
										value = Long.parseLong((String) value);
									} else if (columnClass == Float.class)
									{
										// convert string to float
										value = Float.parseFloat((String) value);
									} else if (columnClass == Double.class)
									{
										// convert string to double
										value = Double.parseDouble((String) value);
									} else if (columnClass == Boolean.class)
									{
										// convert string to bool
										value = Boolean.parseBoolean((String) value);
									} else if (columnClass == LocalDate.class)
									{
										// convert string to date
										value = LocalDate.parse((String) value);
									} else if (columnClass == LocalTime.class)
									{
										// convert string to time
										value = LocalTime.parse((String) value);
									} else if (columnClass == LocalDateTime.class)
									{
										// convert string to datetime
										value = LocalDateTime.parse((String) value);
									}

									// set the object's field to the value
									setField(object.getClass().getDeclaredField(columnName), object, value);
								}
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
							columnClass = null;
							columnName = null;
						}
					} else if (stanza.getEventType() == END_ELEMENT)
					{
						isArrayColumn = false;
						arrayIndex = 0;
						columnClass = null;
						columnName = null;
					}
					break;
				case "legion:entry":
					if (object != null)
					{
						if (stanza.getEventType() == START_ELEMENT)
						{
							// if a column is selected
							if (columnClass != null && columnName != null)
							{
								// get the index of next close xml stanza (end stanza of this opening stanza)
								int closeEntryIndex = searchCloseEntryIndexInStack(index + 1, xml);

								try
								{
									// get the child object xml stanzas
									List<XmlStanza> childrenList =
											new ArrayList<XmlStanza>(xml.subList(index + 1, closeEntryIndex));
									// remove add child object xml stanzas from the object xml stanza list
									xml.removeAll(childrenList);

									// unmarshall the child object
									Object result = unmarshallDataset(childrenList);
									if (result != null)
									{
										if (isArrayColumn)
										{
											// get object's field as array
											Object[] array = get(object.getClass(), columnName, object);

											// add child object to the array
											if (array != null && arrayIndex < array.length)
											{
												array[arrayIndex++] = result;
											}
										} else if (Collection.class.isAssignableFrom(columnClass))
										{
											if (checkCollectionImplementation(columnClass))
											{
												// get object's field as list
												Collection collection = get(object.getClass(), columnName, object);

												// add child object to the list
												if (collection != null)
												{
													// ATTENTION: Not all subclasses of Collection support adding elements,
													// e.g. some sets, like HashMap's KeySet
													// please use ArrayList or LinkedList to wrap the sets
													// (put the set as parameter of the List constructor)
													collection.add(result);
												}
											}
										} else if (Map.class.isAssignableFrom(columnClass))
										{
											// get object's field as map
											Map map = get(object.getClass(), columnName, object);

											// instantiate the key object
											Object key = stanza.getAttributes().get("key");
											String keyClassName = stanza.getAttributes().get("keyClass");
											Class<?> keyClass =
													(keyClassName != null) ? Class.forName(keyClassName) : null;

											if (keyClass == Integer.class)
											{
												key = Integer.parseInt((String) key);
											} else if (keyClass == Byte.class)
											{
												key = Byte.parseByte((String) key);
											} else if (keyClass == Short.class)
											{
												key = Short.parseShort((String) key);
											} else if (keyClass == Long.class)
											{
												key = Long.parseLong((String) key);
											} else if (keyClass == Float.class)
											{
												key = Float.parseFloat((String) key);
											} else if (keyClass == Double.class)
											{
												key = Double.parseDouble((String) key);
											} else if (keyClass == Boolean.class)
											{
												key = Boolean.parseBoolean((String) key);
											} else if (keyClass == LocalDate.class)
											{
												key = LocalDate.parse((String) key);
											} else if (keyClass == LocalTime.class)
											{
												key = LocalTime.parse((String) key);
											} else if (keyClass == LocalDateTime.class)
											{
												key = LocalDateTime.parse((String) key);
											}

											// put child object with key to map
											if (map != null && key != null && keyClass != null)
											{
												map.put(key, result);
											}

										} else
										{
											// set the object's field with its child
											setField(object.getClass().getDeclaredField(columnName), object, result);
											columnClass = null;
											columnName = null;
										}
									}
								}
								catch (Exception e)
								{
									e.printStackTrace();
									columnClass = null;
									columnName = null;
								}
							} else
							{
								// get the index of next close xml stanza (end stanza of this opening stanza)
								int closeEntryIndex = searchCloseEntryIndexInStack(index + 1, xml);

								try
								{
									// get the child object xml stanzas
									List<XmlStanza> childrenList =
											new ArrayList<XmlStanza>(xml.subList(index + 1, closeEntryIndex));
									// remove add child object xml stanzas from the object xml stanza list
									xml.removeAll(childrenList);

									// unmarshall the child object
									Object result = unmarshall(childrenList);
									if (result != null)
									{
										if (object.getClass().isArray())
										{
											Object[] array = (Object[]) object;

											// add child object to the array
											if (array != null && arrayIndex < array.length)
											{
												array[arrayIndex++] = result;
											}
										} else if (object instanceof Collection)
										{
											Collection collection = (Collection) object;

											// add child object to the list
											if (collection != null && checkCollectionImplementation(object.getClass()))
											{
												// ATTENTION: Not all subclasses of Collection support adding elements,
												// e.g. some sets, like HashMap's KeySet
												// please use ArrayList or LinkedList to wrap the sets
												// (put the set as parameter of the List constructor)
												collection.add(result);
											}
										} else if (object instanceof Map)
										{
											Map map = (Map) object;

											// instantiate the key object
											Object key = stanza.getAttributes().get("key");
											String keyClassName = stanza.getAttributes().get("keyClass");
											Class<?> keyClass =
													(keyClassName != null) ? Class.forName(keyClassName) : null;

											if (keyClass == Integer.class)
											{
												key = Integer.parseInt((String) key);
											} else if (keyClass == Byte.class)
											{
												key = Byte.parseByte((String) key);
											} else if (keyClass == Short.class)
											{
												key = Short.parseShort((String) key);
											} else if (keyClass == Long.class)
											{
												key = Long.parseLong((String) key);
											} else if (keyClass == Float.class)
											{
												key = Float.parseFloat((String) key);
											} else if (keyClass == Double.class)
											{
												key = Double.parseDouble((String) key);
											} else if (keyClass == Boolean.class)
											{
												key = Boolean.parseBoolean((String) key);
											} else if (keyClass == LocalDate.class)
											{
												key = LocalDate.parse((String) key);
											} else if (keyClass == LocalTime.class)
											{
												key = LocalTime.parse((String) key);
											} else if (keyClass == LocalDateTime.class)
											{
												key = LocalDateTime.parse((String) key);
											}

											// put child object with key to map
											if (map != null && key != null && keyClass != null)
											{
												map.put(key, result);
											}
										}
									}
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
						break;
					}
			}
			index++;
		}
		return object;
	}

	/**
	 * Get the value of a field
	 *
	 * @param columnClass class of object
	 * @param columnName  name of object's field
	 * @param object      the object itself
	 * @param <T>         the value's type
	 * @return value of a field of an object
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */

	private static <T> T get(Class<?> columnClass, String columnName, Object object) throws
																					 NoSuchFieldException,
																					 IllegalAccessException
	{
		Field field = columnClass.getDeclaredField(columnName);

		// set the field accessible (e.g. if it have the modifier private)
		field.setAccessible(true);
		Object result = field.get(object);

		return (T) result;
	}

	/**
	 * Search the closest end stanza index without an opening stanza
	 *
	 * @param index start index where the search should start
	 * @param list  the list on which the search runs
	 * @return index of the closest end stanza without opening stanza
	 */
	private static int searchCloseEntryIndexInStack(int index, List<XmlStanza> list)
	{
		// initialize a stack for the stanzas
		LinkedList<XmlStanza> stack = new LinkedList<XmlStanza>();
		for (; index < list.size(); index++)
		{
			XmlStanza stanza = list.get(index);
			// if start stanza and not empty element
			if (stanza.getEventType() == START_ELEMENT && !stanza.isEmptyElement())
			{
				// push the stanza on the top of the stack
				stack.push(stanza);
			}
			if (stanza.getEventType() == END_ELEMENT)
			{
				// if stack is empty, the end stanza's index is found, return the index
				if (stack.isEmpty())
				{
					return index;
				}

				// get the stanza on the top of the stack
				XmlStanza peek = stack.peek();
				// if top of the stack is a start stanza and name of stanza equals stanza on the top of the stack
				if (peek.getEventType() == START_ELEMENT && peek.getName().equals(stanza.getName()))
				// pop (remove first) from the top of the stanza stack
				{
					stack.pop();
				}
			}
		}

		// return the size of the list otherwise
		return list.size();
	}

	/**
	 * Get a field of an object as a list of xml stanzas
	 *
	 * @param field  the field of the object
	 * @param object the object
	 * @return
	 * @throws IllegalAccessException
	 */
	private static List<XmlStanza> getField(Field field, Object object, long sequenceId) throws IllegalAccessException
	{
		if (field == null || object == null)
		{
			return null;
		}

		// set the field accessible (e.g. if it have the modifier private)
		field.setAccessible(true);

		// get field name and value
		String name = field.getName();
		Object value = field.get(object);

		List<XmlStanza> results = new ArrayList<XmlStanza>();
		if (value != null)
		{
			// field will be ignored if its value is null
			XmlStanza result = new XmlStanza();
			result.setName("legion:column");
			result.setEventType(START_ELEMENT);
			result.getAttributes().put("name", name);
			result.getAttributes().put("class", value.getClass().getCanonicalName());
			result.setSequenceId(sequenceId);
			results.add(result);

			if (value.getClass().isArray()) // check if value is an array
			{
				// set array lenght for unmarshalling
				result.getAttributes().put("arrayCount", Integer.toString(((Object[]) value).length));
				// marshal items of the array
				results.addAll(getStreamEntry(Stream.of((Object[]) value), sequenceId));

				result = new XmlStanza();
				result.setName("legion:column");
				result.setEventType(END_ELEMENT);
				results.add(result);
			} else if (value instanceof Collection) // value is a list
			{
				// marshal items of the list
				results.addAll(getStreamEntry(((Collection) value).stream(), sequenceId));

				result = new XmlStanza();
				result.setName("legion:column");
				result.setEventType(END_ELEMENT);
				results.add(result);
			} else if (value instanceof Map) // value is a map
			{
				// marshal keys and items of the map
				results.addAll(getMapEntry((Map) value, sequenceId));

				result = new XmlStanza();
				result.setName("legion:column");
				result.setEventType(END_ELEMENT);
				results.add(result);
			} else if (value.getClass().isPrimitive() || value instanceof Integer || value instanceof Long ||
					   value instanceof Double || value instanceof Byte || value instanceof Short ||
					   value instanceof Float || value instanceof Boolean || value instanceof LocalDate ||
					   value instanceof LocalTime || value instanceof LocalDateTime || value instanceof String)
			{
				// convert primitive types, date, time and string to string
				result.setEventType(CHARACTERS);
				result.setValue(value.toString());

			} else
			{
				// otherwise marshal the child object
				result = new XmlStanza();
				result.setName("legion:entry");
				result.setEventType(START_ELEMENT);
				result.setSequenceId(sequenceId);
				results.add(result);

				results.addAll(marshall(value, sequenceId));

				result = new XmlStanza();
				result.setName("legion:entry");
				result.setEventType(END_ELEMENT);
				results.add(result);

				result = new XmlStanza();
				result.setName("legion:column");
				result.setEventType(END_ELEMENT);
				results.add(result);
			}
		}

		return results;
	}

	/**
	 * Marshalls a stream from an array or list to a list of xml stanza
	 *
	 * @param stream
	 * @return
	 */
	private static List<XmlStanza> getStreamEntry(Stream stream, long sequenceId)
	{

		List<XmlStanza> results = new ArrayList<XmlStanza>();

		// for every element of the stream
		stream.forEach(value -> {
			XmlStanza result = new XmlStanza();
			result.setName("legion:entry");
			result.setEventType(START_ELEMENT);
			result.setSequenceId(sequenceId);
			results.add(result);

			// marshal the value
			List<XmlStanza> stanzas = XmlMarshaller.marshall(value, sequenceId);

			if (stanzas != null)
			{
				results.addAll(stanzas);
			}

			result = new XmlStanza();
			result.setName("legion:entry");
			result.setEventType(END_ELEMENT);
			results.add(result);
		});

		return results;
	}

	/**
	 * Marshalls a map to a list of xml stanza
	 *
	 * @param map
	 * @return
	 */
	private static List<XmlStanza> getMapEntry(Map map, long sequenceId)
	{
		List<XmlStanza> results = new ArrayList<XmlStanza>();

		// for every key value pair in the map
		map.forEach((key, value) -> {
			XmlStanza result = new XmlStanza();
			result.setName("legion:entry");
			result.setEventType(START_ELEMENT);
			// set the key class and key
			result.getAttributes().put("keyClass", key.getClass().getCanonicalName());
			result.getAttributes().put("key", key.toString());
			result.setSequenceId(sequenceId);
			results.add(result);

			// marshal the value
			List<XmlStanza> stanzas = XmlMarshaller.marshall(value, sequenceId);

			if (stanzas != null)
			{
				results.addAll(stanzas);
			}

			result = new XmlStanza();
			result.setName("legion:entry");
			result.setEventType(END_ELEMENT);
			results.add(result);
		});

		return results;
	}

	/**
	 * Sets an object field's value
	 *
	 * @param field
	 * @param object
	 * @param value
	 * @throws IllegalAccessException
	 */
	private static void setField(Field field, Object object, Object value) throws IllegalAccessException
	{
		// set the field accessible (e.g. if it have the modifier private)
		field.setAccessible(true);
		field.set(object, value);
	}

	private static boolean checkCollectionImplementation(Class<?> clazz) throws Exception
	{
		// look up the known working collection classes
		if (!WORKING_COLLECTIONS.contains(clazz))
		{
			// try to instantiate the collection
			Collection collection = (Collection) clazz.newInstance();
			if (collection != null)
			{
				// test data to insert in the collection
				String stringTest = "TestMe";
				Integer integerTest = new Integer(42);

				// test if collection supports adding
				collection.add(stringTest);
				collection.add(integerTest);

				// test if collection's iterator works
				Iterator iterator = collection.iterator();
				if (iterator.hasNext())
				{
					if (iterator.next() == stringTest && iterator.hasNext())
					{
						if (iterator.next() == integerTest)
						{
							WORKING_COLLECTIONS.add(clazz);
						} else
						{
							return false;
						}
					} else
					{
						return false;
					}
				} else
				{
					return false;
				}
			}
		}
		return true;
	}
}
