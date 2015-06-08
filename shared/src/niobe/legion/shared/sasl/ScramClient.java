package niobe.legion.shared.sasl;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * The implementation of the SCRAM-SHA-1 SASL mechanism.
 *
 * @author Christian Schudt
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

	private Integer iterationCount;
	private byte[]  salt;

	public ScramClient(String hashAlgorithm,
					   String authorizationId,
					   String protocol,
					   String serverName,
					   Map<String, ?> props,
					   CallbackHandler callbackHandler) throws SaslException
	{
		super(hashAlgorithm, props, callbackHandler);

		// authzID can only be encoded in UTF8 - RFC 2222
		if (authorizationId != null)
		{
			this.authorizationId = authorizationId;
			try
			{
				authorizationId.getBytes("UTF8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new SaslException("SCRAM: Error encoding authzid value into UTF-8", e);
			}
		}
		this.gs2Header =
				ScramClient.GS2_CBIND_FLAG + "," + (authorizationId != null ? "a=" + authorizationId : "") + ",";
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
		if (username != null)
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
		if (challenge.length == 0)
		{
			NameCallback ncb = this.authorizationId == null ? new NameCallback("SCRAM username: ") :
							   new NameCallback("SCRAM username: ", this.authorizationId);
			PasswordCallback pcb = new PasswordCallback("SCRAM-SHA-1 password: ", false);

			try
			{
				this.callbackHandler.handle(new Callback[]{ncb, pcb});
				this.passwd = pcb.getPassword();
				pcb.clearPassword();
				this.username = ncb.getName();
				if (this.passwd == null || this.username == null)
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
				if ("".equals(this.username))
				{
					throw new SaslException("SCRAM: Username must not be empty.");
				}
				this.username = ScramClient.replaceUsername(this.username);

			}
			catch (IOException e)
			{
				throw new SaslException("SCRAM: Error acquiring user name or password.", e);
			}
			catch (UnsupportedCallbackException e)
			{
				throw new SaslException("SCRAM: Cannot perform callback to acquire username or password", e);
			}

			try
			{
				this.cnonce = ScramBase.generateNonce();
			}
			catch (NoSuchAlgorithmException e)
			{
				throw new SaslException("SCRAM: Failed to generate nonce.", e);
			}
			this.clientFirstMessageBare = ScramBase.createClientFirstMessageBare(this.username, this.cnonce);
			// First, the client sends the "client-first-message"
			String clientFirstMessage = this.gs2Header + this.clientFirstMessageBare;
			return clientFirstMessage.getBytes();
		} else
		{

			// The server sends the salt and the iteration count to the client,
			// which then computes
			// the following values and sends a ClientProof to the server

			String serverMessage = new String(challenge);
			Map<Character, String> attributes = ScramBase.getAttributes(serverMessage);
			// check if server sends ServerSignature
			if (attributes.containsKey('v'))
			{
				// The client authenficates the server by computing the
				// ServerSignature and
				// comparing it with the value sent by server.
				try
				{
					byte[] saltedPassword = this.computeSaltedPassword(this.passwd, this.salt, this.iterationCount);
					byte[] serverKey = this.computeServerKey(saltedPassword);
					byte[] calculatedServerSignature =
							this.computeServerSignature(serverKey, this.computeAuthMessage());
					String serverSignatureBase64 = attributes.get('v');

					if (serverSignatureBase64 == null)
					{
						throw new SaslException("SCRAM: server signature was null in the server response.");
					}

					if (Arrays.equals(calculatedServerSignature,
									  DatatypeConverter.parseBase64Binary(serverSignatureBase64)))
					{
						this.isComplete = true;
						return new byte[0];
					}
				}
				catch (NoSuchAlgorithmException | InvalidKeyException e)
				{
					throw new SaslException(e.getMessage(), e);
				}
			} else
			{
				this.serverFirstMessage = serverMessage;

				this.nonce = attributes.get('r');
				this.clientFirstMessageBare = ScramBase.createClientFirstMessageBare(this.username, this.nonce);
				String saltBase64 = attributes.get('s');
				try
				{
					this.iterationCount = Integer.parseInt(attributes.get('i'));
				}
				catch (NumberFormatException e)
				{
					throw new SaslException("iterationCount could not be parsed.");
				}

				if (this.nonce == null)
				{
					throw new SaslException("SCRAM: nonce was null in the server response.");
				}
				if (saltBase64 == null)
				{
					throw new SaslException("SCRAM: salt was null in the server response.");
				}
				if (this.iterationCount == null)
				{
					throw new SaslException("SCRAM: iterationCount was null in the server response.");
				}

				this.salt = DatatypeConverter.parseBase64Binary(saltBase64);

				try
				{
					this.channelBinding = DatatypeConverter.printBase64Binary(this.gs2Header.getBytes());
					byte[] clientKey = this.computeClientKey(this.computeSaltedPassword(this.passwd,
																						this.salt,
																						this.iterationCount));
					byte[] clientSignature = this.computeClientSignature(clientKey, this.computeAuthMessage());
					// ClientProof := ClientKey XOR ClientSignature
					byte[] clientProof = ScramBase.xor(clientKey, clientSignature);
					this.clientFinalMessageWithoutProof = "c=" + this.channelBinding + ",r=" + this.nonce;
					// The client then responds by sending a
					// "client-final-message"
					// with the
					// same nonce and a ClientProof computed using the selected
					// hash
					// function as explained earlier.
					String clientFinalMessage = this.clientFinalMessageWithoutProof + ",p=" +
												DatatypeConverter.printBase64Binary(clientProof);
					return clientFinalMessage.getBytes();

				}
				catch (NoSuchAlgorithmException | InvalidKeyException e)
				{
					throw new SaslException(e.getMessage(), e);
				}
			}
		}
		return new byte[0];
	}

	@Override
	public boolean isComplete()
	{
		return this.isComplete;
	}

	@Override
	public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException
	{
		return new byte[0];
	}

	@Override
	public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException
	{
		return new byte[0];
	}

	@Override
	public Object getNegotiatedProperty(String propName)
	{
		return null;
	}

	@Override
	public void dispose() throws SaslException
	{

	}
}