package cuina.legion.server;

public interface ISqlDialect
{
	/**
	 * 1 bit boolean
	 * 
	 * @return
	 */
	public String boolName();

	/**
	 * 32 bit integer
	 * 
	 * @return
	 */
	public String integerName();

	/**
	 * 64 bit integer
	 * 
	 * @return
	 */
	public String longName();

	/**
	 * 32 bit float
	 * 
	 * @return
	 */
	public String floatName();

	/**
	 * 64 bit double
	 * 
	 * @return
	 */
	public String doubleName();

	/**
	 * string
	 * 
	 * @return
	 */
	public String stringName();

	/**
	 * date
	 * 
	 * @return
	 */
	public String dateName();

	/**
	 * binary large object
	 * 
	 * @return
	 */
	public String blobName();

	/**
	 * auto increment, for table creation
	 * 
	 * @return
	 */
	public String autoIncrementName();
}
