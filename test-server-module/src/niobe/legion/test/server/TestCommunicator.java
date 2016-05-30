/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (TestCommunicator.java) is part of Niobe Legion (module niobe-legion-test-server-module).
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

package niobe.legion.test.server;

import java.io.IOException;
import java.net.SocketException;
import niobe.legion.shared.communicator.ICommunicator;
import niobe.legion.shared.data.Stanza;

public class TestCommunicator implements ICommunicator
{
    public static final String NAMESPACE = "niobe.module.test";
    public static final String NAMESPACE_URI = "ashnurazg.de/niobe/legion-module-test";

    private final ICommunicator parent;

    public TestCommunicator(ICommunicator parent)
    {
        this.parent = parent;
    }

    @Override
    public void consumeStartElement(Stanza currentStanza) throws IOException
    {

    }

    @Override
    public void consumeCharacters(Stanza currentStanza) throws IOException
    {

    }

    @Override
    public void consumeEndElement(Stanza currentStanza) throws IOException
    {

    }

    @Override
    public void write(Stanza stanza) throws IOException, SocketException
    {
        this.parent.write(stanza);
    }

    @Override
    public String getNamespace()
    {
        return NAMESPACE;
    }

    @Override
    public String getNamespaceURI()
    {
        return NAMESPACE_URI;
    }
}
