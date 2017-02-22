/**
 * 
 */
package jdbp;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;

import jdbp.db.driver.DriverStorage;
import jdbp.db.host.HostManager;

/**
 * Utility to obtain the jdbp.db.driver in the classpath, check if it is compatible against the predefined list of drivers and register it with the
 * jdbp.db.driver manager
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
class DriverLocator {

	private static final String REQUESTED_DRIVER_NAME = "requestedDriverName";
	private static final String LOAD_BALANCED = "loadBalanced";
	private static final String HOST_NAMES = "hostNames";
	private static final String SCHEMA_NAMES = "schemaNames";
	private static final String URL_PARAMS = "urlParams";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String PROP_DEFINED_STATEMENTS = "propDefinedStatements";
	private static final String DB_DEFINED_STATEMENTS = "dbDefinedStatements";

	private static Driver driver = null;

	public DriverLocator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Find the appropriate jdbc jdbp.db.driver based on property files and availability in the classpath
	 */
	public static void findJdbcDriver() {
		ResourceBundle jdbpProps = ResourceBundle.getBundle("resources.jdbp", Locale.getDefault());
		Set<String> keySet = jdbpProps.keySet();
		for(String key: keySet) {
			if(key.equals(REQUESTED_DRIVER_NAME)) {
				String requestedDriverName = jdbpProps.getString(key);
				driver = DriverLocator.locateDriver(requestedDriverName);
				DriverStorage.setRequestedDriverName(requestedDriverName);
			}
			else if(key.equals(LOAD_BALANCED)) {
				SchemaManager.setLoadBalanced(jdbpProps.getString(key));
			}
			else if(key.equals(HOST_NAMES)) {
				List<String> hostNames = getHostNames(jdbpProps.getString(key));
				HostManager.setHostNames(hostNames);
			}
			else if(key.equals(SCHEMA_NAMES)) {
				List<String> schemaNames = getSchemaNames(jdbpProps.getString(key));
				SchemaManager.setSchemaNames(schemaNames);
			}
			else if(key.equals(URL_PARAMS)) {
				SchemaManager.setUrlParams(jdbpProps.getString(key));
			}
			else if(key.equals(USERNAME)) {
				SchemaManager.setUserName(jdbpProps.getString(key));
			}
			else if(key.equals(PASSWORD)) {
				SchemaManager.setPassword(jdbpProps.getString(key));
			}
			else if(key.equals(PROP_DEFINED_STATEMENTS)) {
				SchemaManager.setPropDefinedStatements(jdbpProps.getString(key));
			}
			else if(key.equals(DB_DEFINED_STATEMENTS)) {
				SchemaManager.setDbDefinedStatements(jdbpProps.getString(key));
			}
		}
		if(driver == null) {
			driver = DriverLocator.locateDriver();
		}
	}

	/**
	 * Utility method to locate the first valid JDBC jdbp.db.driver in the classPath
	 * 
	 * @return the validDriver instance
	 */
	public static Driver locateDriver() {
		ServiceLoader<java.sql.Driver> sqlDriverLoader = ServiceLoader.load(java.sql.Driver.class);
		Driver validDriver = sqlDriverLoader != null ? sqlDriverLoader.iterator().next() : null;
		return (validDriver != null && validDriver.jdbcCompliant()) ? validDriver : null;
	}

	/**
	 * Utility method to locate the requested jdbp.db.driver by driverName if you have more than one jdbp.db.driver in the classpath supporting the
	 * same JDBC protocol(s)
	 * 
	 * @param driverName
	 * @return the requestedDriver instance
	 */
	public static Driver locateDriver(String driverName) {
		ServiceLoader<java.sql.Driver> sqlDriverLoader = ServiceLoader.load(java.sql.Driver.class);
		Driver requestedDriver = null;
		for(Driver driver: sqlDriverLoader) {
			if(driver.jdbcCompliant() && isDriverEquivalent(driverName, driver.getClass())) {
				requestedDriver = driver;
				break;
			}
		}
		if(requestedDriver == null) {
			requestedDriver = locateDriver();
		}
		return requestedDriver;
	}

	private static boolean isDriverEquivalent(String driverName, Class<? extends Driver> clazz) {
		Class<?> driverClass = clazz.getClass();
		String className = driverClass.getName().toLowerCase();
		return className.toLowerCase().contains(driverName);
	}

	private static List<String> getHostNames(String hostNamesPropertyString) {
		String[] hostNameArray = hostNamesPropertyString.split("[,]");
		List<String> hostNames = new ArrayList<>();
		for(String hostName: hostNameArray) {
			hostNames.add(hostName);
		}
		return hostNames;
	}

	private static List<String> getSchemaNames(String schemaNamesPropertyString) {
		String[] schemaNameArray = schemaNamesPropertyString.split("[,]");
		List<String> schemaNames = new ArrayList<>();
		for(String schemaName: schemaNameArray) {
			schemaNames.add(schemaName);
		}
		return schemaNames;
	}
}
