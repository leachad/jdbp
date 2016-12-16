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

import db.JdbpConnectionManager;
import exception.JdbpException;

/**
 * JdbpDriverManager initializes all key-value pairs outlined by implementing code in jdbp.properties. Authenticated access will only be accessible if
 * the client code defines the credentials in jdbp.properties. Additionally, any properties the implementing code requires must be identified in
 * jdbp.properties.
 * 
 * @since 12.2.2016
 * @author andrew.leach
 */
public class JdbpDriverManager {

	private static Driver driver;
	private static String url;
	private static Map<String, String> urlParamArgPairs;
	private static String username;
	private static String password;
	private static Properties info;
	private static Map<String, String> constructedUrlsForSchema = new HashMap<>();

	private JdbpDriverManager() {
		// private do nothing constructor to hide the implicit constructor
	}

	/**
	 * @param driver
	 * @param driverAction
	 * @throws JdbpException
	 */
	public static void registerDriver(DriverAction driverAction) throws JdbpException {
		try {
			DriverManager.registerDriver(driver, driverAction);
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
	}

	/**
	 * @throws JdbpException
	 */
	public static void registerDriver() throws JdbpException {
		JdbpDriverManager.registerDriver(null);
	}

	/**
	 * @param schemaName
	 * @return a connection
	 * @throws JdbpException
	 */
	public static Connection getConnection(String schemaName) throws JdbpException {
		return getValidConnection(schemaName);
	}

	private static Connection getValidConnection(String schemaName) throws JdbpException {
		Connection connection = null;
		if(url == null) {
			JdbpException.throwException("database url cannot be null");
		}
		if(schemaName != null) {
			url = appendSchemaName(schemaName);
		}
		if(urlParamArgPairs != null) {
			url = appendUrlArgs();
		}
		updateConstructedUrlForSchema(schemaName);

		if(userCredentialsProvided() && !propertiesInfoProvided()) {
			connection = JdbpConnectionManager.getConnection(schemaName, username, password);
		}
		else if(!userCredentialsProvided() && propertiesInfoProvided()) {
			connection = JdbpConnectionManager.getConnection(schemaName, info);
		}
		else if(!userCredentialsProvided() && !propertiesInfoProvided()) {
			connection = JdbpConnectionManager.getConnection(schemaName);
		}

		return connection;
	}

	private static String appendUrlArgs() {
		StringBuilder sb = new StringBuilder(url + "?");
		int argIndex = 1;
		for(Entry<String, String> paramArgPair: urlParamArgPairs.entrySet()) {
			sb.append(paramArgPair.getKey() + "=" + paramArgPair.getValue());
			if(argIndex >= 1 && argIndex < urlParamArgPairs.size() && argIndex != urlParamArgPairs.size()) {
				sb.append("&");
			}
			argIndex++;
		}
		return sb.toString();
	}

	private static String appendSchemaName(String schemaName) {
		if(url.charAt(url.length() - 1) != 0x2F) {
			url = url + 0x2F;
		}
		return url + schemaName;
	}

	private static boolean userCredentialsProvided() {
		return username != null && password != null;
	}

	private static boolean propertiesInfoProvided() {
		return info != null;
	}

	/**
	 * Currently only a one to one mapping for schema names and constructed urls
	 * 
	 * @param schemaName
	 */
	private static void updateConstructedUrlForSchema(String schemaName) {
		if(url != null) {
			constructedUrlsForSchema.put(schemaName, url);
		}
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
	 * @param schemaName
	 * @return constructed url for schema name, else, available url field in JdbpDriverManager
	 */
	public static String getUrlForSchemaName(String schemaName) {
		return constructedUrlsForSchema.get(schemaName) != null ? constructedUrlsForSchema.get(schemaName) : url;
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
