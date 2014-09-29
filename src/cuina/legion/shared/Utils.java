package cuina.legion.shared;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Utils
{
	public final static SecureRandom random = new SecureRandom();

	public final static String getRandomString(int length)
	{
		return new BigInteger(130, Utils.random).toString(length);
	}
}
