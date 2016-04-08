/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LegionSaslBaseFactory.java) is part of Niobe Legion (module niobe-legion-shared).
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
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared.sasl;

import java.util.Map;

/**
 * @author fireandfuel
 */
public abstract class LegionSaslBaseFactory
{
    public String[] getMechanismNames(Map<String, ?> props)
    {
        return new String[]{"SCRAM-MD5", "SCRAM-SHA-1", "SCRAM-SHA-224", "SCRAM-SHA-256", "SCRAM-SHA-384", "SCRAM-SHA-512", "SCRAM-KECCAK-224", "SCRAM-KECCAK-256", "SCRAM-KECCAK-288", "SCRAM-KECCAK-384", "SCRAM-KECCAK-512", "SCRAM-RipeMD128", "SCRAM-RipeMD160", "SCRAM-Whirlpool", "ANONYMOUS", "PLAIN"};
    }
}
