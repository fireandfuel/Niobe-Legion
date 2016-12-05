/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MarshalListTest.java) is part of Niobe Legion (module niobe-legion-shared_test).
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import niobe.legion.shared.data.Stanza;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fireandfuel
 */
public class MarshalListTest implements XMLStreamConstants
{
    @Test
    public void testBooleanListMarshal()
    {
        List<Boolean> list = new ArrayList<Boolean>(Arrays.asList(true, false, true));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
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
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
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
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("false", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Boolean", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("true", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Boolean", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testByteListMarshal()
    {
        List<Byte> list = new ArrayList<Byte>(Arrays.asList((byte) 1, (byte) 2, (byte) 3));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
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
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Byte", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Byte", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Byte", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testShortListMarshal()
    {
        List<Short> list = new ArrayList<Short>(Arrays.asList((short) 1, (short) 2, (short) 3));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
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
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Short", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Short", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Short", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testIntegerListMarshal()
    {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
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
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testFloatListMarshal()
    {
        List<Float> list = new ArrayList<Float>(Arrays.asList(0.5F, 1.2F, 3.14F));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
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
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("0.5", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Float", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1.2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Float", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3.14", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Float", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testDoubleListMarshal()
    {
        List<Double> list = new ArrayList<Double>(Arrays.asList(0.5D, 1.2D, 3.14D));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
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
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("0.5", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Double", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1.2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Double", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3.14", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Double", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testLongListMarshal()
    {
        List<Long> list = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
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
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Long", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Long", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Long", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testCharacterListMarshal()
    {
        List<Character> list = new ArrayList<Character>(Arrays.asList('1', '2', '3'));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Character", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Character", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Character", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testStringListMarshal()
    {
        List<String> list = new ArrayList<String>(Arrays.asList("1", "2", "3"));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }

    @Test
    public void testBigIntegerListMarshal()
    {
        List<BigInteger> list = new ArrayList<BigInteger>(Arrays.asList(new BigInteger("1"),
                                                                        new BigInteger("2"),
                                                                        new BigInteger("3")));
        List<Stanza> stanzaList = StanzaMarshaller.marshal(list, 12);
        Assert.assertEquals(11, stanzaList.size());
        for(int index = 0; index < stanzaList.size(); index++)
        {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch(index)
            {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 4:
                case 7:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(Integer.toString((index - 1) / 3), stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("1", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.math.BigInteger", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("2", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.math.BigInteger", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals("3", stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.math.BigInteger", stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 3:
                case 6:
                case 9:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }
}
