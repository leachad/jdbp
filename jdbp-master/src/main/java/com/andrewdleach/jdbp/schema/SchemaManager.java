/**
 * 
 */
package com.andrewdleach.jdbp.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.andrewdleach.jdbp.connection.ConnectionManagerProperties;
import com.andrewdleach.jdbp.driver.DriverStorage;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.host.HostManager;
import com.andrewdleach.jdbp.properties.util.DriverUtil;
import com.andrewdleach.jdbp.properties.util.StatementUtil;
import com.andrewdleach.jdbp.statement.syntax.sproc.JdbpCallableStatement;

/**
 * @author andrew.leach
 */
public class SchemaManager {
	private static Map<String, JdbpSchema> schemaMap = new HashMap<>();
	private static List<String> schemaNames = new ArrayList<>();

	private static Map<String, String> urlParamArgPairs;
	private static String userName;
	private static String password;
	private static Properties info;
	private static boolean loadBalanced;
	private static boolean propDefinedStatements;
	private static boolean dbDefinedStatements;
	private static boolean noSqlDriver;

	public SchemaManager() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Utility method to retrieve a requested Schema object by name. If the schema is not available, there is a new schema created from the requested
	 * name and made available to any other requests to getSchema.
	 * 
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	public static JdbpSchema getSchema(String schemaName) throws JdbpException {
		JdbpSchema dbInstance = schemaMap.get(schemaName);
		if(dbInstance == null) {
			dbInstance = buildJdbpSchemaFromProperties(schemaName);
			schemaMap.put(schemaName, dbInstance);
		}
		return dbInstance;
	}

	/**
	 * Stores the schemaNames obtained from props
	 * 
	 * @param schemaNames
	 */
	public static void setSchemaNames(List<String> schemaNames) {
		SchemaManager.schemaNames.addAll(schemaNames);
	}

	/**
	 * Creat Schemas for names obtained from user defined properties file
	 * 
	 * @throws JdbpException
	 */
	public static void createAllSchemasFromProperties() throws JdbpException {
		if(schemaNames != null) {
			for(String schemaName: schemaNames) {
				JdbpSchema dbInstance = buildJdbpSchemaFromProperties(schemaName);
				schemaMap.put(schemaName, dbInstance);
			}
		}
	}

	public static void closeAllDataSources() {
		List<JdbpSchema> schemaContainers = SchemaManager.getAvailableSchemas();
		for(JdbpSchema schemaContainer: schemaContainers) {
			schemaContainer.closeDataSource();
		}
	}

	/**
	 * @param schemaName
	 * @return a fully built schemaContainer
	 */
	private static JdbpSchema buildJdbpSchemaFromProperties(String schemaName) throws JdbpException {
		if(HostManager.getHostNames() == null) {
			JdbpException.throwException("database hostName cannot be null");
		}
		if(schemaName == null) {
			JdbpException.throwException(new IllegalArgumentException("schemaName must not be null"));
		}

		ConnectionManagerProperties connectionManagerProperties = new ConnectionManagerProperties();

		String targetUrl = buildTargetUrlForSchema(schemaName);
		connectionManagerProperties.setTargetUrl(targetUrl);

		if(userCredentialsProvided() && !propertiesInfoProvided()) {
			connectionManagerProperties.setUserName(userName);
			connectionManagerProperties.setPassword(password);
			connectionManagerProperties.setCredentialsNoProperties(true);
		}
		else if(!userCredentialsProvided() && propertiesInfoProvided()) {
			connectionManagerProperties.setPropertiesInfo(info);
			connectionManagerProperties.setPropertiesNoCredentials(true);
		}
		else if(!userCredentialsProvided() && !propertiesInfoProvided()) {
			connectionManagerProperties.setNoPropertiesNoCredentials(true);
		}

		AbstractSchema schema = null;
		if(SchemaManager.noSqlDriver) {
			connectionManagerProperties.setNoSqlSchema(true);
			schema = new JdbpNoSqlSchema(schemaName, connectionManagerProperties);
		}
		else {
			schema = new JdbpSchema(schemaName, connectionManagerProperties);
		}

		if(propDefinedStatements || dbDefinedStatements) {

		}

		if(propDefinedStatements || dbDefinedStatements) {
			List<JdbpCallableStatement> statements = null;
			if(propDefinedStatements) {
				statements = StatementUtil.constructStatementContainersWithResourceBundle(schemaName);
			}
			else {
				statements = StatementUtil.constructStatementContainersWithClientTable();
			}
			if(statements != null && statements.size() > 0) {
				schema.setAvailableStatements(statements);
			}
		}

		return schema;
	}

	private static String buildTargetUrlForSchema(String schemaName) {
		StringBuilder targetUrlBuilder = new StringBuilder();
		String requestedDriverName = DriverStorage.getRequestedDriverName();
		String connectionString = DriverUtil.getDriverClassFlagForDriverName(requestedDriverName);
		if(loadBalanced && DriverUtil.isLoadBalancedSupportedForDriverName(requestedDriverName)) {
			connectionString = connectionString + DriverUtil.getLoadBalancedFlagForDriverName(requestedDriverName);
		}
		// TODO add in additional cases for replication, etc...
		targetUrlBuilder.append(connectionString);
		targetUrlBuilder.append("//");

		List<String> hostNames = HostManager.getHostNames();
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

		String formattedHostName = targetUrlBuilder.toString();
		if(formattedHostName.charAt(formattedHostName.length() - 1) != 0x2F) {
			targetUrlBuilder.append("/");
		}
		targetUrlBuilder.append(schemaName);

		targetUrlBuilder.append("?");
		int argIndex = 1;
		for(Entry<String, String> paramArgPair: urlParamArgPairs.entrySet()) {
			targetUrlBuilder.append(paramArgPair.getKey() + "=" + paramArgPair.getValue());
			if(argIndex >= 1 && argIndex < urlParamArgPairs.size() && argIndex != urlParamArgPairs.size()) {
				targetUrlBuilder.append("&");
			}
			argIndex++;
		}

		return targetUrlBuilder.toString();
	}

	private static List<JdbpSchema> getAvailableSchemas() {
		return new ArrayList<>(schemaMap.values());
	}

	private static boolean userCredentialsProvided() {
		return userName != null && password != null;
	}

	private static boolean propertiesInfoProvided() {
		return info != null;
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
	 * @param userName
	 */
	public static void setUserName(String userName) {
		SchemaManager.userName = userName;
	}

	/**
	 * @param password
	 */
	public static void setPassword(String password) {
		SchemaManager.password = password;
	}

	public static void setLoadBalanced(String isLoadBalanced) {
		if(Boolean.getBoolean(isLoadBalanced)) {
			SchemaManager.loadBalanced = Boolean.getBoolean(isLoadBalanced);
		}
	}

	public static void setPropDefinedStatements(String propDefinedStatements) {
		if(Boolean.getBoolean(propDefinedStatements)) {
			SchemaManager.propDefinedStatements = Boolean.getBoolean(propDefinedStatements);
		}

	}

	public static void setDbDefinedStatements(String dbDefinedStatements) {
		if(Boolean.getBoolean(dbDefinedStatements)) {
			SchemaManager.dbDefinedStatements = Boolean.getBoolean(dbDefinedStatements);
		}
	}

	public static void setNoSqlDriver(boolean noSqlDriver) {
		SchemaManager.noSqlDriver = noSqlDriver;

	}
}
