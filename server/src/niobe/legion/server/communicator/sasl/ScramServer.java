/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ScramServer.java) is part of Niobe Legion (module niobe-legion-server_main).
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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.xml.bind.DatatypeConverter;
import niobe.legion.shared.Utils;
import niobe.legion.shared.communicator.sasl.ScramBase;

/**
 * @author Christian Schudt https://bitbucket.org/sco0ter/babbler
 * @author fireandfuel
 */
class ScramServer extends ScramBase implements SaslServer
{

    private final static int ITERATION_COUNT = 4096;

    private final static Pattern USER_VALIDATION = Pattern.compile("=(?!2C|3D)");

    private char[] password;

    private byte[] salt;

    private String authorizationId;

    public ScramServer(String hashAlgorithm, Map<String, ?> props, CallbackHandler callbackHandler) throws SaslException
    {
        super(hashAlgorithm, props, callbackHandler);
    }

    static String validateAndGetUsername(String userAttribute) throws SaslException
    {
        if(userAttribute != null)
        {
            // If the server receives a username that
            // contains '=' not followed by either '2C' or '3D', then the
            // server MUST fail the authentication.

            Matcher matcher = ScramServer.USER_VALIDATION.matcher(userAttribute);
            if(matcher.find())
            {
                throw new SaslException("Username must not contain '=' not followed by '2C' or '3D'.");
            }
            return userAttribute.replaceAll("=3D", "=").replaceAll("=2C", ",");
        }
        return null;
    }

    @Override
    public byte[] evaluateResponse(byte[] response) throws SaslException
    {

        String clientMessage = new String(response);
        Map<Character, String> attributes = ScramBase.getAttributes(clientMessage);
        String cnonce = attributes.get('r');

        if(cnonce == null)
        {
            throw new SaslException("SCRAM: No nonce found in client message.");
        }

        // If no client message has been received yet.
        if(this.clientFirstMessageBare == null)
        {

            // Note that the client's first message will always start with "n",
            // "y",
            // or "p"; otherwise, the message is invalid and authentication MUST
            // fail.
            if(!clientMessage.startsWith("n") && !clientMessage.startsWith("y") && !clientMessage.startsWith("p"))
            {
                throw new SaslException("SCRAM: Client first message must start with n, y or p.");
            }

            String user = attributes.get('n');

            if(user == null)
            {
                throw new SaslException("SCRAM: No user found in client first message.");
            }

            // Upon receipt of the username by the server, the server MUST
            // either prepare it using the "SASLprep" profile [RFC4013] of the
            // "stringprep" algorithm [RFC3454] treating it as a query string
            // (i.e., unassigned Unicode codepoints are allowed) or otherwise
            // be prepared to do SASLprep-aware string comparisons and/or
            // index lookups. If the preparation of the username fails or
            // results in an empty string, the server SHOULD abort the
            // authentication exchange.

            user = ScramServer.validateAndGetUsername(ScramBase.prepare(attributes.get('n')));

            // The syntax of this field is the same as that of the "n" field
            // with respect to quoting of '=' and ','.
            this.authorizationId = ScramServer.validateAndGetUsername(attributes.get('a'));

            // Retrieve the password for the user.
            NameCallback ncb = new NameCallback("SCRAM username: ", user);
            ncb.setName(user);
            PasswordCallback pcb = new PasswordCallback("SCRAM password: ", false);
            try
            {
                this.callbackHandler.handle(new Callback[]{ncb, pcb});
            } catch(IOException | UnsupportedCallbackException e)
            {
                throw new SaslException("SCRAM: Error retrieving password.");
            }
            this.password = pcb.getPassword();
            pcb.clearPassword();

            // Generate salt.
            final Random r = Utils.random;
            this.salt = new byte[32];
            r.nextBytes(this.salt);

            try
            {
                this.nonce = (cnonce + ScramBase.generateNonce());
            } catch(NoSuchAlgorithmException e)
            {
                throw new SaslException();
            }

            this.clientFirstMessageBare = ScramBase.createClientFirstMessageBare(user, this.nonce);

            // In response, the server sends a "server-first-message" containing
            // the
            // user's iteration count i and the user's salt, and appends its own
            // nonce to the client-specified one.
            this.serverFirstMessage = "r=" + this.nonce + ",s=" + DatatypeConverter
                    .printBase64Binary(this.salt) + ",i=" + ScramServer.ITERATION_COUNT;
            return this.serverFirstMessage.getBytes();

        } else
        {

            // The server verifies the nonce and the proof
            if(!cnonce.equals(this.nonce))
            {
                throw new SaslException("SCRAM: Client provided invalid nonce.");
            }

            String clientProofBase64 = attributes.get('p');
            if(clientProofBase64 == null)
            {
                throw new SaslException("SCRAM: Client provided no client proof.");
            }

            this.channelBinding = attributes.get('c');
            if(this.channelBinding == null)
            {
                throw new SaslException("SCRAM: Client provided no channel-binding.");
            }

            // The server authenticates the client by computing the
            // ClientSignature,
            // exclusive-ORing that with the ClientProof to recover the
            // ClientKey
            // and verifying the correctness of the ClientKey by applying the
            // hash
            // function and comparing the result to the StoredKey. If the
            // ClientKey
            // is correct, this proves that the client has access to the user's
            // password.
            try
            {
                byte[] saltedPassword = this
                        .computeSaltedPassword(this.password, this.salt, ScramServer.ITERATION_COUNT);
                byte[] clientKey = this.computeClientKey(saltedPassword);
                String authMessage = this.computeAuthMessage();
                byte[] clientSignature = this.computeClientSignature(clientKey, authMessage);
                byte[] clientProof = DatatypeConverter.parseBase64Binary(clientProofBase64);
                byte[] recoveredClientKey = ScramBase.xor(clientSignature, clientProof);
                if(Arrays.equals(this.h(recoveredClientKey), this.computeStoredKey(clientKey)))
                {
                    this.complete = true;
                    byte[] serverKey = this.computeServerKey(saltedPassword);
                    // return ServerSignature
                    String serverFinalMessage = "v=" + DatatypeConverter
                            .printBase64Binary(this.computeServerSignature(serverKey, authMessage));
                    return serverFinalMessage.getBytes();
                } else
                {
                    // On failed authentication, the entire server-
                    // final-message is OPTIONAL; specifically, a server
                    // implementation
                    // MAY conclude the SASL exchange with a failure without
                    // sending the
                    // server-final-message. This results in an
                    // application-level error
                    // response without an extra round-trip.

                    // In XMPP we don't want to return a server-final-message,
                    // but instead return a XMPP SASL failure.
                    // Therefore throw an exception.
                    throw new SaslException("SCRAM authentication failed.");
                }

            } catch(InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e)
            {
                throw new SaslException("SCRAM", e);
            }
        }
    }

    @Override
    public String getAuthorizationID()
    {
        return this.authorizationId;
    }
}