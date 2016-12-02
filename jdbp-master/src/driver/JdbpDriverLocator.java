/**
 * 
 */
package driver;

import java.sql.Driver;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Utility to obtain the driver in the classpath, check if it is compatible against the predefined list of drivers and register it with the driver
 * manager
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class JdbpDriverLocator {

	private static final String REQUESTED_DRIVER_NAME = "requestedDriverName";
	private static final String URL = "url";
	private static final String URL_PARAMS = "urlParams";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static Driver driver = null;

	private JdbpDriverLocator() {
		// private constructor to hide the default public constructor
	}

	/**
	 * Find the appropriate jdbc driver based on property files and availability in the classpath
	 */
	public static void findJdbcDriver() {
		ResourceBundle jdbpProps = ResourceBundle.getBundle("resources.jdbp", Locale.getDefault());
		Set<String> keySet = jdbpProps.keySet();
		for(String key: keySet) {
			if(key.equals(REQUESTED_DRIVER_NAME)) {
				driver = JdbpDriverLocator.locateDriver(jdbpProps.getString(key));
			}
			else if(key.equals(URL)) {
				JdbpDriverManager.setUrl(jdbpProps.getString(key));
			}
			else if(key.equals(URL_PARAMS)) {
				JdbpDriverManager.setUrlParams(jdbpProps.getString(key));
			}
			else if(key.equals(USERNAME)) {
				JdbpDriverManager.setUserName(jdbpProps.getString(key));
			}
			else if(key.equals(PASSWORD)) {
				JdbpDriverManager.setPassword(jdbpProps.getString(key));
			}
		}
		if(driver == null) {
			driver = locateDriver();
		}
	}

	/**
	 * Locates the first valid driver in the classPath
	 * 
	 * @return
	 */
	public static Driver locateDriver() {
		ServiceLoader<java.sql.Driver> sqlDriverLoader = ServiceLoader.load(java.sql.Driver.class);
		Driver validDriver = sqlDriverLoader != null ? sqlDriverLoader.iterator().next() : null;
		return (validDriver != null && validDriver.jdbcCompliant()) ? validDriver : null;
	}

	/**
	 * Used if your application has more than one driver supporting the same protocol
	 * 
	 * @param driverName
	 * @return
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

	/**
	 * @param driverName
	 * @param clazz
	 * @return drivers equivalency as determined requested driver name
	 */
	private static boolean isDriverEquivalent(String driverName, Class<? extends Driver> clazz) {
		Class<?> driverClass = clazz.getClass();
		String className = driverClass.getName().toLowerCase();
		return className.toLowerCase().contains(driverName);
	}
}
