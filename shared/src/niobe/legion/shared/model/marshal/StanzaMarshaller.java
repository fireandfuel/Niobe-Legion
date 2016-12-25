/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (StanzaMarshaller.java) is part of Niobe Legion (module niobe-legion-shared_main).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared.model.marshal;

import niobe.legion.shared.data.Stanza;

import javax.xml.stream.XMLStreamConstants;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author fireandfuel, 07.04.15
 */
public class StanzaMarshaller implements XMLStreamConstants
{

    private final static class StanzaColumn
    {
        final String columnName;
        final Class<?> columnClass;
        final boolean isArrayColumn;

        StanzaColumn(String columnName, Class<?> columnClass, boolean isArrayColumn)
        {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isArrayColumn = isArrayColumn;
        }
    }

    private final static List<Class> ALLOWED_COLLECTION_CLASSES = Arrays.asList(java.util.ArrayDeque.class,
                                                                                java.util.ArrayList.class,
                                                                                java.util.HashSet.class,
                                                                                java.util.LinkedHashSet.class,
                                                                                java.util.LinkedList.class,
                                                                                java.util.PriorityQueue.class,
                                                                                java.util.Stack.class,
                                                                                java.util.TreeSet.class,
                                                                                java.util.Vector.class,
                                                                                java.util.concurrent.ArrayBlockingQueue.class,
                                                                                java.util.concurrent.ConcurrentLinkedDeque.class,
                                                                                java.util.concurrent.LinkedBlockingDeque.class,
                                                                                java.util.concurrent.LinkedBlockingQueue.class,
                                                                                java.util.concurrent.LinkedTransferQueue.class,
                                                                                java.util.concurrent.PriorityBlockingQueue.class,
                                                                                java.util.concurrent.SynchronousQueue.class);

    private final static List<Class> ALLOWED_MAP_CLASSES = Arrays.asList(java.util.HashMap.class,
                                                                         java.util.Hashtable.class,
                                                                         java.util.LinkedHashMap.class,
                                                                         java.util.EnumMap.class,
                                                                         java.util.IdentityHashMap.class,
                                                                         java.util.Properties.class,
                                                                         java.util.TreeMap.class,
                                                                         java.util.WeakHashMap.class,
                                                                         java.util.concurrent.ConcurrentHashMap.class,
                                                                         java.util.concurrent.ConcurrentSkipListMap.class,
                                                                         java.util.jar.Attributes.class);

    /**
     * Marshal an object to a list of xml stanzas
     *
     * @param object
     * @return
     */
    public static List<Stanza> marshal(Object object, long sequenceId)
    {
        if(object == null)
        {
            return null;
        }

        // get object's class name
        String className = object.getClass().getCanonicalName();

        List<Stanza> results = new ArrayList<Stanza>();

        Stanza result = new Stanza();
        result.setName("legion:dataset");
        result.setEventType(START_ELEMENT);
        result.putAttribute("class", className);
        result.setSequenceId(sequenceId);
        results.add(result);

        if(object.getClass().isArray()) // Check if object is an array
        {
            // array needs the count of elements to initialize while unmarshalling
            // use the canonical class name instead of the primitive type name
            switch(className)
            {
                case "boolean[]":
                    result.putAttribute("class", Boolean.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((boolean[]) object).length));
                    results.addAll(getEntry((boolean[]) object, sequenceId));
                    break;
                case "byte[]":
                    result.putAttribute("class", Byte.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((byte[]) object).length));
                    results.addAll(getEntry((byte[]) object, sequenceId));
                    break;
                case "short[]":
                    result.putAttribute("class", Short.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((short[]) object).length));
                    results.addAll(getEntry((short[]) object, sequenceId));
                    break;
                case "int[]":
                    result.putAttribute("class", Integer.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((int[]) object).length));
                    results.addAll(getEntry((int[]) object, sequenceId));
                    break;
                case "float[]":
                    result.putAttribute("class", Float.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((float[]) object).length));
                    results.addAll(getEntry((float[]) object, sequenceId));
                    break;
                case "double[]":
                    result.putAttribute("class", Double.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((double[]) object).length));
                    results.addAll(getEntry((double[]) object, sequenceId));
                    break;
                case "long[]":
                    result.putAttribute("class", Long.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((long[]) object).length));
                    results.addAll(getEntry((long[]) object, sequenceId));
                    break;
                case "char[]":
                    result.putAttribute("class", Character.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((char[]) object).length));
                    results.addAll(getEntry((char[]) object, sequenceId));
                    break;
                case "java.math.BigInteger":
                    result.putAttribute("class", BigInteger.class.getCanonicalName() + "[]");
                    result.putAttribute("arrayCount", Integer.toString(((BigInteger[]) object).length));
                    results.addAll(getEntry((BigInteger[]) object, sequenceId));
                    break;
                default:
                    result.putAttribute("arrayCount", Integer.toString(((Object[]) object).length));
                    results.addAll(getStreamEntry(Stream.of((Object[]) object), sequenceId));
                    break;
            }

            result = new Stanza();
            result.setName("legion:dataset");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        } else if(object instanceof Collection) // Check if object is a collection
        {
            try
            {
                Collection collection = (Collection) object;

                if(!checkCollectionClass(collection.getClass()))
                {
                    collection = convertCollection(collection);
                    result.putAttribute("class", collection.getClass().getCanonicalName());
                }

                // marshal the children of list
                results.addAll(getStreamEntry(collection.stream(), sequenceId));

                result = new Stanza();
                result.setName("legion:dataset");
                result.setEventType(END_ELEMENT);
                result.setSequenceId(sequenceId);
                results.add(result);
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        } else if(object instanceof Map) // Check if object is a map
        {
            Map map = (Map) object;

            if(!checkMapClass(map.getClass()))
            {
                map = convertMap(map);
                result.putAttribute("class", map.getClass().getCanonicalName());
            }

            // marshal the children of map
            results.addAll(getMapEntry(map, sequenceId));

            result = new Stanza();
            result.setName("legion:dataset");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        } else if(object.getClass()
                .isPrimitive() || object instanceof Integer || object instanceof Long || object instanceof Double || object instanceof Float || object instanceof Boolean || object instanceof LocalDate || object instanceof LocalTime || object instanceof LocalDateTime || object instanceof String || object instanceof Byte || object instanceof Short || object instanceof Character || object instanceof BigInteger)
        {
            // object is a primitive type like int, OR date, time or string
            result.setEmptyElement(true);
            result.setValue(object.toString());
        } else if (object.getClass().isEnum()) {
            Enum<?> enumObject = (Enum<?>) object;
            result.setEmptyElement(true);
            result.setValue(enumObject.name());
        } else // object is not an array, collection, map, primitive or enum
        {
            // get all declared fields (includes private fields, too) of the object
            Field[] fields = object.getClass().getDeclaredFields();

            // for every non-transient, non-constant (primitive + static + final) and non-native field: marshal it
            // and add it to result list
            Stream.of(fields).filter(field -> !Modifier.isTransient(field.getModifiers()) && !(field.getType()
                    .isPrimitive() && Modifier.isStatic(field.getModifiers()) && Modifier
                    .isFinal(field.getModifiers())) && !Modifier.isNative(field.getModifiers())).map(field ->
                                                                                                     {
                                                                                                         try
                                                                                                         {
                                                                                                             return getField(
                                                                                                                     field,
                                                                                                                     object,
                                                                                                                     sequenceId);
                                                                                                         } catch(IllegalAccessException e)
                                                                                                         {
                                                                                                             e.printStackTrace();
                                                                                                         }
                                                                                                         return null;
                                                                                                     })
                    .filter(list -> list != null && !list.isEmpty()).forEach(results::addAll);

            result = new Stanza();
            result.setName("legion:dataset");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    /**
     * Unmarshal a list of xml stanzas to list of objects
     *
     * @param xml
     * @return
     */
    public static List<Object> unmarshal(List<Stanza> xml)
    {
        List<Object> results = new ArrayList<Object>();

        if(xml == null)
        {
            return results;
        }
        while(xml.size() > 0)
        {
            int closeIndex = searchCloseEntryIndexInStack(0, xml);
            if(closeIndex == 0)
            {
                return null;
            }
            List<Stanza> subList = new ArrayList<Stanza>(xml.subList(0, closeIndex));
            xml.removeAll(subList);
            Object result = unmarshalStanzas(subList);

            if(result != null)
            {
                results.add(result);
            }
        }

        return results;
    }

    /**
     * Unmarshal a list of xml stanzas to object
     *
     * @param xml
     * @return
     */
    private static Object unmarshalStanzas(List<Stanza> xml)
    {
        if(xml == null)
        {
            return null;
        }

        Object object = null;
        StanzaColumn stanzaColumn = null;

        int index = 0;
        while(index < xml.size())
        {
            Stanza stanza = xml.get(index);

            switch(stanza.getName())
            {
                case "legion:dataset":
                    if(stanza.getEventType() == START_ELEMENT)
                    {
                        object = unmarshalDataset(stanza);
                    }
                    break;
                case "legion:column":
                    stanzaColumn = unmarshalColumn(object, stanza);
                    break;
                case "legion:entry":
                    if(stanza.getEventType() == START_ELEMENT)
                    {
                        // get the index of next close xml stanza (end stanza of this opening stanza)
                        int closeEntryIndex = searchCloseEntryIndexInStack(index, xml);

                        // get the child object xml stanzas
                        List<Stanza> childrenList = new ArrayList<Stanza>(xml.subList(index + 1, closeEntryIndex - 1));
                        // remove add child object xml stanzas from the object xml stanza list
                        xml.removeAll(childrenList);

                        unmarshalEntry(object, stanza, childrenList, stanzaColumn);
                    }
                    break;
            }
            index++;
        }
        return object;
    }

    private static Object unmarshalDataset(Stanza stanza)
    {
        Object object = null;
        try
        {
            // get the object's class name
            String className = stanza.getAttribute("class");
            // check if it is an array
            if(className.endsWith("[]"))
            {
                className = className.replace("[]", "");

                // instantiate the array
                int arrayCount = (stanza.getAttribute("arrayCount") != null) ? Integer
                        .parseInt(stanza.getAttribute("arrayCount")) : 0;

                Class<?> objectClass = Class.forName(className);

                if(objectClass
                        .isPrimitive() || objectClass == Integer.class || objectClass == Long.class || objectClass == Double.class || objectClass == Float.class || objectClass == Boolean.class || objectClass == Byte.class || objectClass == Short.class || objectClass == Character.class || objectClass == BigInteger.class)
                {
                    if(objectClass == Boolean.class)
                    {
                        object = new boolean[arrayCount];
                    } else if(objectClass == Byte.class)
                    {
                        object = new byte[arrayCount];
                    } else if(objectClass == Short.class)
                    {
                        object = new short[arrayCount];
                    } else if(objectClass == Integer.class)
                    {
                        object = new int[arrayCount];
                    } else if(objectClass == Long.class)
                    {
                        object = new long[arrayCount];
                    } else if(objectClass == Float.class)
                    {
                        object = new float[arrayCount];
                    } else if(objectClass == Double.class)
                    {
                        object = new double[arrayCount];
                    } else if(objectClass == Character.class)
                    {
                        object = new char[arrayCount];
                    } else if(objectClass == BigInteger.class)
                    {
                        object = new BigInteger[arrayCount];
                    }
                } else
                {
                    object = Array.newInstance(Class.forName(className), arrayCount);
                }
            } else
            {
                Class<?> objectClass = Class.forName(className);

                if(objectClass
                        .isPrimitive() || objectClass.isEnum() || objectClass == Integer.class || objectClass == Long.class || objectClass == Double.class || objectClass == Float.class || objectClass == Boolean.class || objectClass == LocalDate.class || objectClass == LocalTime.class || objectClass == LocalDateTime.class || objectClass == String.class || objectClass == Byte.class || objectClass == Short.class || objectClass == Character.class || objectClass == BigInteger.class)
                {
                    String value = stanza.getValue();

                    if(objectClass == Integer.class)
                    {
                        object = Integer.parseInt(value);
                    } else if(objectClass == Byte.class)
                    {
                        object = Byte.parseByte(value);
                    } else if(objectClass == Character.class)
                    {
                        object = value.charAt(0);
                    } else if(objectClass == Short.class)
                    {
                        object = Short.parseShort(value);
                    } else if(objectClass == Long.class)
                    {
                        object = Long.parseLong(value);
                    } else if(objectClass == Float.class)
                    {
                        object = Float.parseFloat(value);
                    } else if(objectClass == Double.class)
                    {
                        object = Double.parseDouble(value);
                    } else if(objectClass == Boolean.class)
                    {
                        object = Boolean.parseBoolean(value);
                    } else if(objectClass == LocalDate.class)
                    {
                        object = LocalDate.parse(value);
                    } else if(objectClass == LocalTime.class)
                    {
                        object = LocalTime.parse(value);
                    } else if(objectClass == LocalDateTime.class)
                    {
                        object = LocalDateTime.parse(value);
                    } else if(objectClass == BigInteger.class)
                    {
                        object = new BigInteger(value);
                    } else if (objectClass.isEnum()) {
                        Optional optional = EnumSet.allOf((Class) objectClass).stream()
                                .filter(e -> e.getClass().isEnum() && value.equals(((Enum<?>) e).name())).findFirst();
                        if (optional.isPresent()) {
                            object = optional.get();
                        }
                    } else if (objectClass == String.class)
                    {
                        object = value;
                    }
                } else
                {
                    if(Collection.class.isAssignableFrom(objectClass))
                    {
                        if(checkCollectionClass(objectClass))
                        {
                            // instantiate collection
                            object = objectClass.newInstance();
                        }
                    } else if(Map.class.isAssignableFrom(objectClass))
                    {
                        if(checkMapClass(objectClass))
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
        } catch(Exception e)
        {
            e.printStackTrace();
            object = null;
        }
        return object;
    }

    private static StanzaColumn unmarshalColumn(Object object, Stanza stanza)
    {
        String columnName = null;
        Class<?> columnClass = null;
        boolean isArrayColumn = false;

        if(object != null && stanza.getEventType() == START_ELEMENT)
        {
            try
            {
                // get the class name of a field
                String className = stanza.getAttribute("class");
                // check if it is an array
                if(className.endsWith("[]"))
                {
                    isArrayColumn = true;
                    className = className.replace("[]", "");
                }

                // get the field's class by its class name
                columnClass = Class.forName(className);
                // get the field's name
                columnName = stanza.getAttribute("name");

                // check if field class is a primitive class, date class, time class, string class,
                // list class, map class or is an array
                if(columnClass != null && (Collection.class.isAssignableFrom(columnClass) || Map.class
                        .isAssignableFrom(columnClass) || isArrayColumn))
                {
                    Object value;
                    if(isArrayColumn)
                    {
                        // instantiate the array
                        int arrayCount = (stanza.getAttribute("arrayCount") != null) ? Integer
                                .parseInt(stanza.getAttribute("arrayCount")) : 0;
                        value = Array.newInstance(columnClass, arrayCount);
                        // set the object's field to the value
                        setField(object.getClass().getDeclaredField(columnName), object, value);
                    } else if(Collection.class.isAssignableFrom(columnClass))
                    {
                        if(checkCollectionClass(columnClass))
                        {
                            // instantiate collection
                            value = columnClass.newInstance();
                        } else
                        {
                            value = new ArrayList<>();
                        }
                        // set the object's field to the value
                        setField(object.getClass().getDeclaredField(columnName), object, value);
                    } else if(Map.class.isAssignableFrom(columnClass))
                    {
                        if(checkMapClass(columnClass))
                        {
                            // instantiate map
                            value = columnClass.newInstance();
                        } else
                        {
                            value = new HashMap<>();
                        }
                        // set the object's field to the value
                        setField(object.getClass().getDeclaredField(columnName), object, value);
                    }
                }
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        } else if(stanza.getEventType() == CHARACTERS)
        {
            try
            {
                // get the class name of a field
                String className = stanza.getAttribute("class");

                // get the field's class by its class name
                columnClass = Class.forName(className);
                // get the field's name
                columnName = stanza.getAttribute("name");

                // check if field class is a primitive class, date class, time class, string class,
                // list class, map class or is an array
                if(columnClass != null && (columnClass
                        .isPrimitive() || columnClass == Integer.class || columnClass == Double.class || columnClass == Float.class || columnClass == Boolean.class || columnClass == LocalDate.class || columnClass == LocalTime.class || columnClass == LocalDateTime.class || columnClass == String.class || columnClass == Byte.class || columnClass == Short.class || columnClass == Character.class || columnClass == BigInteger.class))
                {
                    // get the field value as a string
                    Object value = stanza.getValue();
                    if(value != null)
                    {
                        if(columnClass == Integer.class)
                        {
                            // convert string to int
                            value = Integer.parseInt((String) value);
                        } else if(columnClass == Byte.class)
                        {
                            // convert string to byte
                            value = Byte.parseByte((String) value);
                        } else if(columnClass == Short.class)
                        {
                            // convert string to short
                            value = Short.parseShort((String) value);
                        } else if(columnClass == Character.class)
                        {
                            // convert string to char
                            object = ((String) value).charAt(0);
                        } else if(columnClass == Long.class)
                        {
                            // convert string to long
                            value = Long.parseLong((String) value);
                        } else if(columnClass == Float.class)
                        {
                            // convert string to float
                            value = Float.parseFloat((String) value);
                        } else if(columnClass == Double.class)
                        {
                            // convert string to double
                            value = Double.parseDouble((String) value);
                        } else if(columnClass == Boolean.class)
                        {
                            // convert string to bool
                            value = Boolean.parseBoolean((String) value);
                        } else if(columnClass == LocalDate.class)
                        {
                            // convert string to date
                            value = LocalDate.parse((String) value);
                        } else if(columnClass == LocalTime.class)
                        {
                            // convert string to time
                            value = LocalTime.parse((String) value);
                        } else if(columnClass == LocalDateTime.class)
                        {
                            // convert string to datetime
                            value = LocalDateTime.parse((String) value);
                        } else if(columnClass == BigInteger.class)
                        {
                            // convert string to datetime
                            value = new BigInteger((String) value);
                        }

                        // set the object's field to the value
                        setField(object.getClass().getDeclaredField(columnName), object, value);
                    }
                }
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        } else if(stanza.getEventType() == END_ELEMENT)
        {
            return null;
        }
        return new StanzaColumn(columnName, columnClass, isArrayColumn);
    }

    private static void unmarshalEntry(Object object, Stanza entryStanza, List<Stanza> stanzas,
                                       StanzaColumn stanzaColumn)
    {
        Class<?> columnClass = stanzaColumn != null ? stanzaColumn.columnClass : null;
        String columnName = stanzaColumn != null ? stanzaColumn.columnName : null;
        boolean isArrayColumn = stanzaColumn != null && stanzaColumn.isArrayColumn;

        // unmarshal the child object
        Object result = unmarshalStanzas(stanzas);

        // if a column is selected
        if(result != null)
        {
            if(columnClass != null && columnName != null)
            {
                try
                {
                    if(isArrayColumn)
                    {
                        // get object's field as array
                        Object[] array = get(object.getClass(), columnName, object);
                        String arrayIndexString = entryStanza.getAttribute("index");
                        if(arrayIndexString != null && arrayIndexString.matches("\\d+"))
                        {
                            int arrayIndex = Integer.parseInt(arrayIndexString);

                            // add child object to the array
                            if(array != null && arrayIndex < array.length)
                            {
                                array[arrayIndex] = result;
                            }
                        }
                    } else if(Collection.class.isAssignableFrom(columnClass))
                    {
                        if(checkCollectionClass(columnClass))
                        {
                            // get object's field as list
                            Collection collection = get(object.getClass(), columnName, object);

                            // add child object to the list
                            if(collection != null)
                            {
                                collection.add(result);
                            }
                        }
                    } else if(Map.class.isAssignableFrom(columnClass))
                    {
                        if(checkMapClass(columnClass))
                        {
                            // get object's field as map
                            Map map = get(object.getClass(), columnName, object);

                            // instantiate the key object
                            Object key = entryStanza.getAttribute("key");
                            String keyClassName = entryStanza.getAttribute("keyClass");
                            Class<?> keyClass = (keyClassName != null) ? Class.forName(keyClassName) : null;

                            if(keyClass == Integer.class)
                            {
                                key = Integer.parseInt((String) key);
                            } else if(keyClass == Byte.class)
                            {
                                key = Byte.parseByte((String) key);
                            } else if(keyClass == Short.class)
                            {
                                key = Short.parseShort((String) key);
                            } else if(columnClass == Character.class)
                            {
                                key = ((String) key).charAt(0);
                            } else if(keyClass == Long.class)
                            {
                                key = Long.parseLong((String) key);
                            } else if(keyClass == Float.class)
                            {
                                key = Float.parseFloat((String) key);
                            } else if(keyClass == Double.class)
                            {
                                key = Double.parseDouble((String) key);
                            } else if(keyClass == Boolean.class)
                            {
                                key = Boolean.parseBoolean((String) key);
                            } else if(keyClass == LocalDate.class)
                            {
                                key = LocalDate.parse((String) key);
                            } else if(keyClass == LocalTime.class)
                            {
                                key = LocalTime.parse((String) key);
                            } else if(keyClass == LocalDateTime.class)
                            {
                                key = LocalDateTime.parse((String) key);
                            } else if(keyClass == BigInteger.class)
                            {
                                key = new BigInteger((String) key);
                            }

                            // put child object with key to map
                            if(map != null && key != null && keyClass != null)
                            {
                                map.put(key, result);
                            }
                        }
                    } else
                    {
                        // set the object's field with its child
                        setField(object.getClass().getDeclaredField(columnName), object, result);
                    }

                } catch(Exception e)
                {
                    e.printStackTrace();
                }
            } else
            {
                try
                {
                    if(object.getClass().isArray())
                    {
                        String arrayIndexString = entryStanza.getAttribute("index");
                        if(arrayIndexString != null && arrayIndexString.matches("\\d+"))
                        {
                            int arrayIndex = Integer.parseInt(arrayIndexString);

                            switch(object.getClass().getCanonicalName())
                            {
                                case "boolean[]":
                                    boolean[] booleans = (boolean[]) object;
                                    if(arrayIndex < booleans.length)
                                    {
                                        booleans[arrayIndex] = (boolean) result;
                                    }
                                    break;
                                case "byte[]":
                                    byte[] bytes = (byte[]) object;
                                    if(arrayIndex < bytes.length)
                                    {
                                        bytes[arrayIndex] = (byte) result;
                                    }
                                    break;
                                case "short[]":
                                    short[] shorts = (short[]) object;
                                    if(arrayIndex < shorts.length)
                                    {
                                        shorts[arrayIndex] = (short) result;
                                    }
                                    break;
                                case "int[]":
                                    int[] ints = (int[]) object;
                                    if(arrayIndex < ints.length)
                                    {
                                        ints[arrayIndex] = (int) result;
                                    }
                                    break;
                                case "long[]":
                                    long[] longs = (long[]) object;
                                    if(arrayIndex < longs.length)
                                    {
                                        longs[arrayIndex] = (long) result;
                                    }
                                    break;
                                case "float[]":
                                    float[] floats = (float[]) object;
                                    if(arrayIndex < floats.length)
                                    {
                                        floats[arrayIndex] = (float) result;
                                    }
                                    break;
                                case "double[]":
                                    double[] doubles = (double[]) object;
                                    if(arrayIndex < doubles.length)
                                    {
                                        doubles[arrayIndex] = (double) result;
                                    }
                                    break;
                                case "char[]":
                                    char[] chars = (char[]) object;
                                    if(arrayIndex < chars.length)
                                    {
                                        chars[arrayIndex] = (char) result;
                                    }
                                    break;
                                case "java.math.BigInteger[]":
                                    BigInteger[] integers = (BigInteger[]) object;
                                    if(arrayIndex < integers.length)
                                    {
                                        integers[arrayIndex] = (BigInteger) result;
                                    }
                                    break;
                                default:
                                    Object[] array = (Object[]) object;
                                    // add child object to the array
                                    if(arrayIndex < array.length)
                                    {
                                        array[arrayIndex] = result;
                                    }
                                    break;
                            }

                        }
                    } else if(object instanceof Collection)
                    {
                        Collection collection = (Collection) object;

                        // add child object to the list
                        if(checkCollectionClass(collection.getClass()))
                        {
                            collection.add(result);
                        }
                    } else if(object instanceof Map)
                    {
                        Map map = (Map) object;

                        if(checkMapClass(map.getClass()))
                        {
                            // instantiate the key object
                            Object key = entryStanza.getAttribute("key");
                            String keyClassName = entryStanza.getAttribute("keyClass");
                            Class<?> keyClass = (keyClassName != null) ? Class.forName(keyClassName) : null;

                            if(keyClass == Integer.class)
                            {
                                key = Integer.parseInt((String) key);
                            } else if(keyClass == Byte.class)
                            {
                                key = Byte.parseByte((String) key);
                            } else if(keyClass == Short.class)
                            {
                                key = Short.parseShort((String) key);
                            } else if(keyClass == Character.class)
                            {
                                key = ((String) key).charAt(0);
                            } else if(keyClass == Long.class)
                            {
                                key = Long.parseLong((String) key);
                            } else if(keyClass == Float.class)
                            {
                                key = Float.parseFloat((String) key);
                            } else if(keyClass == Double.class)
                            {
                                key = Double.parseDouble((String) key);
                            } else if(keyClass == Boolean.class)
                            {
                                key = Boolean.parseBoolean((String) key);
                            } else if(keyClass == LocalDate.class)
                            {
                                key = LocalDate.parse((String) key);
                            } else if(keyClass == LocalTime.class)
                            {
                                key = LocalTime.parse((String) key);
                            } else if(keyClass == LocalDateTime.class)
                            {
                                key = LocalDateTime.parse((String) key);
                            } else if(keyClass == BigInteger.class) {
                                key = new BigInteger((String) key);
                            } else if (keyClass.isEnum()) {
                                Object finalKey = key;

                                Optional optional = EnumSet.allOf((Class) keyClass).stream()
                                        .filter(e -> e.getClass().isEnum() && finalKey.equals(((Enum<?>) e).name())).findFirst();
                                if (optional.isPresent()) {
                                    key = optional.get();
                                }
                            }

                            // put child object with key to map
                            if(key != null && keyClass != null)
                            {
                                map.put(key, result);
                            }
                        }
                    }
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
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
    private static <T> T get(Class<?> columnClass, String columnName, Object object) throws NoSuchFieldException,
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
     * @param startIndex start index where the search should start
     * @param list       the list on which the search runs
     * @return index of the closest end stanza without opening stanza
     */
    private static int searchCloseEntryIndexInStack(int startIndex, List<Stanza> list)
    {
        // initialize a stack for the stanzas
        LinkedList<Stanza> stack = new LinkedList<Stanza>();

        Stanza start = list.get(startIndex);

        for(int index = startIndex + 1; index < list.size(); index++)
        {
            Stanza stanza = list.get(index);
            // if start stanza and not empty element
            if(stanza.getEventType() == START_ELEMENT && !stanza.isEmptyElement())
            {
                // push the stanza on the top of the stack
                stack.push(stanza);
            } else if(stanza.getEventType() == END_ELEMENT)
            {
                // if stack is empty, the end stanza's index is found, return the index
                if(stack.isEmpty())
                {
                    if(stanza.getName().equals(start.getName()))
                    {
                        return index + 1;
                    }
                    return startIndex;
                }

                // get the stanza on the top of the stack
                Stanza peek = stack.peek();
                // if top of the stack is a start stanza and name of stanza equals stanza on the top of the stack
                if(peek.getEventType() == START_ELEMENT && peek.getName().equals(stanza.getName()))
                {
                    // pop (remove first) from the top of the stanza stack
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
    private static List<Stanza> getField(Field field, Object object, long sequenceId) throws IllegalAccessException
    {
        if(field == null || object == null)
        {
            return null;
        }

        // set the field accessible (e.g. if it have the modifier private)
        field.setAccessible(true);

        // get field name and value
        String name = field.getName();
        Object value = field.get(object);

        List<Stanza> results = new ArrayList<Stanza>();
        if(value != null)
        {
            // field will be ignored if its value is null
            Stanza result = new Stanza();
            result.setName("legion:column");
            result.setEventType(START_ELEMENT);
            result.putAttribute("name", name);
            result.putAttribute("class", value.getClass().getCanonicalName());
            result.setSequenceId(sequenceId);
            results.add(result);

            if(value.getClass().isArray()) // check if value is an array
            {
                // set array lenght for unmarshalling
                result.putAttribute("arrayCount", Integer.toString(((Object[]) value).length));
                // marshal items of the array
                results.addAll(getStreamEntry(Stream.of((Object[]) value), sequenceId));

                result = new Stanza();
                result.setName("legion:column");
                result.setEventType(END_ELEMENT);
                result.setSequenceId(sequenceId);
                results.add(result);
            } else if(value instanceof Collection) // value is a list
            {
                Collection collection = (Collection) value;
                if(!checkCollectionClass(collection.getClass()))
                {
                    collection = convertCollection(collection);
                    result.putAttribute("class", collection.getClass().getCanonicalName());
                }

                // marshal items of the list
                results.addAll(getStreamEntry(collection.stream(), sequenceId));

                result = new Stanza();
                result.setName("legion:column");
                result.setEventType(END_ELEMENT);
                result.setSequenceId(sequenceId);
                results.add(result);
            } else if(value instanceof Map) // value is a map
            {
                Map map = (Map) value;

                if(!checkMapClass(map.getClass()))
                {
                    map = convertMap(map);
                    result.putAttribute("class", map.getClass().getCanonicalName());
                }
                // marshal keys and items of the map
                results.addAll(getMapEntry(map, sequenceId));

                result = new Stanza();
                result.setName("legion:column");
                result.setSequenceId(sequenceId);
                result.setEventType(END_ELEMENT);
                results.add(result);
            } else if(value.getClass()
                    .isPrimitive() || value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Byte || value instanceof Short || value instanceof Float || value instanceof Boolean || value instanceof LocalDate || value instanceof LocalTime || value instanceof LocalDateTime || value instanceof String || value instanceof Character || value instanceof BigInteger)
            {
                // convert primitive types, date, time and string to string
                result.setEventType(CHARACTERS);
                result.setValue(value.toString());
            } else
            {
                // otherwise marshal the child object
                result = new Stanza();
                result.setName("legion:entry");
                result.setEventType(START_ELEMENT);
                result.setSequenceId(sequenceId);
                results.add(result);

                results.addAll(marshal(value, sequenceId));

                result = new Stanza();
                result.setName("legion:entry");
                result.setEventType(END_ELEMENT);
                result.setSequenceId(sequenceId);
                results.add(result);

                result = new Stanza();
                result.setName("legion:column");
                result.setEventType(END_ELEMENT);
                result.setSequenceId(sequenceId);
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
    private static <T> List<Stanza> getStreamEntry(Stream<T> stream, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        // for every element of the stream
        stream.forEach(value ->
                       {
                           Stanza result = new Stanza();
                           result.setName("legion:entry");
                           result.setEventType(START_ELEMENT);
                           result.setSequenceId(sequenceId);
                           result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
                           results.add(result);

                           // marshal the value
                           List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

                           if(stanzas != null)
                           {
                               results.addAll(stanzas);
                           }

                           result = new Stanza();
                           result.setName("legion:entry");
                           result.setEventType(END_ELEMENT);
                           result.setSequenceId(sequenceId);
                           results.add(result);
                       });

        return results;
    }

    private static List<Stanza> getEntry(boolean[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(boolean value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    private static List<Stanza> getEntry(byte[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(byte value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    private static List<Stanza> getEntry(short[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(short value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    private static List<Stanza> getEntry(int[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(int value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    private static List<Stanza> getEntry(float[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(float value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    private static List<Stanza> getEntry(double[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(double value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    private static List<Stanza> getEntry(long[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(long value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    private static List<Stanza> getEntry(char[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(char value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    private static List<Stanza> getEntry(BigInteger[] array, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();
        AtomicInteger counter = new AtomicInteger();

        for(BigInteger value : array)
        {
            Stanza result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(START_ELEMENT);
            result.setSequenceId(sequenceId);
            result.putAttribute("index", Integer.toString(counter.getAndIncrement()));
            results.add(result);

            // marshal the value
            List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

            if(stanzas != null)
            {
                results.addAll(stanzas);
            }

            result = new Stanza();
            result.setName("legion:entry");
            result.setEventType(END_ELEMENT);
            result.setSequenceId(sequenceId);
            results.add(result);
        }
        return results;
    }

    /**
     * Marshalls a map to a list of xml stanza
     *
     * @param map
     * @return
     */
    private static <S, T> List<Stanza> getMapEntry(Map<S, T> map, long sequenceId)
    {
        List<Stanza> results = new ArrayList<Stanza>();

        // for every key value pair in the map
        map.forEach((key, value) ->
                    {
                        Stanza result = new Stanza();
                        result.setName("legion:entry");
                        result.setEventType(START_ELEMENT);
                        // set the key class and key
                        result.putAttribute("keyClass", key.getClass().getCanonicalName());
                        result.putAttribute("key", key.toString());
                        result.setSequenceId(sequenceId);
                        results.add(result);

                        // marshal the value
                        List<Stanza> stanzas = StanzaMarshaller.marshal(value, sequenceId);

                        if(stanzas != null)
                        {
                            results.addAll(stanzas);
                        }

                        result = new Stanza();
                        result.setName("legion:entry");
                        result.setEventType(END_ELEMENT);
                        result.setSequenceId(sequenceId);
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
        if(field.getType().isAssignableFrom(value.getClass()) || field.getType().isPrimitive())
        {
            field.setAccessible(true);
            field.set(object, value);
        }
    }

    private static boolean checkCollectionClass(Class<?> clazz)
    {
        return ALLOWED_COLLECTION_CLASSES.contains(clazz);
    }

    private static boolean checkMapClass(Class<?> clazz)
    {
        return ALLOWED_MAP_CLASSES.contains(clazz);
    }

    private static <T> Collection<T> convertCollection(Collection<T> collection)
    {
        if(collection instanceof Queue)
        {
            Queue<T> queue = new ArrayDeque<T>();
            queue.addAll(collection);
            return queue;
        } else if(collection instanceof List)
        {
            return new ArrayList<T>((List<T>) collection);
        } else if(collection instanceof Set)
        {
            HashSet<T> set = new HashSet<T>();
            set.addAll(collection);
            return set;
        }
        return collection;
    }

    private static Map convertMap(Map map)
    {
        Map newMap = new HashMap<>(map);
        return newMap;
    }
}
