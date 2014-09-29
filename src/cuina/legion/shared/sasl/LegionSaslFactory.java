package cuina.legion.shared.sasl;

import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

public class LegionSaslFactory implements SaslClientFactory, SaslServerFactory
{

	@Override
	public SaslClient createSaslClient(String[] mechanisms, String authorizationId,
			String protocol, String serverName, Map<String, ?> props, CallbackHandler callbackHandler)
			throws SaslException
	{
		for(String mechanism : mechanisms)
		{
			switch(mechanism)
			{
			case "SCRAM-SHA-1":
				return new ScramClient("SHA-1", authorizationId, protocol, serverName, props, callbackHandler);
			case "ANONYMOUS":
				return new AnonymousSaslClient();

			}
		}
		return null;
	}

	@Override
	public SaslServer createSaslServer(String mechanism, String protocol, String serverName,
			Map<String, ?> props, CallbackHandler callbackHandler) throws SaslException
	{
		switch(mechanism)
		{
		case "SCRAM-SHA-1":
			return new ScramServer("SHA-1", props, callbackHandler);
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
		return new String[] { "SCRAM-SHA-1", "ANONYMOUS", "PLAIN" };
	}
}
