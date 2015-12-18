/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (Utils.java) is part of Niobe Legion (module niobe-legion-shared).
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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.stream.IntStream;

public class Utils
{
    public final static String PRINTABLE_CHARS = "01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+-*!?&=/()";

    public final static SecureRandom random = new SecureRandom();

    public static String getRandomHexString(int length)
    {
        String result = new BigInteger(130, Utils.random).toString();
        return result.substring(0, Math.min(result.length(), length));
    }

    public static String getRandomString(int length)
    {
        byte[] chars = new BigInteger(130, Utils.random).toByteArray();
        int[] values = new int[chars.length];
        for(int i = 0; i < chars.length; i++)
        {
            values[i] = chars[i] & 0xFF;
        }

        String result = IntStream.of(values)
                .mapToObj(value -> Character.toString(PRINTABLE_CHARS.charAt(value % PRINTABLE_CHARS.length())))
                .reduce(String::concat).get();

        return result.substring(0, Math.min(result.length(), length));
    }
}
