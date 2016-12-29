package db.driver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverAction;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import db.connection.IndexedPoolableConnection;
import db.connection.JdbpConnectionManager;
import db.host.JdbpHostManager;
import db.properties.util.JdbpDriverUtil;
import db.schema.JdbpSchemaManager;
import db.schema.SchemaContainer;
import db.statement.StatementContainer;
import db.statement.StatementUtil;
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
	private static Map<String, String> urlParamArgPairs;
	private static String username;
	private static String password;
	private static Properties info;
	private static boolean loadBalanced;
	private static String requestedDriverName;
	private static boolean propDefinedStatements;
	private static boolean dbDefinedStatements;

	private JdbpDriverManager() {
		// private do nothing constructor to hide the implicit constructor
	}

	/**
	 * @param db.driver
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
	 * @return an IndexedPoolableConnection instance
	 * @throws JdbpException
	 */
	public static IndexedPoolableConnection getConnection(String schemaName) throws JdbpException {
		return getValidConnection(schemaName);
	}

	/**
	 * @param connection
	 * @param schemaName
	 * @throws JdbpException
	 */
	public static void releaseConnection(Connection connection, String schemaName) throws JdbpException {
		JdbpConnectionManager.releaseConnection(connection, schemaName);
	}

	/**
	 * @param schemaName
	 * @return a fully built schemaContainer
	 */
	public static SchemaContainer buildSchemaContainerFromProperties(String schemaName) throws JdbpException {
		if(JdbpHostManager.getHostNames() == null) {
			JdbpException.throwException("database hostName cannot be null");
		}
		SchemaContainer schemaContainer = null;
		if(schemaName == null) {
			JdbpException.throwException(new IllegalArgumentException("schemaName must not be null"));
		}
		else {
			schemaContainer = new SchemaContainer(schemaName);
			StringBuilder targetUrlBuilder = new StringBuilder();
			appendDriverClassAndUrlScheme(targetUrlBuilder);
			appendHostName(targetUrlBuilder);
			appendSchemaName(targetUrlBuilder, schemaName);
			appendUrlArgs(targetUrlBuilder);
			schemaContainer.setTargetUrl(targetUrlBuilder.toString());
			if(userCredentialsProvided() && !propertiesInfoProvided()) {
				schemaContainer.setUserName(username);
				schemaContainer.setPassword(password);
				schemaContainer.setCredentialsNoProperties(true);
			}
			else if(!userCredentialsProvided() && propertiesInfoProvided()) {
				schemaContainer.setPropertiesInfo(info);
				schemaContainer.setPropertiesNoCredentials(true);
			}
			else if(!userCredentialsProvided() && !propertiesInfoProvided()) {
				schemaContainer.setNoPropertiesNoCredentials(true);
			}
			if(propDefinedStatements || dbDefinedStatements) {
				List<StatementContainer> statements = null;
				if(propDefinedStatements) {
					statements = StatementUtil.constructStatementContainersWithResourceBundle(schemaName);
				}
				else {
					statements = StatementUtil.constructStatementContainersWithClientTable();
				}
				schemaContainer.setAvailableStatements(statements);
			}
		}
		return schemaContainer;
	}

	private static IndexedPoolableConnection getValidConnection(String schemaName) throws JdbpException {
		SchemaContainer schemaContainer = JdbpSchemaManager.fetchDB(schemaName);
		if(schemaContainer == null) {
			schemaContainer = buildSchemaContainerFromProperties(schemaName);
		}
		return JdbpConnectionManager.getConnection(schemaContainer);
	}

	private static void appendDriverClassAndUrlScheme(StringBuilder targetUrlBuilder) {
		String connectionString = JdbpDriverUtil.getDriverClassFlagForDriverName(requestedDriverName);
		if(loadBalanced && JdbpDriverUtil.isLoadBalancedSupportedForDriverName(requestedDriverName)) {
			connectionString = connectionString + JdbpDriverUtil.getLoadBalancedFlagForDriverName(requestedDriverName);
		}
		// TODO add in additional cases for replication, etc...
		targetUrlBuilder.append(connectionString);
		targetUrlBuilder.append("//");
	}

	private static void appendHostName(StringBuilder targetUrlBuilder) {
		List<String> hostNames = JdbpHostManager.getHostNames();
		int commaIndex = 0;
		for(String hostName: hostNames) {
			targetUrlBuilder.append(hostName);
			if(commaIndex < hostNames.size() - 1) {
				targetUrlBuilder.append(",");
				commaIndex++;
			}
			else {
				targetUrlBuilder.append("/");
			}
		}
	}

	private static void appendSchemaName(StringBuilder targetUrlBuilder, String schemaName) {
		String formattedHostName = targetUrlBuilder.toString();
		if(formattedHostName.charAt(formattedHostName.length() - 1) != 0x2F) {
			targetUrlBuilder.append("/");
		}
		targetUrlBuilder.append(schemaName);
	}

	private static void appendUrlArgs(StringBuilder targetUrlBuilder) {
		targetUrlBuilder.append("?");
		int argIndex = 1;
		for(Entry<String, String> paramArgPair: urlParamArgPairs.entrySet()) {
			targetUrlBuilder.append(paramArgPair.getKey() + "=" + paramArgPair.getValue());
			if(argIndex >= 1 && argIndex < urlParamArgPairs.size() && argIndex != urlParamArgPairs.size()) {
				targetUrlBuilder.append("&");
			}
			argIndex++;
		}
	}

	private static boolean userCredentialsProvided() {
		return username != null && password != null;
	}

	private static boolean propertiesInfoProvided() {
		return info != null;
	}

	/**
	 * @param db.driver
	 */
	public static void setDriver(Driver driver) {
		JdbpDriverManager.driver = driver;
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

	public static void setLoadBalanced(String isLoadBalanced) {
		if(Boolean.getBoolean(isLoadBalanced)) {
			JdbpDriverManager.loadBalanced = Boolean.getBoolean(isLoadBalanced);
		}
	}

	public static void setRequestedDriverName(String requestedDriverName) {
		JdbpDriverManager.requestedDriverName = requestedDriverName;
	}

	public static void setPropDefinedStatements(String propDefinedStatements) {
		if(Boolean.getBoolean(propDefinedStatements)) {
			JdbpDriverManager.propDefinedStatements = Boolean.getBoolean(propDefinedStatements);
		}

	}

	public static void setDbDefinedStatements(String dbDefinedStatements) {
		if(Boolean.getBoolean(dbDefinedStatements)) {
			JdbpDriverManager.dbDefinedStatements = Boolean.getBoolean(dbDefinedStatements);
		}
	}
}
