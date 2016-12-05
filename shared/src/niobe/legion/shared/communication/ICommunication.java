/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ICommunication.java) is part of Niobe Legion (module niobe-legion-shared_main).
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

package niobe.legion.shared.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamConstants;
import niobe.legion.shared.data.Stanza;

public interface ICommunication extends XMLStreamConstants
{
    void initInputReader(InputStream in) throws CommunicationException;

    /**
     * write a stanza instantly via communication implementation
     *
     * @param stanza the stanza to write
     */
    void write(OutputStream out, Stanza stanza) throws IOException;

    /**
     * should be blocking
     *
     * @return communication implementation is ready to offer another stanza
     */
    boolean hasNextStanza() throws CommunicationException;

    /**
     * get the next stanza
     *
     * @return the stanza from the communication implementation
     */
    Stanza getNextStanza() throws CommunicationException;

    /**
     * set the current stanza
     */
    void setCurrentStanza(Stanza currentStanza);
}
