/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (UnmarshalSimpleTest.java) is part of Niobe Legion (module niobe-legion-shared_test).
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
import java.util.Arrays;
import java.util.List;

/**
 * @author fireandfuel
 */
public class UnmarshalSimpleTest implements XMLStreamConstants
{
    @Test
    public void testBooleanUnmarshal()
    {
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue("true");
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.Boolean");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(Boolean.class, objects.get(0).getClass());
        boolean value = (boolean) objects.get(0);
        Assert.assertEquals(true, value);
    }

    @Test
    public void testByteUnmarshal()
    {
        byte expected = (byte) (Math.random() * 255d);
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(Byte.toString(expected));
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.Byte");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(Byte.class, objects.get(0).getClass());
        byte value = (byte) objects.get(0);
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testShortUnmarshal()
    {
        short expected = (short) (Math.random() * 255d);
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(Short.toString(expected));
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.Short");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(Short.class, objects.get(0).getClass());
        short value = (short) objects.get(0);
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testIntegerUnmarshal()
    {
        int expected = (int) (Math.random() * 1000d);
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(Integer.toString(expected));
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.Integer");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(Integer.class, objects.get(0).getClass());
        int value = (int) objects.get(0);
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testLongUnmarshal()
    {
        long expected = (long) (Math.random() * 1000d);
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(Long.toString(expected));
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.Long");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(Long.class, objects.get(0).getClass());
        long value = (long) objects.get(0);
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testFloatUnmarshal()
    {
        float expected = (float) Math.random();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(Float.toString(expected));
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.Float");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(Float.class, objects.get(0).getClass());
        float value = (float) objects.get(0);
        Assert.assertEquals(expected, value, 0D);
    }

    @Test
    public void testDoubleUnmarshal()
    {
        double expected = Math.random();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(Double.toString(expected));
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.Double");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(Double.class, objects.get(0).getClass());
        double value = (double) objects.get(0);
        Assert.assertEquals(expected, value, 0D);
    }

    @Test
    public void testCharacterUnmarshal()
    {
        char expected = 'c';
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(Character.toString(expected));
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.Character");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(Character.class, objects.get(0).getClass());
        char value = (char) objects.get(0);
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testStringUnmarshal()
    {
        String expected = "SomeTestString";
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(expected);
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.lang.String");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(String.class, objects.get(0).getClass());
        String value = (String) objects.get(0);
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testBigIntegerUnmarshal()
    {
        BigInteger expected = new BigInteger("1024");
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue("1024");
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", "java.math.BigInteger");
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(BigInteger.class, objects.get(0).getClass());
        BigInteger value = (BigInteger) objects.get(0);
        Assert.assertEquals(expected, value);
    }

    @Test
    public void testEnumUnmarshal() {
        TestEnum expected = TestEnum.FIRST;
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setValue(TestEnum.FIRST.name());
        stanza.putAttribute("sequenceId", "12");
        stanza.putAttribute("class", TestEnum.class.getName());
        stanza.setEventType(START_ELEMENT);
        List<Stanza> stanzas = new ArrayList<Stanza>(Arrays.asList(stanza));
        List<Object> objects = StanzaMarshaller.unmarshal(stanzas);

        Assert.assertNotNull(objects);
        Assert.assertEquals(1, objects.size());
        Assert.assertEquals(TestEnum.class, objects.get(0).getClass());
        TestEnum value = (TestEnum) objects.get(0);
        Assert.assertEquals(expected, value);
    }
}
