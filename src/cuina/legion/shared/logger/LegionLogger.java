package cuina.legion.shared.logger;


public enum LegionLogger implements ILogger
{
	STDERR("stderr"), STDOUT("stdout"), RECEIVED("recv"), SEND("send"), DATABASE("database"), AUTH(
			"auth"), MODULE("module");

	private String loggerName;

	private LegionLogger(String loggerName)
	{
		this.loggerName = loggerName;
	}

	@Override
	public String getLoggerName()
	{
		return this.loggerName;
	}
}
