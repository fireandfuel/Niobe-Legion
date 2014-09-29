package cuina.legion.shared.sasl;

import java.security.Provider;

public class LegionSaslProvider extends Provider
{
	private static final long serialVersionUID = 1L;

	public LegionSaslProvider()
	{
		super("Legion", 1.0,
				"supplies SASL authenfication mechanisms for Legion server and client");

		this.put("SaslClientFactory.SCRAM-SHA-1", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.ANONYMOUS", LegionSaslFactory.class.getName());

		this.put("SaslServerFactory.SCRAM-SHA-1", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.ANONYMOUS", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.PLAIN", LegionSaslFactory.class.getName());

	}

}
