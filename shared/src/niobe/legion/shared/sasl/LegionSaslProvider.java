package niobe.legion.shared.sasl;

import java.security.Provider;

public class LegionSaslProvider extends Provider
{
	private static final long serialVersionUID = 1L;

	public LegionSaslProvider()
	{
		super("Legion", 1.0, "supplies SASL authenfication mechanisms for Legion server and client");

		this.put("SaslClientFactory.SCRAM-MD5", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-SHA-1", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-SHA-224", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-SHA-256", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-SHA-384", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-SHA-512", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK-224", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK-256", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK-288", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK-384", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK-512", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-RipeMD128", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-RipeMD160", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-Whirlpool", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.ANONYMOUS", LegionSaslFactory.class.getName());

		this.put("SaslServerFactory.SCRAM-MD5", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA-1", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA-224", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA-256", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA-384", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA-512", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK-224", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK-256", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK-288", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK-384", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK-512", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-RipeMD128", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-RipeMD160", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-Whirlpool", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.ANONYMOUS", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.PLAIN", LegionSaslFactory.class.getName());
	}

}
