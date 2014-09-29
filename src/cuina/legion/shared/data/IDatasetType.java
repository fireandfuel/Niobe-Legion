package cuina.legion.shared.data;


public interface IDatasetType
{
	public String getTableName();

	public Column[] getColumns();

	public String getXmlStanzaName();
}
