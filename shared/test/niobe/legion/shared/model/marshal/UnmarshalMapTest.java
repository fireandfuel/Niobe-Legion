/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (UnmarshalMapTest.java) is part of Niobe Legion (module niobe-legion-shared_test).
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
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamConstants;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author fireandfuel
 */
public class UnmarshalMapTest implements XMLStreamConstants
{
    @Test
    public void testBooleanMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Boolean");
        stanza.putAttribute("key", "false");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("true");
        stanza.putAttribute("class", "java.lang.Boolean");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Boolean");
        stanza.putAttribute("key", "true");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("false");
        stanza.putAttribute("class", "java.lang.Boolean");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(Boolean.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals(false, key);
                    break;
                case 1:
                    Assert.assertEquals(true, key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(Boolean.class, map.get(true).getClass());
        Assert.assertEquals(false, map.get(true));
        Assert.assertEquals(Boolean.class, map.get(false).getClass());
        Assert.assertEquals(true, map.get(false));
    }

    @Test
    public void testByteMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Byte");
        stanza.putAttribute("key", "1");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("2");
        stanza.putAttribute("class", "java.lang.Byte");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Byte");
        stanza.putAttribute("key", "3");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("4");
        stanza.putAttribute("class", "java.lang.Byte");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(Byte.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals((byte) 1, key);
                    break;
                case 1:
                    Assert.assertEquals((byte) 3, key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(Byte.class, map.get((byte) 1).getClass());
        Assert.assertEquals((byte) 2, map.get((byte) 1));
        Assert.assertEquals(Byte.class, map.get((byte) 3).getClass());
        Assert.assertEquals((byte) 4, map.get((byte) 3));
    }

    @Test
    public void testShortMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Short");
        stanza.putAttribute("key", "1");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("2");
        stanza.putAttribute("class", "java.lang.Short");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Short");
        stanza.putAttribute("key", "3");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("4");
        stanza.putAttribute("class", "java.lang.Short");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(Short.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals((short) 1, key);
                    break;
                case 1:
                    Assert.assertEquals((short) 3, key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(Short.class, map.get((short) 1).getClass());
        Assert.assertEquals((short) 2, map.get((short) 1));
        Assert.assertEquals(Short.class, map.get((short) 3).getClass());
        Assert.assertEquals((short) 4, map.get((short) 3));
    }

    @Test
    public void testIntegerMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Integer");
        stanza.putAttribute("key", "1");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("2");
        stanza.putAttribute("class", "java.lang.Integer");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Integer");
        stanza.putAttribute("key", "3");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("4");
        stanza.putAttribute("class", "java.lang.Integer");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(Integer.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals(1, key);
                    break;
                case 1:
                    Assert.assertEquals(3, key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(Integer.class, map.get(1).getClass());
        Assert.assertEquals(2, map.get(1));
        Assert.assertEquals(Integer.class, map.get(3).getClass());
        Assert.assertEquals(4, map.get(3));
    }

    @Test
    public void testLongMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Long");
        stanza.putAttribute("key", "1");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("2");
        stanza.putAttribute("class", "java.lang.Long");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Long");
        stanza.putAttribute("key", "3");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("4");
        stanza.putAttribute("class", "java.lang.Long");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(Long.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals(1L, key);
                    break;
                case 1:
                    Assert.assertEquals(3L, key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(Long.class, map.get(1L).getClass());
        Assert.assertEquals(2L, map.get(1L));
        Assert.assertEquals(Long.class, map.get(3L).getClass());
        Assert.assertEquals(4L, map.get(3L));
    }

    @Test
    public void testFloatMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Float");
        stanza.putAttribute("key", "0.5");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1.2");
        stanza.putAttribute("class", "java.lang.Float");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Float");
        stanza.putAttribute("key", "3.14");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("4");
        stanza.putAttribute("class", "java.lang.Float");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(Float.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals(0.5F, key);
                    break;
                case 1:
                    Assert.assertEquals(3.14F, key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(Float.class, map.get(0.5F).getClass());
        Assert.assertEquals(1.2F, map.get(0.5F));
        Assert.assertEquals(Float.class, map.get(3.14F).getClass());
        Assert.assertEquals(4F, map.get(3.14F));
    }

    @Test
    public void testDoubleMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Double");
        stanza.putAttribute("key", "0.5");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1.2");
        stanza.putAttribute("class", "java.lang.Double");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Double");
        stanza.putAttribute("key", "3.14");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("4");
        stanza.putAttribute("class", "java.lang.Double");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(Double.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals(0.5D, key);
                    break;
                case 1:
                    Assert.assertEquals(3.14D, key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(Double.class, map.get(0.5D).getClass());
        Assert.assertEquals(1.2D, map.get(0.5D));
        Assert.assertEquals(Double.class, map.get(3.14D).getClass());
        Assert.assertEquals(4D, map.get(3.14D));
    }

    @Test
    public void testCharacterMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Character");
        stanza.putAttribute("key", "A");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("B");
        stanza.putAttribute("class", "java.lang.Character");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.Character");
        stanza.putAttribute("key", "C");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("D");
        stanza.putAttribute("class", "java.lang.Character");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(Character.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals('A', key);
                    break;
                case 1:
                    Assert.assertEquals('C', key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(Character.class, map.get('A').getClass());
        Assert.assertEquals('B', map.get('A'));
        Assert.assertEquals(Character.class, map.get('C').getClass());
        Assert.assertEquals('D', map.get('C'));
    }

    @Test
    public void testStringMapUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.String");
        stanza.putAttribute("key", "A");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("B");
        stanza.putAttribute("class", "java.lang.String");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.lang.String");
        stanza.putAttribute("key", "C");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("D");
        stanza.putAttribute("class", "java.lang.String");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet())
        {
            Assert.assertNotNull(key);
            Assert.assertEquals(String.class, key.getClass());
            switch(counter)
            {
                case 0:
                    Assert.assertEquals("A", key);
                    break;
                case 1:
                    Assert.assertEquals("C", key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(String.class, map.get("A").getClass());
        Assert.assertEquals("B", map.get("A"));
        Assert.assertEquals(String.class, map.get("C").getClass());
        Assert.assertEquals("D", map.get("C"));
    }

    @Test
    public void testBigIntegerMapUnmarshal() {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.math.BigInteger");
        stanza.putAttribute("key", "1");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1000");
        stanza.putAttribute("class", "java.math.BigInteger");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", "java.math.BigInteger");
        stanza.putAttribute("key", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("2000");
        stanza.putAttribute("class", "java.math.BigInteger");
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for (Object key : map.keySet()) {
            Assert.assertNotNull(key);
            Assert.assertEquals(BigInteger.class, key.getClass());
            switch (counter) {
                case 0:
                    Assert.assertEquals(new BigInteger("1"), key);
                    break;
                case 1:
                    Assert.assertEquals(new BigInteger("2"), key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(BigInteger.class, map.get(new BigInteger("1")).getClass());
        Assert.assertEquals(new BigInteger("1000"), map.get(new BigInteger("1")));
        Assert.assertEquals(BigInteger.class, map.get(new BigInteger("2")).getClass());
        Assert.assertEquals(new BigInteger("2000"), map.get(new BigInteger("2")));
    }

    @Test
    public void testEnumMapUnmarshal() {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.TreeMap");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", TestEnum.class.getName());
        stanza.putAttribute("key", TestEnum.FIRST.name());
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue(TestEnum.SECOND.name());
        stanza.putAttribute("class", TestEnum.class.getName());
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("keyClass", TestEnum.class.getName());
        stanza.putAttribute("key", TestEnum.THIRD.name());
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue(TestEnum.FOURTH.name());
        stanza.putAttribute("class", TestEnum.class.getName());
        stanza.setSequenceId(12L);
        stanza.setEmptyElement(true);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TreeMap.class, objects.get(0).getClass());
        TreeMap map = (TreeMap) objects.get(0);
        Assert.assertEquals(2, map.size());
        int counter = 0;
        for(Object key : map.keySet()) {
            Assert.assertNotNull(key);
            Assert.assertEquals(TestEnum.class, key.getClass());
            switch(counter) {
                case 0:
                    Assert.assertEquals(TestEnum.FIRST, key);
                    break;
                case 1:
                    Assert.assertEquals(TestEnum.THIRD, key);
                    break;
            }
            counter++;
        }
        Assert.assertEquals(2, counter);
        Assert.assertEquals(TestEnum.class, map.get(TestEnum.FIRST).getClass());
        Assert.assertEquals(TestEnum.SECOND, map.get(TestEnum.FIRST));
        Assert.assertEquals(TestEnum.class, map.get(TestEnum.THIRD).getClass());
        Assert.assertEquals(TestEnum.FOURTH, map.get(TestEnum.THIRD));
    }
}
