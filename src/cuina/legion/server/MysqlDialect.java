package cuina.legion.server;

public class MysqlDialect implements ISqlDialect
{

	@Override
	public String boolName()
	{
		return "BOOL";
	}

	@Override
	public String integerName()
	{
		return "INT";
	}

	@Override
	public String longName()
	{
		return "BIGINT";
	}

	@Override
	public String floatName()
	{
		return "FLOAT";
	}

	@Override
	public String doubleName()
	{
		return "DOUBLE";
	}

	@Override
	public String stringName()
	{
		return "TEXT";
	}

	@Override
	public String dateName()
	{
		return "DATE";
	}

	@Override
	public String blobName()
	{
		return "BLOB";
	}

	@Override
	public String autoIncrementName()
	{
		return "AUTO_INCREMENT";
	}

}
