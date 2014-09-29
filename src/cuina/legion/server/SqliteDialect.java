package cuina.legion.server;

public class SqliteDialect implements ISqlDialect
{

	@Override
	public String boolName()
	{
		return "INTEGER";
	}

	@Override
	public String integerName()
	{
		return "INTEGER";
	}

	@Override
	public String longName()
	{
		return "INTEGER";
	}

	@Override
	public String floatName()
	{
		return "REAL";
	}

	@Override
	public String doubleName()
	{
		return null;
	}

	@Override
	public String stringName()
	{
		return "TEXT";
	}

	@Override
	public String dateName()
	{
		// TODO Auto-generated method stub
		return "INTEGER";
	}

	@Override
	public String blobName()
	{
		return "BLOB";
	}

	@Override
	public String autoIncrementName()
	{
		return "AUTOINCREMENT";
	}

}
