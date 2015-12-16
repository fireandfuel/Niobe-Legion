package niobe.legion.shared.sasl;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;
import java.util.Map;

public class LegionSaslFactory implements SaslClientFactory, SaslServerFactory
{

	@Override
	public SaslClient createSaslClient(String[] mechanisms,
									   String authorizationId,
									   String protocol,
									   String serverName,
									   Map<String, ?> props,
									   CallbackHandler callbackHandler) throws SaslException
	{
		for (String mechanism : mechanisms)
		{
			switch (mechanism)
			{
				case "SCRAM-MD5":
					return new ScramClient("MD5", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-SHA-1":
					return new ScramClient("SHA-1", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-SHA-224":
					return new ScramClient("SHA-224", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-SHA-256":
					return new ScramClient("SHA-256", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-SHA-384":
					return new ScramClient("SHA-384", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-SHA-512":
					return new ScramClient("SHA-512", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK-224":
					return new ScramClient("KECCAK-224", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK-256":
					return new ScramClient("KECCAK-256", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK-288":
					return new ScramClient("KECCAK-288", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK-384":
					return new ScramClient("KECCAK-384", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK-512":
					return new ScramClient("KECCAK-512", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-RipeMD128":
					return new ScramClient("RipeMD128", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-RipeMD160":
					return new ScramClient("RipeMD160", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-Whirlpool":
					return new ScramClient("Whirlpool", authorizationId, protocol, serverName, props, callbackHandler);
				case "ANONYMOUS":
					return new AnonymousSaslClient();

			}
		}
		return null;
	}

	@Override
	public SaslServer createSaslServer(String mechanism,
									   String protocol,
									   String serverName,
									   Map<String, ?> props,
									   CallbackHandler callbackHandler) throws SaslException
	{
		switch (mechanism)
		{
			case "SCRAM-MD5":
				return new ScramServer("MD5", props, callbackHandler);
			case "SCRAM-SHA-1":
				return new ScramServer("SHA-1", props, callbackHandler);
			case "SCRAM-SHA-224":
				return new ScramServer("SHA-224", props, callbackHandler);
			case "SCRAM-SHA-256":
				return new ScramServer("SHA-256", props, callbackHandler);
			case "SCRAM-SHA-384":
				return new ScramServer("SHA-384", props, callbackHandler);
			case "SCRAM-SHA-512":
				return new ScramServer("SHA-512", props, callbackHandler);
			case "SCRAM-KECCAK-224":
				return new ScramServer("KECCAK-224", props, callbackHandler);
			case "SCRAM-KECCAK-256":
				return new ScramServer("KECCAK-256", props, callbackHandler);
			case "SCRAM-KECCAK-288":
				return new ScramServer("KECCAK-288", props, callbackHandler);
			case "SCRAM-KECCAK-384":
				return new ScramServer("KECCAK-384", props, callbackHandler);
			case "SCRAM-KECCAK-512":
				return new ScramServer("KECCAK-512", props, callbackHandler);
			case "SCRAM-RipeMD128":
				return new ScramServer("RipeMD128", props, callbackHandler);
			case "SCRAM-RipeMD160":
				return new ScramServer("RipeMD160", props, callbackHandler);
			case "SCRAM-Whirlpool":
				return new ScramServer("Whirlpool", props, callbackHandler);
			case "ANONYMOUS":
				return new AnonymousSaslServer();
			case "PLAIN":
				return new PlainSaslServer(callbackHandler);
		}
		return null;
	}

	@Override
	public String[] getMechanismNames(Map<String, ?> props)
	{
		return new String[]{
				"SCRAM-MD5", "SCRAM-SHA-1", "SCRAM-SHA-224", "SCRAM-SHA-256", "SCRAM-SHA-384", "SCRAM-SHA-512",
				"SCRAM-KECCAK-224", "SCRAM-KECCAK-256", "SCRAM-KECCAK-288", "SCRAM-KECCAK-384", "SCRAM-KECCAK-512",
				"SCRAM-RipeMD128", "SCRAM-RipeMD160", "SCRAM-Whirlpool",
				"ANONYMOUS", "PLAIN"};
	}
}
