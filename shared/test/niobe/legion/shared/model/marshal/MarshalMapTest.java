/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MarshalMapTest.java) is part of Niobe Legion (module niobe-legion-shared_test).
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

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.stream.XMLStreamConstants;
import niobe.legion.shared.data.Stanza;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fireandfuel
 */
public class MarshalMapTest implements XMLStreamConstants
{
    @Test
    public void testBooleanMapMarshal()
    {
        Map<Boolean, Boolean> map = new TreeMap<Boolean, Boolean>();
        map.put(false, true);
        map.put(true, false);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Boolean", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("false", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("true", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Boolean", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Boolean", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("true", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("false", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Boolean", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testByteMapMarshal()
    {
        Map<Byte, Byte> map = new TreeMap<Byte, Byte>();
        map.put((byte) 1, (byte) 2);
        map.put((byte) 2, (byte) 1);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Byte", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("1", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Byte", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Byte", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("2", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Byte", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testShortMapMarshal()
    {
        Map<Short, Short> map = new TreeMap<Short, Short>();
        map.put((short) 1, (short) 2);
        map.put((short) 2, (short) 1);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Short", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("1", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Short", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Short", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("2", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Short", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testIntegerMapMarshal()
    {
        Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
        map.put(1, 2);
        map.put(2, 1);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("1", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("2", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testFloatMapMarshal()
    {
        Map<Float, Float> map = new TreeMap<Float, Float>();
        map.put(1.2F, 3.14F);
        map.put(3.14F, 1.2F);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Float", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("1.2", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3.14", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Float", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Float", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("3.14", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1.2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Float", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testDoubleMapMarshal()
    {
        Map<Double, Double> map = new TreeMap<Double, Double>();
        map.put(1.2D, 3.14D);
        map.put(3.14D, 1.2D);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Double", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("1.2", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3.14", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Double", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Double", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("3.14", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1.2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Double", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testLongMapMarshal()
    {
        Map<Long, Long> map = new TreeMap<Long, Long>();
        map.put(1L, 2L);
        map.put(2L, 1L);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Long", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("1", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Long", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Long", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("2", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Long", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testCharacterMapMarshal()
    {
        Map<Character, Character> map = new TreeMap<Character, Character>();
        map.put('A', 'B');
        map.put('C', 'D');
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Character", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("A", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("B", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Character", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Character", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("C", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("D", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Character", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testStringMapMarshal()
    {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("A", "B");
        map.put("C", "D");
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("A", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("B", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("C", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("D", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testBigIntegerMapMarshal()
    {
        Map<BigInteger, BigInteger> map = new TreeMap<BigInteger, BigInteger>();
        map.put(new BigInteger("1"), new BigInteger("1000"));
        map.put(new BigInteger("2"), new BigInteger("2000"));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(map, 12);
        Assert.assertEquals(8, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.TreeMap", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.math.BigInteger", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("1", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1000", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.math.BigInteger", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.math.BigInteger", stanza.getAttribute("keyClass"));
                    Assert.assertEquals("2", stanza.getAttribute("key"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2000", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.math.BigInteger", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 7:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }
}
