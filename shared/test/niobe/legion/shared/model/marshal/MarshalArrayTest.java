/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MarshalArrayTest.java) is part of Niobe Legion (module niobe-legion-shared_test).
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
import java.util.List;

/**
 * @author fireandfuel
 */
public class MarshalArrayTest implements XMLStreamConstants
{
    @Test
    public void testBooleanArrayMarshal()
    {
        boolean[] array = {true, false, true};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Boolean[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testByteArrayMarshal()
    {
        byte[] array = {1, 2, 3};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Byte[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testShortArrayMarshal()
    {
        short[] array = {1, 2, 3};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Short[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testIntegerArrayMarshal()
    {
        int[] array = {1, 2, 3};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testFloatArrayMarshal()
    {
        float[] array = {0.5F, 1.2F, 3.14F};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Float[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testDoubleArrayMarshal()
    {
        double[] array = {0.5D, 1.2D, 3.14D};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Double[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testLongArrayMarshal()
    {
        long[] array = {1L, 2L, 3L};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Long[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testCharacterArrayMarshal()
    {
        char[] array = {'1', '2', '3'};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Character[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testStringArrayMarshal()
    {
        String[] array = {"1", "2", "3"};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
    public void testBigIntegerArrayMarshal()
    {
        BigInteger[] array = {new BigInteger("1"), new BigInteger("2"), new BigInteger("3")};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
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
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.math.BigInteger[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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

    @Test
    public void testEnumArrayMarshal() {
        TestEnum[] array = {TestEnum.FIRST, TestEnum.SECOND, TestEnum.THIRD};
        List<Stanza> stanzaList = StanzaMarshaller.marshal(array, 12);
        Assert.assertEquals(11, stanzaList.size());
        for (int index = 0; index < stanzaList.size(); index++) {
            Stanza stanza = stanzaList.get(index);
            System.out.println("Test Stanza " + index + " of " + (stanzaList.size() - 1));

            Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
            switch (index) {
                case 0:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals(TestEnum.class.getName() + "[]", stanza.getAttribute("class"));
                    Assert.assertEquals("3", stanza.getAttribute("arrayCount"));
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
                    Assert.assertEquals(TestEnum.FIRST.name(), stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(TestEnum.class.getName(), stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(TestEnum.SECOND.name(), stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(TestEnum.class.getName(), stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertTrue(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(TestEnum.THIRD.name(), stanza.getValue());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(TestEnum.class.getName(), stanza.getAttribute("class"));
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
