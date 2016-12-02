package driver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverAction;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import exception.JdbpDriverException;

public class JdbpDriverManager {

	private static Driver driver;
	private static String url;
	private static Map<String, String> urlParamArgPairs;
	private static String username;
	private static String password;
	private static Properties info;

	/**
	 * @param driver
	 * @param driverAction
	 * @throws JdbpDriverException
	 */
	public static void registerDriver(DriverAction driverAction) throws JdbpDriverException {
		try {
			DriverManager.registerDriver(driver, driverAction);
		}
		catch(SQLException e) {
			JdbpDriverException.throwException(e);
		}
	}

	/**
	 * @throws JdbpDriverException
	 */
	public static void registerDriver() throws JdbpDriverException {
		JdbpDriverManager.registerDriver(null);
	}

	/**
	 * @param url
	 * @return a connection
	 * @throws JdbpDriverException
	 */
	public static Connection getConnection(String url) throws JdbpDriverException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url);
		}
		catch(SQLException e) {
			JdbpDriverException.throwException(e);
		}
		return connection;
	}

	/**
	 * No-argument version of the method that uses static fields to return a connection if they exist
	 * 
	 * @return a connection
	 */
	public static Connection getConnection() throws JdbpDriverException {
		return getValidConnection();
	}

	/**
	 * @param url
	 * @param user
	 * @param password
	 * @return a connection
	 * @throws JdbpDriverException
	 */
	public static Connection getConnection(String url, String user, String password) throws JdbpDriverException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, user, password);
		}
		catch(SQLException e) {
			JdbpDriverException.throwException(e);
		}
		return connection;
	}

	/**
	 * @param url
	 * @param info
	 * @return a connection
	 * @throws JdbpDriverException
	 */
	public static Connection getConnection(String url, Properties info) throws JdbpDriverException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, info);
		}
		catch(SQLException e) {
			JdbpDriverException.throwException(e);
		}
		return connection;
	}

	private static Connection getValidConnection() throws JdbpDriverException {
		Connection connection = null;
		if(url == null) {
			JdbpDriverException.throwException("database url cannot be null");
		}
		if(urlParamArgPairs != null) {
			url = appendUrlArgs();
		}
		if(userCredentialsProvided() && !propertiesInfoProvided()) {
			connection = JdbpDriverManager.getConnection(url, username, password);
		}
		else if(!userCredentialsProvided() && propertiesInfoProvided()) {
			connection = JdbpDriverManager.getConnection(url, info);
		}
		else if(!userCredentialsProvided() && !propertiesInfoProvided()) {
			connection = JdbpDriverManager.getConnection(url);
		}

		return connection;
	}

	private static String appendUrlArgs() {
		StringBuilder sb = new StringBuilder(url + "?");
		int argIndex = 0;
		for(Entry<String, String> paramArgPair: urlParamArgPairs.entrySet()) {
			sb.append(paramArgPair.getKey() + "=" + paramArgPair.getValue());
			if(argIndex > 0 || argIndex < urlParamArgPairs.size()) {
				sb.append("&");
			}
			argIndex++;
		}
		return sb.toString();
	}

	private static boolean userCredentialsProvided() {
		return username != null && password != null;
	}

	private static boolean propertiesInfoProvided() {
		return info != null;
	}

	/**
	 * @param driver
	 */
	public static void setDriver(Driver driver) {
		JdbpDriverManager.driver = driver;
	}

	/**
	 * @param url
	 */
	public static void setUrl(String url) {
		JdbpDriverManager.url = url;
	}

	/**
	 * @param urlParams
	 */
	public static void setUrlParams(String urlParams) {
		String[] urlParamArray = urlParams.split("[,]");
		urlParamArgPairs = new HashMap<>();
		for(String urlParamArgPair: urlParamArray) {
			String[] splitPair = urlParamArgPair.split("[=]");
			urlParamArgPairs.put(splitPair[0], splitPair[1]);
		}
	}

	/**
	 * @param username
	 */
	public static void setUserName(String username) {
		JdbpDriverManager.username = username;
	}

	/**
	 * @param password
	 */
	public static void setPassword(String password) {
		JdbpDriverManager.password = password;
	}

}
