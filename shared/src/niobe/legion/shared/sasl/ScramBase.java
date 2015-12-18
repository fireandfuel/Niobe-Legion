/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ScramBase.java) is part of Niobe Legion (module niobe-legion-shared).
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

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author Christian Schudt, fireandfuel
 */
public class ScramBase
{
    static
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final byte[] INT1 = new byte[]{0, 0, 0, 1};

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

    protected ScramBase(String hashAlgorithm, Map<String, ?> props, CallbackHandler callbackHandler) throws
                                                                                                     SaslException
    {
        this.mechanism = "SCRAM-";

        switch(hashAlgorithm)
        {
            // MD5
            case "MD5":
                this.hmacAlgorithm = "HMac-MD5";
                break;
            // SHA-1
            case "SHA-1":
                this.hmacAlgorithm = "HMac-SHA1";
                break;
            // SHA-2 algorithms:
            case "SHA-224":
                this.hmacAlgorithm = "HMac-SHA224";
                break;
            case "SHA-256":
                this.hmacAlgorithm = "HMac-SHA256";
                break;
            case "SHA-384":
                this.hmacAlgorithm = "HMac-SHA384";
                break;
            case "SHA-512":
                this.hmacAlgorithm = "HMac-SHA512";
                break;
            // SHA-3 algorithms (alias Keccak):
            case "KECCAK-224":
                this.hmacAlgorithm = "HMac-KECCAK224";
                break;
            case "KECCAK-256":
                this.hmacAlgorithm = "HMac-KECCAK256";
                break;
            case "KECCAK-288":
                this.hmacAlgorithm = "HMac-KECCAK288";
                break;
            case "KECCAK-384":
                this.hmacAlgorithm = "HMac-KECCAK384";
                break;
            case "KECCAK-512":
                this.hmacAlgorithm = "HMac-KECCAK512";
                break;
            // RipeMD algorithms
            case "RipeMD128":
                this.hmacAlgorithm = "HMac-RipeMD128";
                break;
            case "RipeMD160":
                this.hmacAlgorithm = "HMac-RipeMD160";
                break;
            // Whirlpool
            case "Whirlpool":
                this.hmacAlgorithm = "HMac-Whirlpool";
                break;
            default:
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
     * @param a The first byte array.
     * @param b The second byte array.
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
        Random rand = SecureRandom.getInstanceStrong();
        rand.nextBytes(nonce);
        return DatatypeConverter.printBase64Binary(nonce);
    }

    /**
     * Prepares a username or password.
     *
     * @param str The string.
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
     * @param str The string.
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
     * @param username The user name.
     * @param nonce    The nonce.
     * @return The client-first-message-bare.
     */
    protected static String createClientFirstMessageBare(String username, String nonce)
    {
        return "n=" + username + ",r=" + nonce;
    }

    /**
     * Computes the client signature.
     *
     * @param clientKey   The client key.
     * @param authMessage The auth message.
     * @return The client signature.
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    protected byte[] computeClientSignature(byte[] clientKey, String authMessage) throws InvalidKeyException,
                                                                                         NoSuchAlgorithmException,
                                                                                         NoSuchProviderException
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
        return this.clientFirstMessageBare + "," + this.serverFirstMessage + "," + clientFinalMessageWithoutProof;
    }

    /**
     * Computes the salted password.
     *
     * @param password       The password.
     * @param salt           The salt.
     * @param iterationCount The iteration count.
     * @return The salted password.
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    protected byte[] computeSaltedPassword(char[] password, byte[] salt, int iterationCount) throws InvalidKeyException,
                                                                                                    NoSuchAlgorithmException,
                                                                                                    NoSuchProviderException
    {
        // SaltedPassword := Hi(Normalize(password), salt, i)
        if(password != null)
        {
            return this.hi(ScramBase.prepare(new String(password)).getBytes(), salt, iterationCount);
        }
        throw new InvalidKeyException("User unknown");
    }

    /**
     * Computes the client key.
     *
     * @param saltedPassword The salted password.
     * @return The client key.
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    protected byte[] computeClientKey(byte[] saltedPassword) throws InvalidKeyException, NoSuchAlgorithmException,
                                                                    NoSuchProviderException
    {
        // ClientKey := HMAC(SaltedPassword, "Client Key")
        return this.hmac(saltedPassword, ScramBase.CLIENT_KEY);
    }

    /**
     * Computes the stored key.
     *
     * @param clientKey The client key.
     * @return The stored key.
     * @throws NoSuchAlgorithmException
     */
    protected byte[] computeStoredKey(byte[] clientKey) throws NoSuchAlgorithmException, NoSuchProviderException
    {
        // StoredKey := H(ClientKey)
        return this.h(clientKey);
    }

    /**
     * Computes the server key.
     *
     * @param saltedPassword The salted password.
     * @return The server key.
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    protected byte[] computeServerKey(byte[] saltedPassword) throws InvalidKeyException, NoSuchAlgorithmException,
                                                                    NoSuchProviderException
    {
        // ServerKey := HMAC(SalterPassword, "Server Key")
        return this.hmac(saltedPassword, ScramBase.SERVER_KEY);
    }

    /**
     * Computes the server signature.
     *
     * @param serverKey   The server key.
     * @param authMessage The authMessage.
     * @return The server signature.
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    protected byte[] computeServerSignature(byte[] serverKey, String authMessage) throws InvalidKeyException,
                                                                                         NoSuchAlgorithmException,
                                                                                         NoSuchProviderException
    {
        return this.hmac(serverKey, authMessage.getBytes());
    }

    /**
     * Apply the cryptographic hash function to the octet string "str",
     * producing an octet string as a result. The size of the result depends on
     * the hash result size for the hash function in use.
     *
     * @param str The byte array.
     */
    protected byte[] h(byte[] str) throws NoSuchAlgorithmException, NoSuchProviderException
    {
        MessageDigest digest = MessageDigest.getInstance(this.hashAlgorithm, "BC");
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
     * @param key The key.
     * @param str The input.
     * @return The HMAC keyed hash.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    protected byte[] hmac(byte[] key, byte[] str) throws NoSuchAlgorithmException, InvalidKeyException,
                                                         NoSuchProviderException
    {
        Mac mac = Mac.getInstance(this.hmacAlgorithm, "BC");
        mac.init(new SecretKeySpec(key, this.hmacAlgorithm));
        mac.update(str);
        return mac.doFinal();
    }

    /**
     * Calculates the salted password.
     *
     * @param str  The input.
     * @param salt The salt.
     * @param i    The iteration count.
     * @return The salted password.
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.InvalidKeyException
     */
    byte[] hi(byte[] str, byte[] salt, int i) throws NoSuchAlgorithmException, InvalidKeyException,
                                                     NoSuchProviderException
    {
        Mac hmac = Mac.getInstance(this.hmacAlgorithm, "BC");
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