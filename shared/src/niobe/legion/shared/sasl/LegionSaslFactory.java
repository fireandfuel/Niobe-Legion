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
				case "SCRAM-SHA224":
					return new ScramClient("SHA224", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-SHA256":
					return new ScramClient("SHA256", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-SHA384":
					return new ScramClient("SHA384", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-SHA512":
					return new ScramClient("SHA512", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK224":
					return new ScramClient("KECCAK224", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK256":
					return new ScramClient("KECCAK256", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK288":
					return new ScramClient("KECCAK288", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK384":
					return new ScramClient("KECCAK384", authorizationId, protocol, serverName, props, callbackHandler);
				case "SCRAM-KECCAK512":
					return new ScramClient("KECCAK512", authorizationId, protocol, serverName, props, callbackHandler);
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
			case "SCRAM-SHA224":
				return new ScramServer("SHA224", props, callbackHandler);
			case "SCRAM-SHA256":
				return new ScramServer("SHA256", props, callbackHandler);
			case "SCRAM-SHA384":
				return new ScramServer("SHA384", props, callbackHandler);
			case "SCRAM-SHA512":
				return new ScramServer("SHA512", props, callbackHandler);
			case "SCRAM-KECCAK224":
				return new ScramServer("KECCAK224", props, callbackHandler);
			case "SCRAM-KECCAK256":
				return new ScramServer("KECCAK256", props, callbackHandler);
			case "SCRAM-KECCAK288":
				return new ScramServer("KECCAK288", props, callbackHandler);
			case "SCRAM-KECCAK384":
				return new ScramServer("KECCAK384", props, callbackHandler);
			case "SCRAM-KECCAK512":
				return new ScramServer("KECCAK512", props, callbackHandler);
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
				"SCRAM-MD5", "SCRAM-SHA-1", "SCRAM-SHA224", "SCRAM-SHA256", "SCRAM-SHA384", "SCRAM-SHA512",
				"SCRAM-KECCAK224", "SCRAM-KECCAK256", "SCRAM-KECCAK288", "SCRAM-KECCAK384", "SCRAM-KECCAK512",
				"SCRAM-RipeMD128", "SCRAM-RipeMD160", "SCRAM-Whirlpool",
				"ANONYMOUS", "PLAIN"};
	}
}
