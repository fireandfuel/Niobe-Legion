package niobe.legion.server.module;

import niobe.legion.shared.module.IModuleDatabaseManager;

public class ModuleDatabaseManager implements IModuleDatabaseManager
{
	//	private ModuleInstance     moduleInstance;
	//	private List<IDatasetType> datasetTypes;
	//	private final Database database = Server.getDatabase();
	//
	//	public ModuleDatabaseManager(ModuleInstance moduleInstance)
	//	{
	//		this.moduleInstance = moduleInstance;
	//		this.datasetTypes = this.moduleInstance.getDataTypes();
	//
	//		if (checkDatabase())
	//			;
	//
	//		this.moduleInstance.setDatabaseManager(this);
	//	}
	//
	//	private boolean checkDatabase()
	//	{
	//		List<Dataset> moduleDatasets = database
	//				.select(DatasetType.MODULE,
	//						"*",
	//						"name = " + moduleInstance.getName(),
	//						null,
	//						false);
	//
	//		if (moduleDatasets != null && moduleDatasets.size() == 1)
	//		{
	//			List<Dataset> moduleTableDatasets = this.database
	//					.select(DatasetType.MODULE_TABLE_REGISTRY,
	//							"*",
	//							"module_id = " + moduleDatasets.get(0).getInteger("id"),
	//							null,
	//							false);
	//
	//			checkTables(moduleInstance.getDataTypes(),
	//						moduleTableDatasets,
	//						moduleDatasets.get(0).getInteger("id"));
	//		}
	//		else
	//		{
	//
	//		}
	//
	//		return false;
	//	}
	//
	//	private boolean checkTables(List<IDatasetType> datasetTypes,
	//								List<Dataset> moduleTableDatasets,
	//								int moduleId)
	//	{
	//		if (datasetTypes != null && moduleTableDatasets != null)
	//		{
	//			datasetTypes.forEach(datasetType -> {
	//				Optional<Dataset> filtered = moduleTableDatasets.stream().filter(
	//						dataset -> dataset.getString("name").equals(datasetType.getTableName()))
	//																.findFirst();
	//
	//				if (filtered.isPresent())
	//				{
	//					// update if necessary
	//					Dataset moduleTableDataset = filtered.get();
	//					checkForChanges(datasetType,
	//									moduleTableDataset);
	//				}
	//				else
	//				{
	//					// insert table in module table registry
	//					Dataset moduleTableDataset = new Dataset(DatasetType.MODULE_TABLE_REGISTRY);
	//					moduleTableDataset.set("name",
	//										   datasetType.getTableName());
	//					moduleTableDataset.set("version",
	//										   1);
	//					moduleTableDataset.set("create_statement",
	//										   createStatement(datasetType));
	//					moduleTableDataset.set("module_id",
	//										   moduleId);
	//					database.insert(moduleTableDataset);
	//
	//					// createTable table
	//					database.createTable(datasetType);
	//				}
	//			});
	//		}
	//
	//		return false;
	//	}
	//
	//	private String createStatement(IDatasetType dataType)
	//	{
	//		Column[] columns = dataType.getColumns();
	//		if (columns != null && columns.length > 0)
	//		{
	//			String sql = "CREATE TABLE IF NOT EXISTS `" + dataType.getTableName() + "` (";
	//
	//			LinkedHashMap<String, IDatasetType> foreignKeys = new LinkedHashMap<String, IDatasetType>();
	//
	//			for (int columnIndex = 0; columnIndex < columns.length; columnIndex++)
	//			{
	//				Column column = columns[columnIndex];
	//
	//				String columnName = column.getColumnName();
	//				String sqlDataType = this.database.getSqlTypeFromDataType(column.getColumnType());
	//				String defaultValue = column.getDefaultValue();
	//				boolean primaryKey = column.isPrimaryKey();
	//				boolean nullable = column.isNullable();
	//				IDatasetType foreignKey = column.getForeignKeyReference();
	//
	//				sql += "`" + columnName + "` " + sqlDataType;
	//
	//				if (!nullable)
	//				{
	//					sql += " NOT NULL";
	//				}
	//
	//				if (defaultValue != null)
	//				{
	//					sql += " DEFAULT " + defaultValue;
	//				}
	//
	//				if (primaryKey)
	//				{
	//					sql += " PRIMARY KEY " + this.database.getSqlDialect().autoIncrementName();
	//				}
	//
	//				if (foreignKey != null)
	//				{
	//					foreignKeys.put(columnName,
	//									foreignKey);
	//				}
	//
	//				if (columnIndex < columns.length - 1 || !foreignKeys.isEmpty())
	//				{
	//					sql += ", ";
	//				}
	//			}
	//
	//			if (!foreignKeys.isEmpty())
	//			{
	//				ArrayList<String> keys = new ArrayList<String>(foreignKeys.keySet());
	//
	//				for (int keyIndex = 0; keyIndex < keys.size(); keyIndex++)
	//				{
	//					sql += "FOREIGN KEY (`" + keys.get(keyIndex) + "`) REFERENCES `"
	//							+ foreignKeys.get(keys.get(keyIndex)).getTableName()
	//							+ "`(`id`)";
	//
	//					if (keyIndex < keys.size() - 1)
	//					{
	//						sql += ", ";
	//					}
	//				}
	//			}
	//
	//			sql += ");";
	//			return sql;
	//		}
	//		return null;
	//	}
	//
	//	private void checkForChanges(IDatasetType newDatasetType,
	//								 Dataset moduleTableDataset)
	//	{
	//		if (newDatasetType != null && moduleTableDataset != null)
	//		{
	//			ComparatorDatasetType comparedDatasetType = new ComparatorDatasetType(moduleTableDataset.getString("name"),
	//																				  moduleTableDataset
	//																						  .getString(
	//																								  "create_statement"),
	//																				  newDatasetType,
	//																				  this.database
	//																						  .getSqlDialect());
	//
	//			//			if(comparedDatasetType.getColumns() != null
	//			//					&& comparedDatasetType.getColumns().length > 0)
	//			//			{
	//			//
	//			//			}
	//		}
	//	}

	//	public createStatement()
}
