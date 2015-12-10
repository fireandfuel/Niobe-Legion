package niobe.legion.server.module;

import niobe.legion.server.AbstractDatabase;
import niobe.legion.shared.module.IModule;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
public interface IServerModule extends IModule
{
	/**
	 * For checking if database is not in use my other modules
	 *
	 * @return name of the module's database
	 */
	String getDatabaseName();

	/**
	 * Initialize the module's database after database checks are done
	 * <p>
	 * This method is only for calling by the server, do not call it on your own.
	 * <p>
	 * You are not allowed to use the hibernate.archive.autodetection property in your hibernate persistence
	 * configuration!
	 *
	 * @return the database of the module
	 */
	AbstractDatabase initDatabase();
}
