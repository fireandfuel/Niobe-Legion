package cuina.legion.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import cuina.legion.shared.Base64;
import cuina.legion.shared.Utils;
import cuina.legion.shared.data.Column;
import cuina.legion.shared.data.DataType;
import cuina.legion.shared.data.Dataset;
import cuina.legion.shared.data.DatasetType;
import cuina.legion.shared.data.IDatasetType;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;

public class Database
{
	private Connection conn;
	private Semaphore semaphore;

	private String connectionType;

	private Cipher encryptCipher, decryptCipher;
	private String host, port, database, user, password;

	private ISqlDialect sqlDialect;

	Database(String type, String... args) throws SQLException
	{
		this.connectionType = type;

		// encrypt / decrypt init
		try
		{
			byte[] key = (args[args.length - 2] != null) ? ((!args[args.length - 2].isEmpty()) ? args[args.length - 2]
					.getBytes("UTF-8") : null)
					: null;

			if(key != null)
			{
				Security.addProvider(new BouncyCastleProvider());

				MessageDigest md = MessageDigest.getInstance("RipeMD128");
				md.update(key);

				byte[] hash = md.digest();

				String initVectorString = null;

				File initVectorFile = new File("server.dat");
				if(initVectorFile.exists())
				{
					FileInputStream fis = new FileInputStream(initVectorFile);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

					initVectorString = reader.readLine();
					reader.close();
				} else
				{
					BufferedWriter writer = new BufferedWriter(new FileWriter(initVectorFile));
					initVectorString = Utils.getRandomString(32);

					writer.write(initVectorString);
					writer.close();
				}

				md.update(initVectorString.getBytes());
				byte[] initializationVector = md.digest();

				SecretKey keyValue = new SecretKeySpec(hash, "AES");

				AlgorithmParameterSpec iVSpec = new IvParameterSpec(initializationVector);

				this.encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
				this.encryptCipher.init(Cipher.ENCRYPT_MODE, keyValue, iVSpec);

				this.decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
				this.decryptCipher.init(Cipher.DECRYPT_MODE, keyValue, iVSpec);

			}
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException
				| InvalidKeyException | InvalidAlgorithmParameterException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} catch (FileNotFoundException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} catch (IOException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		}

		if(type.equalsIgnoreCase("sqlite") && args.length == 2)
		{
			this.database = args[0];
			this.semaphore = new Semaphore(1);

			this.sqlDialect = new SqliteDialect();

			try
			{
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e)
			{
				System.err.println("ERROR: SQLite Driver not found");
				Logger.exception(LegionLogger.DATABASE, e);
				System.exit(2);
			}
		} else if(type.equalsIgnoreCase("mariadb") && args.length == 7)
		{
			this.host = args[0];
			this.port = args[1];
			this.database = args[2];
			this.user = args[3];
			this.password = args[4];
			this.semaphore = new Semaphore(
					(args[6] != null && args[6].matches("\\d+")) ? Integer.parseInt(args[6]) : 1);

			this.sqlDialect = new MysqlDialect();

			try
			{
				Class.forName("org.mariadb.jdbc.Driver");
			} catch (ClassNotFoundException e)
			{
				System.err.println("ERROR: MariaDB Driver not found");
				Logger.exception(LegionLogger.DATABASE, e);
				System.exit(2);
			}
		}

		try
		{
			this.connect();
			this.disconnect();
		} catch (SQLException e)
		{
			this.disconnect();
			throw e;
		}

		List<String> users = this.getUsers();
		if(users == null || users.isEmpty())
		{
			this.createInitialDatabase();
		}
	}

	final String encrypt(String text)
	{
		if(text == null)
		{
			return null;
		}

		if(this.encryptCipher != null)
		{
			byte[] input;
			try
			{
				input = text.getBytes("UTF-8");

				ByteArrayInputStream reader = new ByteArrayInputStream(input);
				ByteArrayOutputStream writer = new ByteArrayOutputStream();

				byte[] buffer = new byte[16];
				int noBytes = 0;

				byte[] cipherBlock = new byte[this.encryptCipher.getOutputSize(buffer.length)];

				int cipherBytes;
				while((noBytes = reader.read(buffer)) != -1)
				{
					cipherBytes = this.encryptCipher.update(buffer, 0, noBytes, cipherBlock);
					writer.write(cipherBlock, 0, cipherBytes);
				}

				cipherBytes = this.encryptCipher.doFinal(cipherBlock, 0);
				writer.write(cipherBlock, 0, cipherBytes);

				byte[] output = writer.toByteArray();

				return Base64.encodeBytes(output);
			} catch (ShortBufferException | IOException | IllegalBlockSizeException
					| BadPaddingException e)
			{
				Logger.exception(LegionLogger.DATABASE, e);
				return text;
			}
		}
		return text;
	}

	final void connect() throws SQLException
	{
		if(this.conn == null || (this.conn != null && this.conn.isClosed()))
		{
			switch(this.connectionType)
			{
				case "mariadb":
					this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
							+ this.port + "/" + this.database + "?" + "user=" + this.user + "&"
							+ "password=" + this.password);
					break;
				case "sqlite":

					if(!new File(this.database).exists())
					{
						this.conn = DriverManager.getConnection("jdbc:sqlite:" + this.database);
						this.createInitialDatabase();
					} else
					{
						this.conn = DriverManager.getConnection("jdbc:sqlite:" + this.database);
					}
					break;
				default:
					break;
			}
		}
	}

	public List<String> getUsers()
	{
		ArrayList<String> users = new ArrayList<String>();

		this.create(DatasetType.GROUP);
		this.create(DatasetType.USER);

		List<Dataset> results = this.select(DatasetType.USER, "`name`", null,
				"ORDER BY `name` ASC", false);

		if(results != null)
		{
			for(Dataset result : results)
			{
				users.add(result.getString("name"));
			}
		}

		return Collections.unmodifiableList(users);
	}

	public String getPassword(String user)
	{
		List<Dataset> results = this.select(DatasetType.USER, "`password`",
				"`name` = '" + this.encrypt(user) + "'", null, false);

		if(results != null && results.size() == 1)
		{
			return results.get(0).getString("password");
		}

		return null;
	}

	final void createInitialDatabase()
	{
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		}

		byte[] entropy = new byte[1024];
		Utils.random.nextBytes(entropy);
		md.update(entropy, 0, 1024);
		String password = new BigInteger(1, md.digest()).toString(16).substring(0, 12);

		Dataset group = new Dataset(DatasetType.GROUP);
		group.set("id", null);
		group.set("name", this.encrypt("administrators"));
		group.set("active", 1);
		group.set("can_administrate", 1);
		this.insert(group);

		group.set("id", null);
		group.set("name", this.encrypt("users"));
		group.set("active", 1);
		group.set("can_administrate", 0);
		this.insert(group);

		group.set("id", null);
		group.set("name", this.encrypt("inactive"));
		group.set("active", 0);
		group.set("can_administrate", 0);
		this.insert(group);

		Dataset user = new Dataset(DatasetType.USER);
		user.set("id", null);
		user.set("name", "root");
		user.set("password", password);
		user.set("group", 1);
		this.insert(user);

		System.out.println("Root password is: " + password);
		System.out.println("SECURITY WARNING: Please change the root password later");
	}

	final void disconnect()
	{
		try
		{
			if(this.conn != null && !this.conn.isClosed())
			{
				this.conn.close();
			}
		} catch (SQLException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		}

	}

	final String decrypt(String text)
	{
		if(text == null)
		{
			return null;
		}

		if(this.decryptCipher != null)
		{
			byte[] input;
			try
			{
				input = Base64.decode(text);

				ByteArrayInputStream reader = new ByteArrayInputStream(input);
				ByteArrayOutputStream writer = new ByteArrayOutputStream();

				byte[] buffer = new byte[16];
				int noBytes = 0;

				byte[] cipherBlock = new byte[this.decryptCipher.getOutputSize(buffer.length)];

				int cipherBytes;
				while((noBytes = reader.read(buffer)) != -1)
				{
					cipherBytes = this.decryptCipher.update(buffer, 0, noBytes, cipherBlock);
					writer.write(cipherBlock, 0, cipherBytes);
				}

				cipherBytes = this.decryptCipher.doFinal(cipherBlock, 0);
				writer.write(cipherBlock, 0, cipherBytes);

				byte[] output = writer.toByteArray();
				return new String(output, "UTF-8");

			} catch (IOException | ShortBufferException | IllegalBlockSizeException
					| BadPaddingException e)
			{
				Logger.exception(LegionLogger.DATABASE, e);
				return text;
			}
		}
		return text;
	}

	public void create(IDatasetType dataType)
	{
		try
		{
			this.semaphore.acquire();
			this.connect();

			if(dataType != null)
			{
				Column[] columns = dataType.getColumns();
				if(columns != null && columns.length > 0)
				{
					Statement stat = this.conn.createStatement();

					String sql = "CREATE TABLE IF NOT EXISTS `" + dataType.getTableName() + "` (";

					LinkedHashMap<String, IDatasetType> foreignKeys = new LinkedHashMap<String, IDatasetType>();

					for(int columnIndex = 0; columnIndex < columns.length; columnIndex++)
					{
						Column column = columns[columnIndex];

						String columnName = column.getColumnName();
						String sqlDataType = this.getSqlTypeFromDataType(column.getColumnType());
						boolean primaryKey = column.isPrimaryKey();
						boolean nullable = column.isNullable();
						IDatasetType foreignKey = column.getForeignKeyReference();

						sql += "`" + columnName + "` " + sqlDataType;

						if(!nullable)
						{
							sql += " NOT NULL";
						}

						if(primaryKey)
						{
							sql += " PRIMARY KEY " + this.sqlDialect.autoIncrementName();
						}

						if(foreignKey != null)
						{
							foreignKeys.put(columnName, foreignKey);
						}

						if(columnIndex < columns.length - 1 || !foreignKeys.isEmpty())
						{
							sql += ", ";
						}

					}

					if(!foreignKeys.isEmpty())
					{
						ArrayList<String> keys = new ArrayList<String>(foreignKeys.keySet());

						for(int keyIndex = 0; keyIndex < keys.size(); keyIndex++)
						{
							sql += "FOREIGN KEY (`" + keys.get(keyIndex) + "`) REFERENCES `"
									+ foreignKeys.get(keys.get(keyIndex)).getTableName()
									+ "`(`id`)";

							if(keyIndex < keys.size() - 1)
							{
								sql += ", ";
							}
						}
					}

					sql += ");";

					stat.executeUpdate(sql);
				}
			}
		} catch (SQLException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} catch (InterruptedException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} finally
		{
			this.disconnect();
			this.semaphore.release();
		}
	}

	public int insert(Dataset dataset)
	{
		if(dataset != null && dataset.getDatasetContentSize() > 0)
		{
			Statement stat;
			try
			{
				this.semaphore.acquire();
				this.connect();

				stat = this.conn.createStatement();

				String table = dataset.getClassType().getTableName();

				int id = 0;

				Map<String, Object> values = dataset.getDatabaseValues();
				if(values.get("id") != null && values.get("id") instanceof Integer
						&& (Integer) values.get("id") <= 0)
				{
					// id is 0 or below 0, get the last inserted id and increase
					// it by 1
					id = this.getLastInserted(table) + 1;

					values.put("id", id);
				}
				List<String> keys = new ArrayList<String>(values.keySet());

				if(!keys.isEmpty())
				{

					String sql = "INSERT INTO `" + table + "` (";

					for(int index = 0; index < keys.size(); index++)
					{
						sql += "`" + keys.get(index) + "`"
								+ ((index < keys.size() - 1) ? ", " : "");
					}
					sql += ") VALUES (";
					for(int index = 0; index < keys.size(); index++)
					{
						String value = null;
						Object obj = values.get(keys.get(index));

						if(obj instanceof String)
						{
							value = "'" + this.encrypt((String) obj) + "'";
						} else if(obj instanceof java.util.Date)
						{
							value = Long.toString(((java.util.Date) obj).getTime());
						} else if(obj instanceof java.sql.Date)
						{
							value = Long.toString(((java.sql.Date) obj).getTime());
						} else if(obj instanceof java.sql.Timestamp)
						{
							value = Long.toString(((java.sql.Timestamp) obj).getTime());
						} else if(obj instanceof Integer)
						{
							value = Integer.toString((Integer) obj);
						} else if(obj instanceof Long)
						{
							value = Long.toString((Long) obj);
						} else if(obj instanceof Boolean)
						{
							value = Boolean.toString((Boolean) obj);
						} else if(obj instanceof Float)
						{
							value = Float.toString((Float) obj);
						} else if(obj instanceof Double)
						{
							value = Double.toString((Double) obj);
						}
						if(value == null)
						{
							value = "NULL";
						}

						sql += value + ((index < keys.size() - 1) ? ", " : "");
					}

					sql += ");";

					stat.executeUpdate(sql);

					return (id <= 0) ? this.getLastInserted(table) : id;

				}
			} catch (SQLException e)
			{
				Logger.exception(LegionLogger.DATABASE, e);
			} catch (InterruptedException e)
			{
				Logger.exception(LegionLogger.DATABASE, e);
			} finally
			{
				this.disconnect();
				this.semaphore.release();
			}
		}

		return 0;
	}

	private int getLastInserted(String table)
	{
		Statement query;

		try
		{
			query = this.conn.createStatement();

			String sql = "SELECT MAX(ID) FROM " + table + ";";

			ResultSet result = query.executeQuery(sql);

			if(result.next())
			{
				return result.getInt(1);
			}
		} catch (SQLException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		}
		return 0;
	}

	public List<Dataset> select(IDatasetType type, String rows, String where, String extra,
			boolean loadForeignKey)
	{
		Statement query;

		try
		{
			String table = type.getTableName();

			this.semaphore.acquire();
			this.connect();

			if(table.contains("`"))
			{
				table = table.replace("`", "");
			}

			query = this.conn.createStatement();
			String sql = "SELECT " + rows + " FROM `" + table + "`";

			if(where != null && !where.isEmpty())
			{
				sql += " WHERE " + where;
			}

			if(extra != null && !extra.isEmpty())
			{
				sql += " " + extra + ";";
			} else
			{
				sql += ";";
			}

			ResultSet set = query.executeQuery(sql);

			return this.getResult(type, set, loadForeignKey);
		} catch (SQLException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} catch (InterruptedException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} finally
		{
			this.disconnect();
			this.semaphore.release();
		}

		return null;
	}

	private List<Dataset> getResult(IDatasetType type, ResultSet set, boolean loadForeignKey)
			throws SQLException
	{
		List<Dataset> results = new ArrayList<Dataset>();
		if(set != null)
		{
			while(set.next())
			{
				Dataset result = new Dataset(type);

				ResultSetMetaData metadata = set.getMetaData();

				int lenght = metadata.getColumnCount();

				for(int i = 0; i < lenght; i++)
				{
					String columnName = metadata.getColumnLabel(i + 1);

					DataType dataType = this.getDataTypeFromOfColumnName(type, columnName);
					switch(dataType)
					{
						case BOOL:
							result.set(columnName, set.getBoolean(i + 1));
							break;
						case STRING:
							result.set(columnName, this.decrypt(set.getString(i + 1)));
							break;
						default:

							IDatasetType foreignKey = this.getForeignKey(type, columnName);

							if(foreignKey != null && loadForeignKey)
							{
								// load foreign key
								String fkSql = "SELECT * FROM `"
										+ foreignKey.getTableName().replace("`", "")
										+ "` WHERE id = " + set.getInt(i + 1);
								Statement fkQuery = this.conn.createStatement();

								ResultSet fkResult = fkQuery.executeQuery(fkSql);
								List<Dataset> fkDatasets = this.getResult(foreignKey, fkResult,
										loadForeignKey);
								if(fkDatasets != null && fkDatasets.size() == 1)
								{
									result.set(columnName, fkDatasets.get(0));
								}
							} else
							{
								result.set(columnName, set.getObject(i + 1));
							}
							break;
					}
				}

				results.add(result);

			}
		}
		return Collections.unmodifiableList(results);
	}

	private IDatasetType getForeignKey(IDatasetType type, String columnName)
	{
		if(type != null && columnName != null)
		{
			for(Column column : type.getColumns())
			{
				if(columnName.equals(column.getColumnName()))
				{
					return column.getForeignKeyReference();
				}
			}
		}

		return null;
	}

	public void update(Dataset dataset, String where)
	{
		Statement stat;

		if(dataset.getInteger("id") == null || dataset.getInteger("id").intValue() <= 0)
		{
			throw new NullPointerException("Dataset ID is not set correctly.");
		}

		try
		{
			this.semaphore.acquire();
			this.connect();

			Map<String, Object> values = dataset.getDatabaseValues();

			List<String> keys = new ArrayList<String>(values.keySet());

			if(!keys.isEmpty())
			{
				String table = dataset.getClassType().getTableName();

				stat = this.conn.createStatement();
				String sql = "UPDATE `" + table + "` SET ";

				for(int index = 0; index < keys.size(); index++)
				{
					String key = keys.get(index);
					String value = null;
					Object obj = values.get(key);

					if(obj instanceof String)
					{
						value = "'" + this.encrypt((String) obj) + "'";
					} else if(obj instanceof java.util.Date)
					{
						value = Long.toString(((java.util.Date) obj).getTime());
					} else if(obj instanceof java.sql.Date)
					{
						value = Long.toString(((java.sql.Date) obj).getTime());
					} else if(obj instanceof java.sql.Timestamp)
					{
						value = Long.toString(((java.sql.Timestamp) obj).getTime());
					} else if(obj instanceof Integer)
					{
						value = Integer.toString((Integer) obj);
					} else if(obj instanceof Long)
					{
						value = Long.toString((Long) obj);
					} else if(obj instanceof Boolean)
					{
						value = Integer.toString(((Boolean) obj).booleanValue() ? 1 : 0);
					} else if(obj instanceof Float)
					{
						value = Float.toString((Float) obj);
					} else if(obj instanceof Double)
					{
						value = Double.toString((Double) obj);
					}
					if(value == null)
					{
						value = "NULL";
					}

					sql += key + " = " + value + ((index < keys.size() - 1) ? ", " : "");
				}

				if(where != null && !where.isEmpty())
				{
					sql += " WHERE " + where;
				}

				stat.executeUpdate(sql);
			}
		} catch (SQLException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} catch (InterruptedException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} finally
		{
			this.disconnect();
			this.semaphore.release();
		}
	}

	public void delete(IDatasetType type, String where)
	{
		Statement stat;

		try
		{
			this.semaphore.acquire();
			this.connect();

			stat = this.conn.createStatement();

			stat.executeUpdate("DELETE FROM `" + type.getTableName() + "` WHERE " + where);

		} catch (SQLException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} catch (InterruptedException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		} finally
		{
			this.disconnect();
			this.semaphore.release();
		}
	}

	String getSqlTypeFromDataType(DataType dataType)
	{
		if(this.sqlDialect != null)
		{
			switch(dataType)
			{
				case BLOB:
					return this.sqlDialect.blobName();
				case BOOL:
					return this.sqlDialect.boolName();
				case DATE:
					return this.sqlDialect.dateName();
				case DOUBLE:
					return this.sqlDialect.doubleName();
				case FLOAT:
					return this.sqlDialect.floatName();
				case INTEGER:
					return this.sqlDialect.integerName();
				case STRING:
					return this.sqlDialect.stringName();
			}
		}
		return null;
	}

	DataType getDataTypeFromOfColumnName(IDatasetType datasetType, String columnName)
	{
		if(datasetType != null && columnName != null)
		{
			Column[] columns = datasetType.getColumns();

			if(columns != null)
			{
				for(Column column : columns)
				{
					if(columnName.equals(column.getColumnName()))
					{
						return column.getColumnType();
					}
				}
			}
		}
		return null;
	}
}
