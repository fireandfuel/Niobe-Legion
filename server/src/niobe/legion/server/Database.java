package niobe.legion.server;

import niobe.legion.shared.Base64;
import niobe.legion.shared.Utils;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.UserEntity;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Database
{
	private static Database database;

	@PersistenceUnit protected EntityManagerFactory entityManagerFactory;
	protected                  EntityManager        entityManager;
	private                    EntityTransaction    entityTransaction;

	Cipher encryptCipher, decryptCipher;

	public static Database init(String type, String... args) throws SQLException
	{
		if (Database.database == null)
		{
			Database.database = new Database(type, args);
		}
		return Database.database;
	}

	Database(String type, String... args) throws SQLException
	{
		HashMap<String, Object> properties = new HashMap<String, Object>();

		// encrypt / decrypt init
		try
		{
			byte[] key = (args[args.length - 2] != null) ?
						 ((!args[args.length - 2].isEmpty()) ? args[args.length - 2].getBytes("UTF-8") : null) : null;

			if (key != null && !type.startsWith("h2"))
			{
				Security.addProvider(new BouncyCastleProvider());

				MessageDigest md = MessageDigest.getInstance("RipeMD128");
				md.update(key);

				byte[] hash = md.digest();

				String initVectorString;

				File initVectorFile = new File("server.dat");
				if (initVectorFile.exists())
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
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException
				| InvalidKeyException | InvalidAlgorithmParameterException | IOException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		}

		if (type.equalsIgnoreCase("mariadb") && args.length == 8)
		{
			String host = args[0];
			String port = args[1];
			String database = args[2];
			String user = args[3];
			String password = args[4];
			String minConnections = args[6];
			String maxConnections = args[7];

			properties.put("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver");
			properties.put("hibernate.connection.url", "jdbc:mysql://" + host + ":" + port + "/" + database);
			properties.put("hibernate.connection.user", user);
			properties.put("hibernate.connection.password", password);
			properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
			properties.put("hibernate.c3p0.min_size", minConnections);
			properties.put("hiberante.c3p0.max_size", maxConnections);
			properties.put("hibernate.c3p0.max_statements", "50");

		} else if (type.equalsIgnoreCase("h2-embedded") && args.length == 6)
		{
			String database = args[0];
			String user = args[1];
			String password = args[2];
			String encryptionPassword = args[3];
			String minConnections = args[4];
			String maxConnections = args[5];

			properties.put("hibernate.connection.driver_class", "org.h2.Driver");
			properties.put("hibernate.connection.url",
						   "jdbc:h2:file:" + database + ((encryptionPassword != null && encryptionPassword.isEmpty()) ?
														 ";CIPHER=AES;DATABASE_TO_UPPER=FALSE" :
														 ";DATABASE_TO_UPPER=FALSE"));
			properties.put("hibernate.connection.user", user);
			properties.put("hibernate.connection.password",
						   (encryptionPassword != null && encryptionPassword.isEmpty()) ?
						   (password + " " + encryptionPassword) : password);
			properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
			properties.put("hibernate.c3p0.min_size", minConnections);
			properties.put("hiberante.c3p0.max_size", maxConnections);
			properties.put("hibernate.c3p0.max_statements", "50");
		} else if (type.equalsIgnoreCase("h2-server") && args.length == 7)
		{
			String host = args[0];
			String port = args[1];
			String database = args[2];
			String user = args[3];
			String password = args[4];
			String encryptionPassword = args[5];
			String minConnections = args[6];
			String maxConnections = args[7];

			properties.put("hibernate.connection.driver_class", "org.h2.Driver");
			properties.put("hibernate.connection.url", "jdbc:h2:tcp://" + host + ":" + port + "/" + database +
													   ((encryptionPassword != null && encryptionPassword.isEmpty()) ?
														";CIPHER=AES;DATABASE_TO_UPPER=FALSE" :
														";DATABASE_TO_UPPER=FALSE"));
			properties.put("hibernate.connection.user", user);
			properties.put("hibernate.connection.password",
						   (encryptionPassword != null && encryptionPassword.isEmpty()) ?
						   (password + " " + encryptionPassword) : password);
			properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
			properties.put("hibernate.c3p0.min_size", minConnections);
			properties.put("hiberante.c3p0.max_size", maxConnections);
			properties.put("hibernate.c3p0.max_statements", "50");
		}

		this.entityManagerFactory = Persistence.createEntityManagerFactory("niobe_legion", properties);

		this.entityManager = this.entityManagerFactory.createEntityManager();
	}

	public final String encrypt(String text)
	{
		if (text == null)
		{
			return null;
		}

		if (this.encryptCipher != null)
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
				while ((noBytes = reader.read(buffer)) != -1)
				{
					cipherBytes = this.encryptCipher.update(buffer, 0, noBytes, cipherBlock);
					writer.write(cipherBlock, 0, cipherBytes);
				}

				cipherBytes = this.encryptCipher.doFinal(cipherBlock, 0);
				writer.write(cipherBlock, 0, cipherBytes);

				byte[] output = writer.toByteArray();

				return Base64.encodeBytes(output);
			}
			catch (ShortBufferException | IOException | IllegalBlockSizeException
					| BadPaddingException e)
			{
				Logger.exception(LegionLogger.DATABASE, e);
				return text;
			}
		}
		return text;
	}

	public final String decrypt(String text)
	{
		if (text == null)
		{
			return null;
		}

		if (this.decryptCipher != null)
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
				while ((noBytes = reader.read(buffer)) != -1)
				{
					cipherBytes = this.decryptCipher.update(buffer, 0, noBytes, cipherBlock);
					writer.write(cipherBlock, 0, cipherBytes);
				}

				cipherBytes = this.decryptCipher.doFinal(cipherBlock, 0);
				writer.write(cipherBlock, 0, cipherBytes);

				byte[] output = writer.toByteArray();
				return new String(output, "UTF-8");

			}
			catch (IOException | ShortBufferException | IllegalBlockSizeException
					| BadPaddingException e)
			{
				Logger.exception(LegionLogger.DATABASE, e);
				return text;
			}
		}
		return text;
	}

	public final void beginTransaction()
	{
		if (this.entityManager != null)
		{
			this.entityTransaction = this.entityManager.getTransaction();
			this.entityTransaction.begin();
		}
	}

	public final void commitTransaction()
	{
		if (this.entityTransaction != null)
		{
			this.entityTransaction.commit();
			this.entityTransaction = null;
		}
	}

	public final void rollbackTransaction()
	{
		if (this.entityTransaction != null)
		{
			this.entityTransaction.rollback();
			this.entityTransaction = null;
		}
	}

	public final <T> T getResult(final String queryName, final Class<T> clazz, Map.Entry... parameters)
	{
		return (T) this.getResult(queryName, clazz, parametersToMap(parameters));
	}

	public final <T> T getResult(final String queryName, final Class<T> clazz, Map<String, Object> parameters)
	{
		return (T) this.getResult(this.entityManager.createNamedQuery(queryName, clazz), parameters);
	}

	private <T> T getResult(final TypedQuery<T> query, final Map<String, Object> parameters)
	{
		if (parameters != null)
		{
			parameters.forEach(query::setParameter);
		}

		T result = null;
		try
		{
			this.beginTransaction();
			result = query.getSingleResult();
			this.commitTransaction();
		}
		catch (NoResultException e)
		{
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.rollbackTransaction();
		}
		return (T) result;
	}

	public final <T> List<T> getResults(final Class<T> clazz)
	{
		return getResults(clazz, null);
	}

	public final <T> List<T> getResults(final Class<T> clazz, final String parameters)
	{
		Query query = this.entityManager.createNativeQuery(parameters, clazz);

		List<T> results = null;
		try
		{
			this.beginTransaction();
			results = query.getResultList();
			this.commitTransaction();
		}
		catch (NoResultException e)
		{
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.rollbackTransaction();
		}
		return results;
	}

	public final <T> List<T> getResults(final String queryName, final Class<T> clazz, Map.Entry... parameters)
	{
		return this.getResults(queryName, clazz, parametersToMap(parameters));
	}

	public final <T> List<T> getResults(final String queryName, final Class<T> clazz, Map<String, Object> parameters)
	{
		return this.getResults(this.entityManager.createNamedQuery(queryName, clazz), parameters);

	}

	private <T> List<T> getResults(final TypedQuery<T> query, final Map<String, Object> parameters)
	{
		if (parameters != null)
		{
			parameters.forEach(query::setParameter);
		}

		List<T> results = null;
		try
		{
			this.beginTransaction();
			results = query.getResultList();
			this.commitTransaction();
		}
		catch (NoResultException e)
		{
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.rollbackTransaction();
		}
		return results;
	}

	public final void insert(Object object)
	{
		try
		{
			this.beginTransaction();
			object = this.entityManager.merge(object);
			this.entityManager.persist(object);
			this.commitTransaction();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.rollbackTransaction();
		}
	}

	public final <T> T update(T object)
	{

		T result = object;
		try
		{
			this.beginTransaction();
			result = this.entityManager.merge(object);

			this.commitTransaction();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.rollbackTransaction();
		}

		return result;
	}

	public final void remove(Object object)
	{
		try
		{
			this.beginTransaction();
			this.entityManager.remove(object);
			this.commitTransaction();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.rollbackTransaction();
		}
	}

	protected Entry<String, Object> entry(String key, Object value)
	{
		return new SimpleEntry<String, Object>(key, value);
	}

	private Map<String, Object> parametersToMap(Map.Entry<String, Object>... parameters)
	{
		return (parameters != null) ? Collections.unmodifiableMap(Stream.of(parameters).collect(entriesToMap())) : null;
	}

	private <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> entriesToMap()
	{
		return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
	}

	public List<String> getUsers()
	{
		List<String> users = null;
		List<UserEntity> results = this.getResults(UserEntity.class);

		if (results != null)
		{
			users = results.stream().map(UserEntity::getName).collect(Collectors.toList());
		}

		return users;
	}

	public String getPassword(String user)
	{
		UserEntity result = this.getResult("getUserByName", UserEntity.class, entry("name", this.encrypt(user)));

		if (result != null)
		{
			return result.getPassword();
		}

		return null;
	}
	//
	//	final void createInitialDatabase()
	//	{
	//		try
	//		{
	//			MessageDigest md = MessageDigest.getInstance("SHA-256");
	//
	//			byte[] entropy = new byte[1024];
	//			Utils.random.nextBytes(entropy);
	//			md.update(entropy,
	//					  0,
	//					  1024);
	//			String password = new BigInteger(1,
	//											 md.digest()).toString(16).substring(0,
	//																				 12);
	//
	//			this.createTable(DatasetType.GROUP);
	//			this.createTable(DatasetType.GROUP_RIGHT);
	//			this.createTable(DatasetType.USER);
	//
	//			this.createTable(DatasetType.MODULE);
	//			this.createTable(DatasetType.MODULE_TABLE_REGISTRY);
	//
	//			Dataset group = new Dataset(DatasetType.GROUP);
	//			group.set("id",
	//					  null);
	//			group.set("name",
	//					  this.encrypt("administrators"));
	//			this.insert(group);
	//
	//			group.set("id",
	//					  null);
	//			group.set("name",
	//					  this.encrypt("users"));
	//			this.insert(group);
	//
	//			group.set("id",
	//					  null);
	//			group.set("name",
	//					  this.encrypt("inactive"));
	//			this.insert(group);
	//
	//			Dataset user = new Dataset(DatasetType.USER);
	//			user.set("id",
	//					 null);
	//			user.set("name",
	//					 "root");
	//			user.set("password",
	//					 password);
	//			user.set("group",
	//					 1);
	//			this.insert(user);
	//
	//			System.out.println("Root password is: " + password);
	//			System.out.println("SECURITY WARNING: Please change the root password later");
	//		} catch (NoSuchAlgorithmException e)
	//		{
	//			Logger.exception(LegionLogger.DATABASE,
	//							 e);
	//		}
	//	}
	//
	//	public void createTable(IDatasetType dataType)
	//	{
	//		try
	//		{
	//			this.semaphore.acquire();
	//			this.connect();
	//
	//			if(dataType != null)
	//			{
	//				Column[] columns = dataType.getColumns();
	//				if(columns != null && columns.length > 0)
	//				{
	//					Statement stat = this.conn.createStatement();
	//
	//					String sql = "CREATE TABLE IF NOT EXISTS`" + dataType.getTableName() + "` (";
	//
	//					LinkedHashMap<String, IDatasetType> foreignKeys = new LinkedHashMap<String, IDatasetType>();
	//
	//					for(int columnIndex = 0; columnIndex < columns.length; columnIndex++)
	//					{
	//						Column column = columns[columnIndex];
	//
	//						String columnName = column.getColumnName();
	//						String sqlDataType = this.getSqlTypeFromDataType(column.getColumnType());
	//						String defaultValue = column.getDefaultValue();
	//						boolean primaryKey = column.isPrimaryKey();
	//						boolean nullable = column.isNullable();
	//						IDatasetType foreignKey = (column instanceof SelfReferenceColumn) ?
	//								dataType :
	//								column.getForeignKeyReference();
	//
	//						sql += "`" + columnName + "` " + sqlDataType;
	//
	//						if(!nullable)
	//						{
	//							sql += " NOT NULL";
	//						}
	//
	//						if(defaultValue != null)
	//						{
	//							sql += " DEFAULT " + defaultValue;
	//						}
	//
	//						if(primaryKey)
	//						{
	//							sql += " PRIMARY KEY " + this.sqlDialect.autoIncrementName();
	//						}
	//
	//						if(foreignKey != null)
	//						{
	//							foreignKeys.put(columnName,
	//											foreignKey);
	//						}
	//
	//						if(columnIndex < columns.length - 1 || !foreignKeys.isEmpty())
	//						{
	//							sql += ", ";
	//						}
	//
	//					}
	//
	//					if(!foreignKeys.isEmpty())
	//					{
	//						ArrayList<String> keys = new ArrayList<String>(foreignKeys.keySet());
	//
	//						for(int keyIndex = 0; keyIndex < keys.size(); keyIndex++)
	//						{
	//							sql += "FOREIGN KEY (`" + keys.get(keyIndex) + "`) REFERENCES `"
	//									+ foreignKeys.get(keys.get(keyIndex)).getTableName()
	//									+ "`(`id`)";
	//
	//							if(keyIndex < keys.size() - 1)
	//							{
	//								sql += ", ";
	//							}
	//						}
	//					}
	//
	//					sql += ");";
	//
	//					stat.execute(sql);
	//				}
	//			}
	//		} catch (SQLException e)
	//		{
	//			Logger.exception(LegionLogger.DATABASE,
	//							 e);
	//		} catch (InterruptedException e)
	//		{
	//			Logger.exception(LegionLogger.DATABASE,
	//							 e);
	//		} finally
	//		{
	//			this.disconnect();
	//			this.semaphore.release();
	//		}
	//	}
	//
	//	public
	//
	//	public void insertColumns(IDatasetType datasetType,
	//							  String condition,
	//							  Column... columns)
	//	{
	//		if(datasetType != null && columns != null && columns.length > 0)
	//		{
	//			Statement stat;
	//			try
	//			{
	//				this.semaphore.acquire();
	//				this.connect();
	//
	//				stat = this.conn.createStatement();
	//
	//				String table = datasetType.getTableName();
	//
	//				LinkedHashMap<String, IDatasetType> foreignKeys = new LinkedHashMap<String, IDatasetType>();
	//
	//				String sql = "ALTER TABLE `" + table + "` ADD ";
	//
	//				for(int columnIndex = 0; columnIndex < columns.length; columnIndex++)
	//				{
	//					Column column = columns[columnIndex];
	//
	//					String columnName = column.getColumnName();
	//					String sqlDataType = this.getSqlTypeFromDataType(column.getColumnType());
	//					String defaultValue = column.getDefaultValue();
	//					boolean primaryKey = column.isPrimaryKey();
	//					boolean nullable = column.isNullable();
	//					IDatasetType foreignKey = (column instanceof SelfReferenceColumn) ?
	//							datasetType :
	//							column.getForeignKeyReference();
	//
	//					sql += "`" + columnName + "` " + sqlDataType;
	//
	//					if(!nullable)
	//					{
	//						sql += " NOT NULL";
	//					}
	//
	//					if(defaultValue != null)
	//					{
	//						sql += " DEFAULT " + defaultValue;
	//					}
	//
	//					if(primaryKey)
	//					{
	//						sql += " PRIMARY KEY " + this.sqlDialect.autoIncrementName();
	//					}
	//
	//					if(foreignKey != null)
	//					{
	//						foreignKeys.put(columnName,
	//										foreignKey);
	//					}
	//
	//					if(columnIndex < columns.length - 1 || !foreignKeys.isEmpty())
	//					{
	//						sql += ", ";
	//					}
	//				}
	//				if(!foreignKeys.isEmpty())
	//				{
	//					ArrayList<String> keys = new ArrayList<String>(foreignKeys.keySet());
	//
	//					for(int keyIndex = 0; keyIndex < keys.size(); keyIndex++)
	//					{
	//						sql += "FOREIGN KEY (`" + keys.get(keyIndex) + "`) REFERENCES `"
	//								+ foreignKeys.get(keys.get(keyIndex)).getTableName()
	//								+ "`(`id`)";
	//
	//						if(keyIndex < keys.size() - 1)
	//						{
	//							sql += ", ";
	//						}
	//					}
	//				}
	//
	//				if(condition != null && !condition.isEmpty())
	//				{
	//					sql += " " + condition + ";";
	//				} else
	//				{
	//					sql += ";";
	//				}
	//
	//				stat.execute(sql);
	//
	//			} catch (SQLException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} catch (InterruptedException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} finally
	//			{
	//				this.disconnect();
	//				this.semaphore.release();
	//			}
	//		}
	//	}
	//
	//	public void changeColumn(IDatasetType datasetType,
	//							 Column oldColumn,
	//							 Column newColumn)
	//	{
	//		if(datasetType != null && oldColumn != null && newColumn != null)
	//		{
	//			Statement stat;
	//			try
	//			{
	//				this.semaphore.acquire();
	//				this.connect();
	//
	//				stat = this.conn.createStatement();
	//
	//				LinkedHashMap<String, IDatasetType> foreignKeys = new LinkedHashMap<String, IDatasetType>();
	//
	//				String table = datasetType.getTableName();
	//
	//				String sql = "ALTER TABLE `" + table + "` MODIFY ";
	//				sql += "`" + oldColumn.getColumnName() + "` ";
	//
	//				String columnName = newColumn.getColumnName();
	//				String sqlDataType = this.getSqlTypeFromDataType(newColumn.getColumnType());
	//				String defaultValue = newColumn.getDefaultValue();
	//				boolean primaryKey = newColumn.isPrimaryKey();
	//				boolean nullable = newColumn.isNullable();
	//				IDatasetType foreignKey = (newColumn instanceof SelfReferenceColumn) ?
	//						datasetType :
	//						newColumn.getForeignKeyReference();
	//
	//				sql += "`" + columnName + "` " + sqlDataType;
	//
	//				if(!nullable)
	//				{
	//					sql += " NOT NULL";
	//				}
	//
	//				if(defaultValue != null)
	//				{
	//					sql += " DEFAULT " + defaultValue;
	//				}
	//
	//				if(primaryKey)
	//				{
	//					sql += " PRIMARY KEY " + this.sqlDialect.autoIncrementName();
	//				}
	//
	//				if(foreignKey != null)
	//				{
	//					foreignKeys.put(columnName,
	//									foreignKey);
	//				}
	//
	//				if(!foreignKeys.isEmpty())
	//				{
	//					ArrayList<String> keys = new ArrayList<String>(foreignKeys.keySet());
	//
	//					for(int keyIndex = 0; keyIndex < keys.size(); keyIndex++)
	//					{
	//						sql += "FOREIGN KEY (`" + keys.get(keyIndex) + "`) REFERENCES `"
	//								+ foreignKeys.get(keys.get(keyIndex)).getTableName()
	//								+ "`(`id`)";
	//
	//						if(keyIndex < keys.size() - 1)
	//						{
	//							sql += ", ";
	//						}
	//					}
	//				}
	//
	//				sql += ";";
	//
	//				stat.execute(sql);
	//			} catch (SQLException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} catch (InterruptedException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} finally
	//			{
	//				this.disconnect();
	//				this.semaphore.release();
	//			}
	//		}
	//	}
	//
	//	public void dropColumns(IDatasetType datasetType,
	//							Column... columns)
	//	{
	//		if(datasetType != null && columns != null && columns.length > 0)
	//		{
	//			Statement stat;
	//			try
	//			{
	//				this.semaphore.acquire();
	//				this.connect();
	//
	//				stat = this.conn.createStatement();
	//
	//				String table = datasetType.getTableName();
	//
	//				String sql = "ALTER TABLE `" + table + "` DROP ";
	//
	//				for(int columnIndex = 0; columnIndex < columns.length; columnIndex++)
	//				{
	//					sql += "`" + columns[columnIndex].getColumnName() + "`";
	//					if(columnIndex < columns.length - 1)
	//					{
	//						sql += ", ";
	//					}
	//				}
	//				sql += ";";
	//				stat.execute(sql);
	//			} catch (SQLException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} catch (InterruptedException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} finally
	//			{
	//				this.disconnect();
	//				this.semaphore.release();
	//			}
	//		}
	//	}
	//
	//	public void dropTable(IDatasetType datasetType)
	//	{
	//		if(datasetType != null)
	//		{
	//			Statement stat;
	//			try
	//			{
	//				this.semaphore.acquire();
	//				this.connect();
	//
	//				stat = this.conn.createStatement();
	//
	//				String table = datasetType.getTableName();
	//
	//				String sql = "DROP TABLE `" + table + "`;";
	//
	//				stat.execute(sql);
	//			} catch (SQLException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} catch (InterruptedException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} finally
	//			{
	//				this.disconnect();
	//				this.semaphore.release();
	//			}
	//		}
	//	}
	//
	//	public int insert(Dataset dataset)
	//	{
	//		if(dataset != null && dataset.getDatasetContentSize() > 0)
	//		{
	//			Statement stat;
	//			try
	//			{
	//				this.semaphore.acquire();
	//				this.connect();
	//
	//				stat = this.conn.createStatement();
	//
	//				String table = dataset.getClassType().getTableName();
	//
	//				int id = 0;
	//
	//				Map<String, Object> values = dataset.getDatabaseValues();
	//				if(values.get("id") != null && values.get("id") instanceof Integer
	//						&& (Integer) values.get("id") <= 0)
	//				{
	//					// id is 0 or below 0, get the last inserted id and increase
	//					// it by 1
	//					id = this.getLastInserted(table) + 1;
	//
	//					values.put("id",
	//							   id);
	//				}
	//				List<String> keys = new ArrayList<String>(values.keySet());
	//
	//				if(!keys.isEmpty())
	//				{
	//
	//					String sql = "INSERT INTO `" + table + "` (";
	//
	//					for(int index = 0; index < keys.size(); index++)
	//					{
	//						sql += "`" + keys.get(index) + "`"
	//								+ ((index < keys.size() - 1) ? ", " : "");
	//					}
	//					sql += ") VALUES (";
	//					for(int index = 0; index < keys.size(); index++)
	//					{
	//						String value = null;
	//						Object obj = values.get(keys.get(index));
	//
	//						if(obj instanceof String)
	//						{
	//							value = "'" + this.encrypt((String) obj) + "'";
	//						} else if(obj instanceof LocalDate)
	//						{
	//							value = ((LocalDate) obj).format(DateTimeFormatter.ISO_LOCAL_DATE);
	//						} else if(obj instanceof LocalTime)
	//						{
	//							value = ((LocalTime) obj).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	//						} else if(obj instanceof LocalDateTime)
	//						{
	//							value = ((LocalDateTime) obj).format(DATE_TIME_FORMATTER);
	//						} else if(obj instanceof Integer)
	//						{
	//							value = Integer.toString((Integer) obj);
	//						} else if(obj instanceof Long)
	//						{
	//							value = Long.toString((Long) obj);
	//						} else if(obj instanceof Boolean)
	//						{
	//							value = Boolean.toString((Boolean) obj);
	//						} else if(obj instanceof Float)
	//						{
	//							value = Float.toString((Float) obj);
	//						} else if(obj instanceof Double)
	//						{
	//							value = Double.toString((Double) obj);
	//						}
	//						if(value == null)
	//						{
	//							value = "NULL";
	//						}
	//
	//						sql += value + ((index < keys.size() - 1) ? ", " : "");
	//					}
	//
	//					sql += ");";
	//
	//					stat.executeUpdate(sql);
	//
	//					return (id <= 0) ? this.getLastInserted(table) : id;
	//
	//				}
	//			} catch (SQLException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} catch (InterruptedException e)
	//			{
	//				Logger.exception(LegionLogger.DATABASE,
	//								 e);
	//			} finally
	//			{
	//				this.disconnect();
	//				this.semaphore.release();
	//			}
	//		}
	//
	//		return 0;
	//	}
	//
	//	private int getLastInserted(String table)
	//	{
	//		Statement query;
	//
	//		try
	//		{
	//			query = this.conn.createStatement();
	//
	//			String sql = "SELECT MAX(ID) FROM " + table + ";";
	//
	//			ResultSet result = query.executeQuery(sql);
	//
	//			if(result.next())
	//			{
	//				return result.getInt(1);
	//			}
	//		} catch (SQLException e)
	//		{
	//			Logger.exception(LegionLogger.DATABASE,
	//							 e);
	//		}
	//		return 0;
	//	}
	//
	//	public <T> List<T> select(Class<T> clazz,
	//							  String rows,
	//							  String where,
	//							  String extra)
	//	{
	//		String hql = "SELECT " + rows + " FROM " + clazz.getSimpleName();
	//
	//		if(where != null && !where.isEmpty())
	//		{
	//			hql += " WHERE " + where;
	//		}
	//
	//		if(extra != null && !extra.isEmpty())
	//		{
	//			hql += " " + extra + ";";
	//		} else
	//		{
	//			hql += ";";
	//		}
	//
	//		EntityManager entityManager = connect();
	//		TypedQuery<T> query = entityManager.createQuery(hql,
	//														clazz);
	//
	//		List<T> results = query.getResultList();
	//
	//		entityManager.getTransaction().commit();
	//
	//		this.disconnect(entityManager);
	//
	//		return results;
	//	}
	//
	//	public void update(Dataset dataset,
	//					   String where)
	//	{
	//		Statement stat;
	//
	//		if(dataset.getInteger("id") == null || dataset.getInteger("id") <= 0)
	//		{
	//			throw new NullPointerException("Dataset ID is not set correctly.");
	//		}
	//
	//		try
	//		{
	//			this.semaphore.acquire();
	//			this.connect();
	//
	//			Map<String, Object> values = dataset.getDatabaseValues();
	//
	//			List<String> keys = new ArrayList<String>(values.keySet());
	//
	//			if(!keys.isEmpty())
	//			{
	//				String table = dataset.getClassType().getTableName();
	//
	//				stat = this.conn.createStatement();
	//				String sql = "UPDATE `" + table + "` SET ";
	//
	//				for(int index = 0; index < keys.size(); index++)
	//				{
	//					String key = keys.get(index);
	//					String value = null;
	//					Object obj = values.get(key);
	//
	//					if(obj instanceof String)
	//					{
	//						value = "'" + this.encrypt((String) obj) + "'";
	//					} else if(obj instanceof LocalDate)
	//					{
	//						value = ((LocalDate) obj).format(DateTimeFormatter.ISO_LOCAL_DATE);
	//					} else if(obj instanceof LocalTime)
	//					{
	//						value = ((LocalTime) obj).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	//					} else if(obj instanceof LocalDateTime)
	//					{
	//						value = ((LocalDateTime) obj).format(DATE_TIME_FORMATTER);
	//					} else if(obj instanceof Integer)
	//					{
	//						value = Integer.toString((Integer) obj);
	//					} else if(obj instanceof Long)
	//					{
	//						value = Long.toString((Long) obj);
	//					} else if(obj instanceof Boolean)
	//					{
	//						value = Integer.toString((boolean) obj ? 1 : 0);
	//					} else if(obj instanceof Float)
	//					{
	//						value = Float.toString((Float) obj);
	//					} else if(obj instanceof Double)
	//					{
	//						value = Double.toString((Double) obj);
	//					}
	//					if(value == null)
	//					{
	//						value = "NULL";
	//					}
	//
	//					sql += key + " = " + value + ((index < keys.size() - 1) ? ", " : "");
	//				}
	//
	//				if(where != null && !where.isEmpty())
	//				{
	//					sql += " WHERE " + where;
	//				} else
	//				{
	//					sql += " WHERE id = " + dataset.getInteger("id");
	//				}
	//
	//				stat.executeUpdate(sql);
	//			}
	//		} catch (SQLException e)
	//		{
	//			Logger.exception(LegionLogger.DATABASE,
	//							 e);
	//		} catch (InterruptedException e)
	//		{
	//			Logger.exception(LegionLogger.DATABASE,
	//							 e);
	//		} finally
	//		{
	//			this.disconnect();
	//			this.semaphore.release();
	//		}
	//	}
	//
	//	public void delete(IDatasetType type,
	//					   String where)
	//	{
	//		Statement stat;
	//
	//		try
	//		{
	//			this.semaphore.acquire();
	//			this.connect();
	//
	//			stat = this.conn.createStatement();
	//
	//			stat.executeUpdate("DELETE FROM `" + type.getTableName() + "` WHERE " + where);
	//
	//		} catch (SQLException e)
	//		{
	//			Logger.exception(LegionLogger.DATABASE,
	//							 e);
	//		} catch (InterruptedException e)
	//		{
	//			Logger.exception(LegionLogger.DATABASE,
	//							 e);
	//		} finally
	//		{
	//			this.disconnect();
	//			this.semaphore.release();
	//		}
	//	}
	//
	//	public void delete(Dataset dataset)
	//	{
	//		if(dataset != null)
	//		{
	//			this.delete(dataset.getClassType(),
	//						"id = " + dataset.getInteger("id"));
	//		}
	//	}
	//
	//	public String getSqlTypeFromDataType(DataType dataType)
	//	{
	//		if(this.sqlDialect != null)
	//		{
	//			switch(dataType)
	//			{
	//				case BLOB:
	//					return this.sqlDialect.blobName();
	//				case BOOL:
	//					return this.sqlDialect.boolName();
	//				case DATE:
	//					return this.sqlDialect.dateName();
	//				case TIME:
	//					return this.sqlDialect.timeName();
	//				case DATE_TIME:
	//					return this.sqlDialect.dateTimeName();
	//				case DOUBLE:
	//					return this.sqlDialect.doubleName();
	//				case FLOAT:
	//					return this.sqlDialect.floatName();
	//				case INTEGER:
	//					return this.sqlDialect.integerName();
	//				case STRING:
	//					return this.sqlDialect.stringName();
	//			}
	//		}
	//		return null;
	//	}
	//
	//	DataType getDataTypeFromOfColumnName(IDatasetType datasetType,
	//										 String columnName)
	//	{
	//		if(datasetType != null && columnName != null)
	//		{
	//			Column[] columns = datasetType.getColumns();
	//
	//			if(columns != null)
	//			{
	//				for(Column column : columns)
	//				{
	//					if(columnName.equals(column.getColumnName()))
	//					{
	//						return column.getColumnType();
	//					}
	//				}
	//			}
	//		}
	//		return null;
	//	}
	//
	//	public ISqlDialect getSqlDialect()
	//	{
	//		return this.sqlDialect;
	//	}
	//
	//	public void setGroupRight(int groupId,
	//							  IRight right,
	//							  boolean active)
	//	{
	//		Dataset rightDataset;
	//		List<Dataset> rightDatasets = this
	//				.select(DatasetType.GROUP_RIGHT,
	//						"*",
	//						"name ='" + right.getName() + "'",
	//						null,
	//						false);
	//
	//		if(rightDatasets != null)
	//		{
	//			if(rightDatasets.size() > 1)
	//			{
	//				rightDatasets.forEach(this::delete);
	//			} else if(rightDatasets.size() == 1)
	//			{
	//				rightDataset = rightDatasets.get(0);
	//				rightDataset.set("active",
	//								 active);
	//				this.update(rightDataset,
	//							null);
	//				return;
	//			}
	//		}
	//		rightDataset = new Dataset(DatasetType.GROUP_RIGHT);
	//		rightDataset.set("id",
	//						 null);
	//		rightDataset.set("name",
	//						 right.getName());
	//		rightDataset.set("active",
	//						 true);
	//		rightDataset.set("group_id",
	//						 groupId);
	//		this.insert(rightDataset);
	//	}
}
