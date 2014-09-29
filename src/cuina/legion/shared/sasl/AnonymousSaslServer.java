package cuina.legion.shared.sasl;

import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

public class AnonymousSaslServer implements SaslServer
{

	@Override
	public String getMechanismName()
	{
		return "ANONYMOUS";
	}

	@Override
	public byte[] evaluateResponse(byte[] response) throws SaslException
	{
		return new byte[0];
	}

	@Override
	public boolean isComplete()
	{
		return true;
	}

	@Override
	public String getAuthorizationID()
	{
		return "AnOnYmOuS";
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
