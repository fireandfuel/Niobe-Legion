/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ICommunicator.java) is part of Niobe Legion (module niobe-legion-shared).
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

package niobe.legion.shared;

import java.io.IOException;
import java.net.InetAddress;
import niobe.legion.shared.data.Stanza;

public interface ICommunicator
{
    void consumeStartElement(Stanza currentStanza) throws IOException;

    void consumeCharacters(Stanza currentStanza) throws IOException;

    void consumeEndElement(Stanza currentStanza) throws IOException;

    void write(Stanza stanza) throws IOException;

    String getNamespace();

    String getNamespaceURI();

    default InetAddress getAddress()
    {
        return null;
    }

    default int getPort()
    {
        return -1;
    }
}
