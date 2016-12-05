/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LegionSaslServerProvider.java) is part of Niobe Legion (module niobe-legion-server_main).
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

package niobe.legion.server.communicator.sasl;

import java.security.Provider;

public class LegionSaslServerProvider extends Provider
{
    private final static long serialVersionUID = 1L;

    public LegionSaslServerProvider()
    {
        super("Legion", 1.0, "supplies SASL authenfication mechanisms for Legion server and client");

        this.put("SaslServerFactory.SCRAM-MD5", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-SHA-1", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-SHA-224", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-SHA-256", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-SHA-384", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-SHA-512", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-KECCAK-224", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-KECCAK-256", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-KECCAK-288", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-KECCAK-384", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-KECCAK-512", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-RipeMD128", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-RipeMD160", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.SCRAM-Whirlpool", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.ANONYMOUS", LegionSaslServerFactory.class.getName());
        this.put("SaslServerFactory.PLAIN", LegionSaslServerFactory.class.getName());
    }

}
