/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LegionSaslClientProvider.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.communicator.sasl;

import java.security.Provider;

/**
 * @author fireandfuel
 */
public class LegionSaslClientProvider extends Provider
{
    public LegionSaslClientProvider()
    {
        super("Legion", 1.0, "supplies SASL authenfication mechanisms for Legion client");

        this.put("SaslClientFactory.SCRAM-MD5", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-SHA-1", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-SHA-224", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-SHA-256", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-SHA-384", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-SHA-512", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-KECCAK-224", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-KECCAK-256", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-KECCAK-288", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-KECCAK-384", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-KECCAK-512", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-RipeMD128", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-RipeMD160", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.SCRAM-Whirlpool", LegionSaslClientFactory.class.getName());
        this.put("SaslClientFactory.ANONYMOUS", LegionSaslClientFactory.class.getName());
    }
}
