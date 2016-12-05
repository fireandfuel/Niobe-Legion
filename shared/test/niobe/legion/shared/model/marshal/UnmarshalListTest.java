/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (UnmarshalListTest.java) is part of Niobe Legion (module niobe-legion-shared_test).
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
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import niobe.legion.shared.data.Stanza;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fireandfuel
 */
public class UnmarshalListTest implements XMLStreamConstants
{
    @Test
    public void testBooleanListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Boolean.class, list.get(0).getClass());
        Assert.assertEquals(true, (boolean) list.get(0));
        Assert.assertEquals(Boolean.class, list.get(1).getClass());
        Assert.assertEquals(false, (boolean) list.get(1));
        Assert.assertEquals(Boolean.class, list.get(2).getClass());
        Assert.assertEquals(true, (boolean) list.get(2));
    }

    @Test
    public void testByteListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Byte.class, list.get(0).getClass());
        Assert.assertEquals(1, (byte) list.get(0));
        Assert.assertEquals(Byte.class, list.get(1).getClass());
        Assert.assertEquals(2, (byte) list.get(1));
        Assert.assertEquals(Byte.class, list.get(2).getClass());
        Assert.assertEquals(3, (byte) list.get(2));
    }

    @Test
    public void testShortListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Short.class, list.get(0).getClass());
        Assert.assertEquals(1, (short) list.get(0));
        Assert.assertEquals(Short.class, list.get(1).getClass());
        Assert.assertEquals(2, (short) list.get(1));
        Assert.assertEquals(Short.class, list.get(2).getClass());
        Assert.assertEquals(3, (short) list.get(2));
    }

    @Test
    public void testIntegerListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Integer.class, list.get(0).getClass());
        Assert.assertEquals(1, (int) list.get(0));
        Assert.assertEquals(Integer.class, list.get(1).getClass());
        Assert.assertEquals(2, (int) list.get(1));
        Assert.assertEquals(Integer.class, list.get(2).getClass());
        Assert.assertEquals(3, (int) list.get(2));
    }

    @Test
    public void testLongListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Long.class, list.get(0).getClass());
        Assert.assertEquals(1L, (long) list.get(0));
        Assert.assertEquals(Long.class, list.get(1).getClass());
        Assert.assertEquals(2L, (long) list.get(1));
        Assert.assertEquals(Long.class, list.get(2).getClass());
        Assert.assertEquals(3L, (long) list.get(2));
    }

    @Test
    public void testFloatListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Float.class, list.get(0).getClass());
        Assert.assertEquals(0.5F, (float) list.get(0), 0F);
        Assert.assertEquals(Float.class, list.get(1).getClass());
        Assert.assertEquals(1.2F, (float) list.get(1), 0F);
        Assert.assertEquals(Float.class, list.get(2).getClass());
        Assert.assertEquals(3.14F, (float) list.get(2), 0F);
    }

    @Test
    public void testDoubleListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Double.class, list.get(0).getClass());
        Assert.assertEquals(0.5D, (double) list.get(0), 0D);
        Assert.assertEquals(Double.class, list.get(1).getClass());
        Assert.assertEquals(1.2D, (double) list.get(1), 0D);
        Assert.assertEquals(Double.class, list.get(2).getClass());
        Assert.assertEquals(3.14D, (double) list.get(2), 0D);
    }

    @Test
    public void testCharacterListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Character.class, list.get(0).getClass());
        Assert.assertEquals('1', (char) list.get(0));
        Assert.assertEquals(Character.class, list.get(1).getClass());
        Assert.assertEquals('2', (char) list.get(1));
        Assert.assertEquals(Character.class, list.get(2).getClass());
        Assert.assertEquals('3', (char) list.get(2));
    }

    @Test
    public void testStringListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(String.class, list.get(0).getClass());
        Assert.assertEquals("1", (String) list.get(0));
        Assert.assertEquals(String.class, list.get(1).getClass());
        Assert.assertEquals("2", (String) list.get(1));
        Assert.assertEquals(String.class, list.get(2).getClass());
        Assert.assertEquals("3", (String) list.get(2));
    }

    @Test
    public void testBigIntegerListUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
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
        Assert.assertEquals(ArrayList.class, objects.get(0).getClass());
        ArrayList list = (ArrayList) objects.get(0);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(BigInteger.class, list.get(0).getClass());
        Assert.assertEquals(new BigInteger("1"), (BigInteger) list.get(0));
        Assert.assertEquals(BigInteger.class, list.get(1).getClass());
        Assert.assertEquals(new BigInteger("2"), (BigInteger) list.get(1));
        Assert.assertEquals(BigInteger.class, list.get(2).getClass());
        Assert.assertEquals(new BigInteger("3"), (BigInteger) list.get(2));
    }
}
