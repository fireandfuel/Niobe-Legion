/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (UnmarshalArrayTest.java) is part of Niobe Legion (module niobe-legion-shared_test).
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

/**
 * @author fireandfuel
 */
public class UnmarshalArrayTest implements XMLStreamConstants
{
    @Test
    public void testBooleanArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.Boolean[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
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
        stanza.putAttribute("index", "1");
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
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "2");
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
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(boolean[].class, objects.get(0).getClass());
        boolean[] array = (boolean[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(true, array[0]);
        Assert.assertEquals(false, array[1]);
        Assert.assertEquals(true, array[2]);
    }

    @Test
    public void testByteArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.Byte[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1");
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
        stanza.putAttribute("index", "1");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3");
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
        Assert.assertEquals(byte[].class, objects.get(0).getClass());
        byte[] array = (byte[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(1, array[0]);
        Assert.assertEquals(2, array[1]);
        Assert.assertEquals(3, array[2]);
    }

    @Test
    public void testShortArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.Short[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1");
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
        stanza.putAttribute("index", "1");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3");
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
        Assert.assertEquals(short[].class, objects.get(0).getClass());
        short[] array = (short[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(1, array[0]);
        Assert.assertEquals(2, array[1]);
        Assert.assertEquals(3, array[2]);
    }

    @Test
    public void testIntegerArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.Integer[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1");
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
        stanza.putAttribute("index", "1");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3");
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
        Assert.assertEquals(int[].class, objects.get(0).getClass());
        int[] array = (int[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(1L, array[0]);
        Assert.assertEquals(2L, array[1]);
        Assert.assertEquals(3L, array[2]);
    }

    @Test
    public void testLongArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.Long[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1");
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
        stanza.putAttribute("index", "1");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3");
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
        Assert.assertEquals(long[].class, objects.get(0).getClass());
        long[] array = (long[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(1, array[0]);
        Assert.assertEquals(2, array[1]);
        Assert.assertEquals(3, array[2]);
    }

    @Test
    public void testFloatArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.Float[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("0.5");
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
        stanza.putAttribute("index", "1");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3.14");
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
        Assert.assertEquals(float[].class, objects.get(0).getClass());
        float[] array = (float[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(0.5F, array[0], 0F);
        Assert.assertEquals(1.2F, array[1], 0F);
        Assert.assertEquals(3.14F, array[2], 0F);
    }

    @Test
    public void testDoubleArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.Double[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("0.5");
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
        stanza.putAttribute("index", "1");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3.14");
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
        Assert.assertEquals(double[].class, objects.get(0).getClass());
        double[] array = (double[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(0.5D, array[0], 0D);
        Assert.assertEquals(1.2D, array[1], 0D);
        Assert.assertEquals(3.14D, array[2], 0D);
    }

    @Test
    public void testCharacterArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.Character[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1");
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
        stanza.putAttribute("index", "1");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("2");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3");
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
        Assert.assertEquals(char[].class, objects.get(0).getClass());
        char[] array = (char[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals('1', array[0]);
        Assert.assertEquals('2', array[1]);
        Assert.assertEquals('3', array[2]);
    }

    @Test
    public void testStringArrayUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.lang.String[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1");
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
        stanza.putAttribute("index", "1");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("2");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3");
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
        Assert.assertEquals(String[].class, objects.get(0).getClass());
        String[] array = (String[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals("1", array[0]);
        Assert.assertEquals("2", array[1]);
        Assert.assertEquals("3", array[2]);
    }

    @Test
    public void testBigIntegerArrayUnmarshal() {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.math.BigInteger[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("1");
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
        stanza.putAttribute("index", "1");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("2");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue("3");
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
        Assert.assertEquals(BigInteger[].class, objects.get(0).getClass());
        BigInteger[] array = (BigInteger[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(new BigInteger("1"), array[0]);
        Assert.assertEquals(new BigInteger("2"), array[1]);
        Assert.assertEquals(new BigInteger("3"), array[2]);
    }

    @Test
    public void testEnumArrayUnmarshal() {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", TestEnum.class.getName() + "[]");
        stanza.putAttribute("arrayCount", "3");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanza.putAttribute("index", "0");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue(TestEnum.FIRST.name());
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
        stanza.putAttribute("index", "1");
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
        stanza.putAttribute("index", "2");
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.setValue(TestEnum.THIRD.name());
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
        Assert.assertEquals(TestEnum[].class, objects.get(0).getClass());
        TestEnum[] array = (TestEnum[]) objects.get(0);
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(TestEnum.FIRST, array[0]);
        Assert.assertEquals(TestEnum.SECOND, array[1]);
        Assert.assertEquals(TestEnum.THIRD, array[2]);
    }
}
