/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MarshalSimpleTest.java) is part of Niobe Legion (module niobe-legion-shared).
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

import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import niobe.legion.shared.data.Stanza;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fireandfuel
 */
public class MarshalSimpleTest implements XMLStreamConstants
{
    @Test
    public void testBooleanMarshal()
    {
        boolean value = true;
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals("true", stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.Boolean", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }

    @Test
    public void testByteMarshal()
    {
        byte value = (byte) (Math.random() * 255d);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals(Byte.toString(value), stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.Byte", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }

    @Test
    public void testShortMarshal()
    {
        short value = (short) (Math.random() * 65535d);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals(Short.toString(value), stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.Short", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }

    @Test
    public void testIntegerMarshal()
    {
        int value = (int) (Math.random() * 1000d);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals(Integer.toString(value), stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.Integer", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }

    @Test
    public void testLongMarshal()
    {
        long value = (long) (Math.random() * 1000d);
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals(Long.toString(value), stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.Long", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }

    @Test
    public void testFloatMarshal()
    {
        float value = (float) Math.random();
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals(Float.toString(value), stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.Float", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }

    @Test
    public void testDoubleMarshal()
    {
        double value = Math.random();
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals(Double.toString(value), stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.Double", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }

    @Test
    public void testCharacterMarshal()
    {
        char value = 'c';
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals(Character.toString(value), stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.Character", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }

    @Test
    public void testStringMarshal()
    {
        String value = "SomeTestString";
        List<Stanza> stanzaList = StanzaMarshaller.marshal(value, 12);
        Assert.assertEquals(1, stanzaList.size());
        Stanza stanza = stanzaList.get(0);
        Assert.assertEquals("legion:dataset", stanza.getName());
        Assert.assertEquals(value, stanza.getValue());
        Assert.assertEquals(2, stanza.getAttributeKeys().size());
        Assert.assertEquals("12", stanza.getAttribute("sequenceId"));
        Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
        Assert.assertEquals(START_ELEMENT, stanza.getEventType());
        Assert.assertTrue(stanza.isEmptyElement());
    }
}