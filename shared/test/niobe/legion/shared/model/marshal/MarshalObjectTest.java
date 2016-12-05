/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (MarshalObjectTest.java) is part of Niobe Legion (module niobe-legion-shared_test).
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

import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import niobe.legion.shared.data.Stanza;
import niobe.legion.shared.model.GroupEntity;
import niobe.legion.shared.model.GroupRightEntity;
import niobe.legion.shared.model.UserEntity;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author fireandfuel
 */
public class MarshalObjectTest implements XMLStreamConstants
{
    @Test
    public void testUserEntityMarshal()
    {
        GroupRightEntity groupRight = new GroupRightEntity();
        groupRight.setName("testRight");
        groupRight.setActive(true);
        List<GroupRightEntity> groupRights = Arrays.asList(groupRight);

        GroupEntity group = new GroupEntity();
        group.setRights(groupRights);
        group.setName("testGroup");
        group.setActive(true);

        UserEntity user = new UserEntity();
        user.setName("testUser");
        user.setPassword("testPassword");
        user.setGroup(group);

        List<Stanza> stanzaList = StanzaMarshaller.marshal(user, 12);
        Assert.assertEquals(24, stanzaList.size());

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
                    Assert.assertEquals(UserEntity.class.getCanonicalName(), stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 1:
                case 7:
                case 12:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Integer", stanza.getAttribute("class"));
                    Assert.assertEquals("id", stanza.getAttribute("name"));
                    Assert.assertEquals("0", stanza.getValue());
                    Assert.assertEquals(CHARACTERS, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 2:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals("name", stanza.getAttribute("name"));
                    Assert.assertEquals("testUser", stanza.getValue());
                    Assert.assertEquals(CHARACTERS, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 3:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals("password", stanza.getAttribute("name"));
                    Assert.assertEquals("testPassword", stanza.getValue());
                    Assert.assertEquals(CHARACTERS, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 4:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals(GroupEntity.class.getCanonicalName(), stanza.getAttribute("class"));
                    Assert.assertEquals("group", stanza.getAttribute("name"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 5:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 10:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertEquals("0", stanza.getAttribute("index"));
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 6:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(GroupEntity.class.getCanonicalName(), stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 8:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals("name", stanza.getAttribute("name"));
                    Assert.assertEquals("testGroup", stanza.getValue());
                    Assert.assertEquals(CHARACTERS, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 9:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.util.ArrayList", stanza.getAttribute("class"));
                    Assert.assertEquals("rights", stanza.getAttribute("name"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 11:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(2, stanza.getAttributeKeys().size());
                    Assert.assertEquals(GroupRightEntity.class.getCanonicalName(), stanza.getAttribute("class"));
                    Assert.assertEquals(START_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 13:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals("name", stanza.getAttribute("name"));
                    Assert.assertEquals("testRight", stanza.getValue());
                    Assert.assertEquals(CHARACTERS, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 14:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.String", stanza.getAttribute("class"));
                    Assert.assertEquals("displayName", stanza.getAttribute("name"));
                    Assert.assertEquals("testRight", stanza.getValue());
                    Assert.assertEquals(CHARACTERS, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 15:
                case 19:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(3, stanza.getAttributeKeys().size());
                    Assert.assertEquals("java.lang.Boolean", stanza.getAttribute("class"));
                    Assert.assertEquals("active", stanza.getAttribute("name"));
                    Assert.assertEquals("true", stanza.getValue());
                    Assert.assertEquals(CHARACTERS, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 16:
                case 20:
                case 23:
                    Assert.assertEquals("legion:dataset", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 17:
                case 21:
                    Assert.assertEquals("legion:entry", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
                case 18:
                case 22:
                    Assert.assertEquals("legion:column", stanza.getName());
                    Assert.assertEquals(1, stanza.getAttributeKeys().size());
                    Assert.assertEquals(END_ELEMENT, stanza.getEventType());
                    Assert.assertFalse(stanza.isEmptyElement());
                    break;
            }
        }
    }
}
