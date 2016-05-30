/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (UnmarshalObjectTest.java) is part of Niobe Legion (module niobe-legion-shared).
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

import java.util.ArrayList;
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
public class UnmarshalObjectTest implements XMLStreamConstants
{
    @Test
    public void testUserEntityUnmarshal()
    {
        List<Stanza> stanzas = new ArrayList<Stanza>();
        Stanza stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", UserEntity.class.getCanonicalName());
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.Integer");
        stanza.putAttribute("name", "id");
        stanza.setValue("0");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.String");
        stanza.putAttribute("name", "name");
        stanza.setValue("testUser");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.String");
        stanza.putAttribute("name", "password");
        stanza.setValue("testPassword");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", GroupEntity.class.getCanonicalName());
        stanza.putAttribute("name", "group");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", GroupEntity.class.getCanonicalName());
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.Integer");
        stanza.putAttribute("name", "id");
        stanza.setValue("0");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.String");
        stanza.putAttribute("name", "name");
        stanza.setValue("testGroup");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", "java.util.ArrayList");
        stanza.putAttribute("name", "rights");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("index", "0");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(START_ELEMENT);
        stanza.putAttribute("class", GroupRightEntity.class.getCanonicalName());
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.Integer");
        stanza.putAttribute("name", "id");
        stanza.setValue("0");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.String");
        stanza.putAttribute("name", "name");
        stanza.setValue("testRight");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.String");
        stanza.putAttribute("name", "displayName");
        stanza.setValue("testRight");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.Boolean");
        stanza.putAttribute("name", "active");
        stanza.setValue("true");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
        stanza.setEventType(CHARACTERS);
        stanza.putAttribute("class", "java.lang.Boolean");
        stanza.putAttribute("name", "active");
        stanza.setValue("true");
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:dataset");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:entry");
        stanza.setEventType(END_ELEMENT);
        stanza.setSequenceId(12L);
        stanzas.add(stanza);

        stanza = new Stanza();
        stanza.setName("legion:column");
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
        Assert.assertEquals(UserEntity.class, objects.get(0).getClass());
        UserEntity userEntity = (UserEntity) objects.get(0);
        Assert.assertEquals(0, userEntity.getId());
        Assert.assertEquals("testUser", userEntity.getName());
        Assert.assertEquals("testPassword", userEntity.getPassword());
        Assert.assertNotNull(userEntity.getGroup());
        GroupEntity groupEntity = userEntity.getGroup();
        Assert.assertEquals(0, groupEntity.getId());
        Assert.assertEquals("testGroup", groupEntity.getName());
        Assert.assertTrue(groupEntity.isActive());
        Assert.assertNotNull(groupEntity.getRights());
        Assert.assertEquals(1, groupEntity.getRights().size());
        Assert.assertEquals(GroupRightEntity.class, groupEntity.getRights().get(0).getClass());
        GroupRightEntity groupRightEntity = groupEntity.getRights().get(0);
        Assert.assertEquals(0, groupRightEntity.getId());
        Assert.assertEquals("testRight", groupRightEntity.getName());
        Assert.assertEquals("testRight", groupRightEntity.getDisplayName());
        Assert.assertTrue(groupRightEntity.isActive());
        Assert.assertNull(groupRightEntity.getChildren());
    }
}
