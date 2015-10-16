package niobe.legion.server;

import niobe.legion.shared.Base64;
import niobe.legion.shared.Utils;
import niobe.legion.shared.data.IRight;
import niobe.legion.shared.data.LegionRight;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.GroupEntity;
import niobe.legion.shared.model.GroupRightEntity;
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
import javax.persistence.criteria.CriteriaQuery;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

	@PersistenceUnit
	EntityManagerFactory entityManagerFactory;
	EntityManager     entityManager;
	EntityTransaction entityTransaction;

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
			properties.put("hibernate.connection.url", "jdbc:h2:file:./" + database +
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

		// Check if database have users
		List<String> users = this.getUsers();
		if (users == null || users.isEmpty())
		{
			this.createInitialUsers();
		}
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
		CriteriaQuery criteriaQuery = this.entityManager.getCriteriaBuilder().createQuery(clazz);
		criteriaQuery.from(clazz);

		return this.getResults(this.entityManager.createQuery(criteriaQuery), null);
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

	public final Object insert(Object object)
	{
		try
		{
			this.beginTransaction();
			// object may contain a list or array of objects which are already known to entityManager
			object = this.entityManager.merge(object);
			this.entityManager.persist(object);
			this.commitTransaction();
			return object;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.rollbackTransaction();
		}
		return null;
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
		UserEntity result = this.getResult("user.getByName", UserEntity.class, entry("name", this.encrypt(user)));

		if (result != null)
		{
			return result.getPassword();
		}

		return null;
	}

	public UserEntity getUser(String name)
	{
		return this.getResult("user.getByName", UserEntity.class, entry("name", this.encrypt(name)));
	}


	final void createInitialUsers()
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte[] entropy = new byte[1024];
			Utils.random.nextBytes(entropy);
			md.update(entropy, 0, 1024);
			String password = new BigInteger(1, md.digest()).toString(16).substring(0, 12);

			GroupEntity group = new GroupEntity();
			group.setName(this.encrypt("users"));
			group.setActive(true);
			group.setRights(groupRightsFor(LegionRight.USER_RIGHT, LegionRight.LOGIN));
			this.insert(group);

			group = new GroupEntity();
			group.setName(this.encrypt("inactive"));
			this.insert(group);

			GroupEntity admins = new GroupEntity();
			admins.setName(this.encrypt("administrators"));
			admins.setActive(true);
			admins.setRights(groupRightsFor(LegionRight.USER_RIGHT,
											LegionRight.LOGIN,
											LegionRight.ADMINISTRATION,
											LegionRight.SERVER_ADMINISTRATION,
											LegionRight.STOP_SERVER,
											LegionRight.RESTART_SERVER,
											LegionRight.USER_ADMINISTRATION,
											LegionRight.ADD_USER,
											LegionRight.RENAME_USER,
											LegionRight.DELETE_USER,
											LegionRight.SET_USER_PASSWORD,
											LegionRight.SET_USER_GROUP,
											LegionRight.GROUP_ADMINISTRATION,
											LegionRight.ADD_GROUP,
											LegionRight.RENAME_GROUP,
											LegionRight.DELETE_GROUP,
											LegionRight.RIGHT_ADMINISTRATION,
											LegionRight.SET_RIGHT,
											LegionRight.UNSET_RIGHT));

			UserEntity user = new UserEntity();
			user.setName("root");
			user.setPassword(password);
			user.setGroup(admins);
			this.insert(user);

			System.out.println("Root password is: " + password);
			System.out.println("SECURITY WARNING: Please change the root password later");
		}
		catch (NoSuchAlgorithmException e)
		{
			Logger.exception(LegionLogger.DATABASE, e);
		}
	}

	private List<GroupRightEntity> groupRightsFor(IRight... rights)
	{
		if (rights != null && rights.length > 0)
		{
			return Stream.of(rights).map(this::createRight).collect(Collectors.toList());
		}
		return null;
	}

	private GroupRightEntity createRight(IRight right)
	{
		GroupRightEntity entity = new GroupRightEntity();
		entity.setName(right.getName());
		entity.setActive(true);
		return entity;
	}
}
