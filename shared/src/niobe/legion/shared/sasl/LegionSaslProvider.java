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
		this.put("SaslClientFactory.SCRAM-SHA224", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-SHA256", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-SHA384", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-SHA512", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK224", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK256", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK288", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK384", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-KECCAK512", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-RipeMD128", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-RipeMD160", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.SCRAM-Whirlpool", LegionSaslFactory.class.getName());
		this.put("SaslClientFactory.ANONYMOUS", LegionSaslFactory.class.getName());

		this.put("SaslServerFactory.SCRAM-SHA-1", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA-1", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA224", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA256", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA384", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-SHA512", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK224", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK256", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK288", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK384", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-KECCAK512", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-RipeMD128", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-RipeMD160", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.SCRAM-Whirlpool", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.ANONYMOUS", LegionSaslFactory.class.getName());
		this.put("SaslServerFactory.PLAIN", LegionSaslFactory.class.getName());
	}

}
