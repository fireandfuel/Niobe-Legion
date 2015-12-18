/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (AnonymousSaslClient.java) is part of Niobe Legion (module niobe-legion-shared).
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

package niobe.legion.shared.sasl;

import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

/**
 * The implementation of the "ANONYMOUS" SASL mechanism.
 *
 * @author Christian Schudt
 */
final class AnonymousSaslClient implements SaslClient
{
    @Override
    public String getMechanismName()
    {
        return "ANONYMOUS";
    }

    @Override
    public boolean hasInitialResponse()
    {
        return true;
    }

    @Override
    public byte[] evaluateChallenge(byte[] challenge) throws SaslException
    {
        return new byte[0];
    }

    @Override
    public boolean isComplete()
    {
        return true;
    }

    @Override
    public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException
    {
        return new byte[0];
    }

    @Override
    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException
    {
        return new byte[0];
    }

    @Override
    public Object getNegotiatedProperty(String propName)
    {
        return null;
    }

    @Override
    public void dispose() throws SaslException
    {
    }
}