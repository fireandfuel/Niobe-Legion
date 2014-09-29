package cuina.legion.shared.data;


public enum DatasetType implements IDatasetType
{
	GROUP("legion:group", "legion_group", new Column("id", DataType.INTEGER, true, false, null),
			new Column("name", DataType.STRING, false, false, null), new Column("active",
					DataType.BOOL, false, false, null), new Column("can_administrate",
					DataType.BOOL, false, false, null)), USER("legion:user", "legion_user",
			new Column("id", DataType.INTEGER, true, false, null), new Column("name",
					DataType.STRING, false, false, null), new Column("password", DataType.STRING,
					false, false, null), new Column("group", DataType.INTEGER, false, false, GROUP));

	String xmlStanzaName;
	String tableName;
	Column[] columns;

	DatasetType(String xmlStanzaName, String tableName, Column... columns)
	{
		this.xmlStanzaName = xmlStanzaName;
		this.tableName = tableName;
		this.columns = columns;
	}

	@Override
	public String getTableName()
	{
		return this.tableName;
	}

	@Override
	public String getXmlStanzaName()
	{
		return this.xmlStanzaName;
	}

	@Override
	public Column[] getColumns()
	{
		return this.columns;
	}
}
