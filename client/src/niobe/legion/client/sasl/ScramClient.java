/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ScramClient.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.sasl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.xml.bind.DatatypeConverter;
import niobe.legion.shared.sasl.ScramBase;

/**
 * The implementation of the SCRAM SASL mechanism.
 *
 * @author Christian Schudt https://bitbucket.org/sco0ter/babbler
 * @author fireandfuel
 * @see <a href="http://tools.ietf.org/search/rfc5802">Salted Challenge Response
 * Authentication Mechanism (SCRAM)</a>
 */
final class ScramClient extends ScramBase implements SaslClient
{

    private static final String GS2_CBIND_FLAG = "n";

    private final String gs2Header;
    private String authorizationId;
    private char[] passwd;
    private String username;
    private String cnonce;
    private byte[] serverSignature;
    private Integer iterationCount;
    private byte[] salt;

    public ScramClient(String hashAlgorithm, String authorizationId, String protocol, String serverName,
                       Map<String, ?> props, CallbackHandler callbackHandler) throws SaslException
    {
        super(hashAlgorithm, props, callbackHandler);

        // authzID can only be encoded in UTF8 - RFC 2222
        if(authorizationId != null)
        {
            this.authorizationId = authorizationId;
            try
            {
                authorizationId.getBytes("UTF8");
            } catch(UnsupportedEncodingException e)
            {
                throw new SaslException("SCRAM: Error encoding authzid value into UTF-8", e);
            }
        }
        this.gs2Header = ScramClient.GS2_CBIND_FLAG + "," + (authorizationId != null ? "a=" + authorizationId : "") + ",";
    }

    /**
     * The characters ',' or '=' in usernames are sent as '=2C' and '=3D'
     * respectively.
     *
     * @param username The username.
     * @return The replaced username.
     */
    static String replaceUsername(String username)
    {
        if(username != null)
        {
            return username.replace("=", "=3D").replace(",", "=2C");
        }
        return null;
    }

    @Override
    public boolean hasInitialResponse()
    {
        // Nothing in SCRAM prevents either sending
        // the client-first message with the SASL authentication request defined
        // by an application protocol ("initial client response")
        return true;
    }

    @Override
    public byte[] evaluateChallenge(byte[] challenge) throws SaslException
    {

        // Initial response
        if(challenge.length == 0)
        {
            NameCallback ncb = this.authorizationId == null ? new NameCallback("SCRAM username: ") : new NameCallback(
                    "SCRAM username: ",
                    this.authorizationId);
            PasswordCallback pcb = new PasswordCallback("SCRAM-SHA-1 password: ", false);

            try
            {
                this.callbackHandler.handle(new Callback[]{ncb, pcb});
                this.passwd = pcb.getPassword();
                pcb.clearPassword();
                this.username = ncb.getName();
                if(this.passwd == null || this.username == null)
                {
                    throw new SaslException("SCRAM: Username and password must not be null.");
                }

                // Before sending the username to the server, the client SHOULD
                // prepare the username using the "SASLprep" profile [RFC4013]
                // of
                // the "stringprep" algorithm [RFC3454] treating it as a query
                // string (i.e., unassigned Unicode code points are allowed).
                this.username = ScramBase.prepare(this.username);

                // If the preparation of the username fails or results in an
                // empty
                // string, the client SHOULD abort the authentication exchange.
                if("".equals(this.username))
                {
                    throw new SaslException("SCRAM: Username must not be empty.");
                }
                this.username = ScramClient.replaceUsername(this.username);
                this.cnonce = ScramBase.generateNonce();
                this.clientFirstMessageBare = ScramBase.createClientFirstMessageBare(this.username, this.cnonce);
                // First, the client sends the "client-first-message"
                String clientFirstMessage = this.gs2Header + this.clientFirstMessageBare;
                return clientFirstMessage.getBytes(StandardCharsets.UTF_8);
            } catch(IOException e)
            {
                throw new SaslException("SCRAM: Error acquiring user name or password.", e);
            } catch(UnsupportedCallbackException e)
            {
                throw new SaslException("SCRAM: Cannot perform callback to acquire username or password", e);
            } catch(NoSuchAlgorithmException e)
            {
                throw new SaslException("SCRAM: Failed to generate nonce.", e);
            }
        } else
        {
            try
            {
                // The server sends the salt and the iteration count to the client,
                // which then computes
                // the following values and sends a ClientProof to the server

                String serverMessage = new String(challenge);
                Map<Character, String> attributes = ScramBase.getAttributes(serverMessage);

                // e: This attribute specifies an error that occurred during
                // authentication exchange.  It is sent by the server in its final
                // message and can help diagnose the reason for the authentication
                // exchange failure.
                String error = attributes.get('e');
                if(error != null)
                {
                    throw new SaslException(error);
                }

                // v: This attribute specifies a base64-encoded ServerSignature.  It
                // is sent by the server in its final message, and is used by the
                // client to verify that the server has access to the user's
                // authentication information.
                String verifier = attributes.get('v');
                if(verifier != null)
                {
                    // The client authenficates the server by computing the
                    // ServerSignature and
                    // comparing it with the value sent by server.
                    if(!Arrays.equals(this.serverSignature, DatatypeConverter.parseBase64Binary(verifier)))
                    {
                        throw new SaslException("SCRAM: Verification failed");
                    }

                    this.complete = true;
                    return null;
                }
                this.serverFirstMessage = serverMessage;

                this.nonce = attributes.get('r');
                this.clientFirstMessageBare = ScramBase.createClientFirstMessageBare(this.username, this.nonce);
                String saltBase64 = attributes.get('s');
                try
                {
                    this.iterationCount = Integer.parseInt(attributes.get('i'));
                } catch(NumberFormatException e)
                {
                    throw new SaslException("iterationCount could not be parsed.");
                }

                if(this.nonce == null)
                {
                    throw new SaslException("SCRAM: nonce was null in the server response.");
                }
                if(saltBase64 == null)
                {
                    throw new SaslException("SCRAM: salt was null in the server response.");
                }
                if(this.iterationCount == null)
                {
                    throw new SaslException("SCRAM: iterationCount was null in the server response.");
                }

                this.salt = DatatypeConverter.parseBase64Binary(saltBase64);

                try
                {
                    this.channelBinding = DatatypeConverter
                            .printBase64Binary(this.gs2Header.getBytes(StandardCharsets.UTF_8));
                    byte[] saltedPassword = this.computeSaltedPassword(this.passwd, this.salt, this.iterationCount);
                    String authMessage = this.computeAuthMessage();
                    byte[] clientKey = this.computeClientKey(saltedPassword);
                    byte[] clientSignature = this.computeClientSignature(clientKey, this.computeAuthMessage());
                    // ClientProof     := ClientKey XOR ClientSignature
                    byte[] clientProof = ScramBase.xor(clientKey, clientSignature);
                    byte[] serverKey = this.computeServerKey(saltedPassword);
                    this.serverSignature = this.hmac(serverKey, authMessage.getBytes(StandardCharsets.UTF_8));
                    String clientFinalMessageWithoutProof = "c=" + channelBinding + ",r=" + nonce;
                    // The client then responds by sending a "client-final-message" with the
                    // same nonce and a ClientProof computed using the selected hash
                    // function as explained earlier.
                    String clientFinalMessage = clientFinalMessageWithoutProof + ",p=" + DatatypeConverter
                            .printBase64Binary(clientProof);
                    return clientFinalMessage.getBytes(StandardCharsets.UTF_8);
                } catch(GeneralSecurityException e)
                {
                    throw new SaslException(e.getMessage(), e);
                }
            } catch(SaslException e)
            {
                complete = true;
                throw e;
            }
        }
    }
}