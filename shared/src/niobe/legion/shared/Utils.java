package niobe.legion.shared;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.stream.IntStream;

public class Utils
{
	public final static String PRINTABLE_CHARS =
			"01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+-*!?&=/()";

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
		for (int i = 0; i < chars.length; i++)
		{
			values[i] = chars[i] & 0xFF ;
		}

		String result = IntStream.of(values).mapToObj(value -> Character
				.toString(PRINTABLE_CHARS.charAt(value % PRINTABLE_CHARS.length()))).reduce(String::concat).get();

		return result.substring(0, Math.min(result.length(), length));
	}
}
