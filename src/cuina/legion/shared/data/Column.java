package cuina.legion.shared.data;


public class Column
{
	private String columnName;
	private DataType columnType;
	private boolean primaryKey;
	private boolean nullable;
	private IDatasetType foreignKeyReference;

	public Column(String columnName, DataType columnType, boolean primaryKey, boolean nullable,
			IDatasetType foreignKeyReference)
	{
		this.columnName = columnName;
		this.columnType = columnType;
		this.primaryKey = primaryKey;
		this.nullable = nullable;
		this.foreignKeyReference = foreignKeyReference;
	}

	/**
	 * @return the columnName
	 */
	public String getColumnName()
	{
		return this.columnName;
	}

	/**
	 * @return the columnType
	 */
	public DataType getColumnType()
	{
		return this.columnType;
	}

	/**
	 * @return the primaryKey
	 */
	public boolean isPrimaryKey()
	{
		return this.primaryKey;
	}

	/**
	 * @return the nullable
	 */
	public boolean isNullable()
	{
		return this.nullable;
	}

	/**
	 * @return the foreignKeyReference
	 */
	public IDatasetType getForeignKeyReference()
	{
		return this.foreignKeyReference;
	}

}
