/**
 * 
 */
package com.andrewdleach.jdbp.driver;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;

import com.andrewdleach.jdbp.properties.info.DriverPropertiesInfo;

/**
 * Utility to obtain the com.andrewdleach.jdbp.driver in the classpath, check if it is compatible against the predefined list of drivers and register
 * it with the com.andrewdleach.jdbp.driver manager
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class DriverLocator {

	private static final String REQUESTED_DRIVER_NAME = "requestedDriverName";
	private static final String LOAD_BALANCED = "loadBalanced";
	private static final String HOST_NAMES = "hostNames";
	private static final String PORT_NUMBERS = "portNumbers";
	private static final String SCHEMA_NAMES = "schemaNames";
	private static final String URL_PARAMS = "urlParams";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String PROP_DEFINED_STATEMENTS = "propDefinedStatements";
	private static final String DB_DEFINED_STATEMENTS = "dbDefinedStatements";
	
	private static Map<String, DriverPropertiesInfo> definedDriverMap = new HashMap<>();

	protected DriverLocator() {}

	/**
	 * Find the appropriate jdbc com.andrewdleach.jdbp.driver based on property files and availability in the classpath
	 */
	public static void findJdbcDrivers() {
		ResourceBundle jdbpProps = ResourceBundle.getBundle("resources.jdbp", Locale.getDefault());
		Set<String> keySet = jdbpProps.keySet();
		for(String key: keySet) {
			String driverProperties = jdbpProps.getString(key);
			String[] driverAndProperty = key.split("[.]");
			String driverName = driverAndProperty[0];
			String propertyName = driverAndProperty[1];
			addDriverPropertyToDriverMap(driverName, propertyName, driverProperties);
		}	
	}
	
	public static Map<String, DriverPropertiesInfo> getDefinedDriverMap() {
		return definedDriverMap;
	}

	private static void addDriverPropertyToDriverMap(String driverName, String propertyName, String driverProperties) {
			DriverPropertiesInfo driverInfo = definedDriverMap.get(driverName);
			if (driverInfo == null) {
				driverInfo = new DriverPropertiesInfo();
				definedDriverMap.put(driverName, driverInfo);
			}
			if(propertyName.equals(REQUESTED_DRIVER_NAME)) {
				driverInfo.setRequestedDriverName(driverProperties);
				Driver driver = DriverLocator.locateDriver(driverProperties);
				driverInfo.setDriver(driver);
			}
			else if(propertyName.equals(LOAD_BALANCED)) {
				driverInfo.setLoadBalanced(Boolean.getBoolean(driverProperties) ? Boolean.getBoolean(driverProperties) : false);
			}
			else if(propertyName.equals(HOST_NAMES)) {
				List<String> hostNames = getHostNames(driverProperties);
				driverInfo.setHostNames(hostNames);
			}
			else if(propertyName.equalsIgnoreCase(PORT_NUMBERS)) {
				driverInfo.setPortNumbers(getPortNumbers(driverProperties));
			}
			else if(propertyName.equals(SCHEMA_NAMES)) {
				driverInfo.setSchemaNames(getSchemaNames(driverProperties));
			}
			else if(propertyName.equals(URL_PARAMS)) {
				driverInfo.setUrlParams(getUrlParams(driverProperties));
			}
			else if(propertyName.equals(USERNAME)) {
				driverInfo.setUserName(driverProperties);
			}
			else if(propertyName.equals(PASSWORD)) {
				driverInfo.setPassword(driverProperties.toCharArray());
			}
			else if(propertyName.equals(PROP_DEFINED_STATEMENTS)) {
				driverInfo.setPropDefinedStatements(Boolean.getBoolean(driverProperties));
			}
			else if(propertyName.equals(DB_DEFINED_STATEMENTS)) {
				driverInfo.setDbDefinedStatements(Boolean.getBoolean(driverProperties));
			}
		
		if(driverInfo.getDriver() == null) {
			Driver driver = DriverLocator.locateDriver();
			driverInfo.setDriver(driver);
		}
	}
	
	/**
	 * @param urlParams
	 */
	public static Map<String, String> getUrlParams(String urlParams) {
		if(urlParams != null && urlParams.length() > 0) {
			String[] urlParamArray = urlParams.split("[,]");
			Map<String, String> urlParamArgPairs = new HashMap<>();
			for(String urlParamArgPair: urlParamArray) {
				String[] splitPair = urlParamArgPair.split("[=]");
				urlParamArgPairs.put(splitPair[0], splitPair[1]);
			}
			return urlParamArgPairs;
		}
		return null;
	}

	/**
	 * Utility method to locate the first valid JDBC com.andrewdleach.jdbp.driver in the classPath
	 * 
	 * @return the validDriver instance
	 */
	private static Driver locateDriver() {
		ServiceLoader<java.sql.Driver> sqlDriverLoader = ServiceLoader.load(java.sql.Driver.class);
		Iterator<java.sql.Driver> iterator = sqlDriverLoader != null ? sqlDriverLoader.iterator() : null;
		Driver validDriver = iterator != null ? (iterator.hasNext() ? iterator.next() : null) : null;
		return (validDriver != null && validDriver.jdbcCompliant()) ? validDriver : null;
	}

	/**
	 * Utility method to locate the requested com.andrewdleach.jdbp.driver by driverName if you have more than one com.andrewdleach.jdbp.driver in the
	 * classpath supporting the same JDBC protocol(s)
	 * 
	 * @param driverName
	 * @return the requestedDriver instance
	 */
	private static Driver locateDriver(String driverName) {
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

	private static List<Integer> getPortNumbers(String portNumbersPropertyString) {
		String[] portNumberArray = portNumbersPropertyString.split("[,]");
		List<Integer> portNumbers = new ArrayList<>();
		for(String portNumber: portNumberArray) {
			portNumbers.add(Integer.parseInt(portNumber));
		}
		return portNumbers;
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
