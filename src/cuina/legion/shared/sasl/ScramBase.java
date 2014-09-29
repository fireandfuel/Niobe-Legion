/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cuina.legion.shared.sasl;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.xml.bind.DatatypeConverter;

/**
 * @author Christian Schudt
 */
public class ScramBase
{

	private static final byte[] INT1 = new byte[] { 0, 0, 0, 1 };

	private static final byte[] CLIENT_KEY = "Client Key".getBytes();

	private static final byte[] SERVER_KEY = "Server Key".getBytes();

	protected final CallbackHandler callbackHandler;

	private final String hmacAlgorithm;

	private final String hashAlgorithm;

	protected boolean isComplete;

	protected String clientFirstMessageBare;

	protected String serverFirstMessage;

	protected String clientFinalMessageWithoutProof;

	protected String nonce;

	protected String gs2Header;

	protected String channelBinding;

	private String mechanism;

	protected ScramBase(String hashAlgorithm, Map<String, ?> props, CallbackHandler callbackHandler)
			throws SaslException
	{
		this.mechanism = "SCRAM-";
		hashAlgorithm = hashAlgorithm.toUpperCase();

		if("SHA-1".equals(hashAlgorithm))
		{
			this.hmacAlgorithm = "HmacSHA1";
		} else
		{
			throw new UnsupportedOperationException("Hash algorithm not supported.");
		}
		this.mechanism += hashAlgorithm;
		this.hashAlgorithm = hashAlgorithm;
		this.callbackHandler = callbackHandler;
	}

	/**
	 * Apply the exclusive-or operation to combine the octet string on the left
	 * of this operator with the octet string on the right of this operator. The
	 * length of the output and each of the two inputs will be the same for this
	 * use.
	 *
	 * @param a
	 *            The first byte array.
	 * @param b
	 *            The second byte array.
	 * @return The XOR combined byte array.
	 */
	protected static byte[] xor(byte[] a, byte[] b)
	{
		byte[] c = new byte[a.length];
		for(int i = 0; i < a.length; i++)
		{
			c[i] = (byte) (a[i] ^ b[i]);
		}
		return c;
	}

	/**
	 * Generates a nonce.
	 *
	 * @return The nonce.
	 * @throws NoSuchAlgorithmException
	 */
	protected static String generateNonce() throws NoSuchAlgorithmException
	{
		byte[] nonce = new byte[16];
		Random rand = SecureRandom.getInstance("SHA1PRNG");
		rand.nextBytes(nonce);
		return DatatypeConverter.printBase64Binary(nonce);
	}

	/**
	 * Prepares a username or password.
	 *
	 * @param str
	 *            The string.
	 * @return The normalized string.
	 */
	protected static String prepare(String str)
	{
		// http://tools.ietf.org/search/rfc4013
		// This profile specifies using Unicode normalization form KC, as
		// described in Section 4 of [StringPrep].
		return Normalizer.normalize(str, Normalizer.Form.NFKC);
	}

	/**
	 * Gets the attributes from a SCRAM string.
	 *
	 * @param str
	 *            The string.
	 * @return The attributes.
	 */
	protected static Map<Character, String> getAttributes(String str)
	{
		Map<Character, String> map = new HashMap<>();
		String[] parts = str.split(",");
		for(String part : parts)
		{
			if(part.length() > 1)
			{
				map.put(part.charAt(0), part.substring(2));
			}
		}
		return map;
	}

	/**
	 * Creates the client-first-message-bare.
	 *
	 * @param username
	 *            The user name.
	 * @param nonce
	 *            The nonce.
	 * @return The client-first-message-bare.
	 */
	protected static String createClientFirstMessageBare(String username, String nonce)
	{
		return "n=" + username + ",r=" + nonce;
	}

	/**
	 * Computes the client signature.
	 *
	 * @param clientKey
	 *            The client key.
	 * @param authMessage
	 *            The auth message.
	 * @return The client signature.
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	protected byte[] computeClientSignature(byte[] clientKey, String authMessage)
			throws InvalidKeyException, NoSuchAlgorithmException
	{
		byte[] storedKey = this.computeStoredKey(clientKey);
		// ClientSignature := HMAC(StoredKey, AuthMessage)
		return this.hmac(storedKey, authMessage.getBytes());
	}

	/**
	 * Computes the auth message.
	 *
	 * @return The auth message.
	 */
	protected String computeAuthMessage()
	{
		// AuthMessage := client-first-message-bare + "," +
		// server-first-message + "," +
		// client-final-message-without-proof
		String clientFinalMessageWithoutProof = "c=" + this.channelBinding + ",r=" + this.nonce;
		return this.clientFirstMessageBare + "," + this.serverFirstMessage + ","
				+ clientFinalMessageWithoutProof;
	}

	/**
	 * Computes the salted password.
	 *
	 * @param password
	 *            The password.
	 * @param salt
	 *            The salt.
	 * @param iterationCount
	 *            The iteration count.
	 * @return The salted password.
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	protected byte[] computeSaltedPassword(char[] password, byte[] salt, int iterationCount)
			throws InvalidKeyException, NoSuchAlgorithmException
	{
		// SaltedPassword := Hi(Normalize(password), salt, i)
		if(password != null)
		{
			return this
					.hi(ScramBase.prepare(new String(password)).getBytes(), salt, iterationCount);
		}
		throw new InvalidKeyException("User unknown");
	}

	/**
	 * Computes the client key.
	 *
	 * @param saltedPassword
	 *            The salted password.
	 * @return The client key.
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	protected byte[] computeClientKey(byte[] saltedPassword) throws InvalidKeyException,
			NoSuchAlgorithmException
	{
		// ClientKey := HMAC(SaltedPassword, "Client Key")
		return this.hmac(saltedPassword, ScramBase.CLIENT_KEY);
	}

	/**
	 * Computes the stored key.
	 *
	 * @param clientKey
	 *            The client key.
	 * @return The stored key.
	 * @throws NoSuchAlgorithmException
	 */
	protected byte[] computeStoredKey(byte[] clientKey) throws NoSuchAlgorithmException
	{
		// StoredKey := H(ClientKey)
		return this.h(clientKey);
	}

	/**
	 * Computes the server key.
	 *
	 * @param saltedPassword
	 *            The salted password.
	 * @return The server key.
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	protected byte[] computeServerKey(byte[] saltedPassword) throws InvalidKeyException,
			NoSuchAlgorithmException
	{
		// ServerKey := HMAC(SalterPassword, "Server Key")
		return this.hmac(saltedPassword, ScramBase.SERVER_KEY);
	}

	/**
	 * Computes the server signature.
	 *
	 * @param serverKey
	 *            The server key.
	 * @param authMessage
	 *            The authMessage.
	 * @return The server signature.
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	protected byte[] computeServerSignature(byte[] serverKey, String authMessage)
			throws InvalidKeyException, NoSuchAlgorithmException
	{
		return this.hmac(serverKey, authMessage.getBytes());
	}

	/**
	 * Apply the cryptographic hash function to the octet string "str",
	 * producing an octet string as a result. The size of the result depends on
	 * the hash result size for the hash function in use.
	 *
	 * @param str
	 *            The byte array.
	 */
	protected byte[] h(byte[] str) throws NoSuchAlgorithmException
	{
		MessageDigest digest = MessageDigest.getInstance(this.hashAlgorithm);
		digest.update(str);
		return digest.digest();
	}

	/**
	 * Apply the HMAC keyed hash algorithm (defined in [RFC2104]) using the
	 * octet string represented by "key" as the key and the octet string "str"
	 * as the input string. The size of the result is the hash result size for
	 * the hash function in use. For example, it is 20 octets for SHA-1 (see
	 * [RFC3174]).
	 *
	 * @param key
	 *            The key.
	 * @param str
	 *            The input.
	 * @return The HMAC keyed hash.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	protected byte[] hmac(byte[] key, byte[] str) throws NoSuchAlgorithmException,
			InvalidKeyException
	{
		Mac mac = Mac.getInstance(this.hmacAlgorithm);
		mac.init(new SecretKeySpec(key, this.hmacAlgorithm));
		mac.update(str);
		return mac.doFinal();
	}

	/**
	 * Calculates the salted password.
	 *
	 * @param str
	 *            The input.
	 * @param salt
	 *            The salt.
	 * @param i
	 *            The iteration count.
	 * @return The salted password.
	 * @throws java.security.NoSuchAlgorithmException
	 *
	 * @throws java.security.InvalidKeyException
	 *
	 */
	byte[] hi(byte[] str, byte[] salt, int i) throws NoSuchAlgorithmException, InvalidKeyException
	{
		Mac hmac = Mac.getInstance(this.hmacAlgorithm);
		hmac.init(new SecretKeySpec(str, this.hmacAlgorithm));

		// U1 := HMAC(str, salt + INT(1))
		// U2 := HMAC(str, U1)
		// ...
		// Ui-1 := HMAC(str, Ui-2)
		// Ui := HMAC(str, Ui-1)
		//
		// Hi := U1 XOR U2 XOR ... XOR Ui
		hmac.update(salt);
		hmac.update(ScramBase.INT1);

		byte[] uPrev = hmac.doFinal();
		byte[] result = uPrev;

		for(int c = 1; c < i; c++)
		{
			hmac.update(uPrev);
			uPrev = hmac.doFinal();
			result = ScramBase.xor(result, uPrev);
		}

		return result;
	}

	/**
	 * Gets the mechanism name, i.e "SCRAM-" + the hash algorithm name.
	 *
	 * @return The mechanism name.
	 */
	public String getMechanismName()
	{
		return this.mechanism;
	}
}