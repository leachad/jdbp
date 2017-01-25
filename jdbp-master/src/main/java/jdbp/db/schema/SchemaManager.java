/**
 * 
 */
package jdbp.db.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import jdbp.db.host.HostManager;
import jdbp.db.properties.util.DriverUtil;
import jdbp.db.properties.util.StatementUtil;
import jdbp.db.statement.syntax.sproc.JdbpCallableStatement;
import jdbp.exception.JdbpException;

/**
 * @author andrew.leach
 */
public class SchemaManager {
	private static Map<String, JdbpSchema> schemaMap = new HashMap<>();
	private static List<String> schemaNames = new ArrayList<>();

	private static Map<String, String> urlParamArgPairs;
	private static String username;
	private static String password;
	private static Properties info;
	private static boolean loadBalanced;
	private static String requestedDriverName;
	private static boolean propDefinedStatements;
	private static boolean dbDefinedStatements;

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

	/**
	 * @param schemaName
	 * @return a fully built schemaContainer
	 */
	public static JdbpSchema buildJdbpSchemaFromProperties(String schemaName) throws JdbpException {
		if(HostManager.getHostNames() == null) {
			JdbpException.throwException("database hostName cannot be null");
		}
		if(schemaName == null) {
			JdbpException.throwException(new IllegalArgumentException("schemaName must not be null"));
		}

		JdbpSchema schema = new JdbpSchema(schemaName);
		String targetUrl = buildTargetUrlForSchema(schema);
		schema.setTargetUrl(targetUrl);
		if(userCredentialsProvided() && !propertiesInfoProvided()) {
			schema.setUserName(username);
			schema.setPassword(password);
			schema.setCredentialsNoProperties(true);
		}
		else if(!userCredentialsProvided() && propertiesInfoProvided()) {
			schema.setPropertiesInfo(info);
			schema.setPropertiesNoCredentials(true);
		}
		else if(!userCredentialsProvided() && !propertiesInfoProvided()) {
			schema.setNoPropertiesNoCredentials(true);
		}
		if(propDefinedStatements || dbDefinedStatements) {
			List<JdbpCallableStatement> statements = null;
			if(propDefinedStatements) {
				statements = StatementUtil.constructStatementContainersWithResourceBundle(schemaName);
			}
			else {
				statements = StatementUtil.constructStatementContainersWithClientTable();
			}
			schema.setAvailableStatements(statements);
		}
		schema.initializeDataSource();

		return schema;
	}

	private static String buildTargetUrlForSchema(JdbpSchema schema) {
		StringBuilder targetUrlBuilder = new StringBuilder();
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
		targetUrlBuilder.append(schema.getSchemaName());

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

	public static List<JdbpSchema> getAvailableSchemas() {
		return new ArrayList<>(schemaMap.values());
	}

	private static boolean userCredentialsProvided() {
		return username != null && password != null;
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
	 * @param username
	 */
	public static void setUserName(String username) {
		SchemaManager.username = username;
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

	public static void setRequestedDriverName(String requestedDriverName) {
		SchemaManager.requestedDriverName = requestedDriverName;
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

	public static Map<String, JdbpSchema> getSchemaMap() {
		return schemaMap;
	}

	public static List<String> getSchemaNames() {
		return schemaNames;
	}

	public static Map<String, String> getUrlParamArgPairs() {
		return urlParamArgPairs;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static Properties getInfo() {
		return info;
	}

	public static boolean isLoadBalanced() {
		return loadBalanced;
	}

	public static String getRequestedDriverName() {
		return requestedDriverName;
	}

	public static boolean isPropDefinedStatements() {
		return propDefinedStatements;
	}

	public static boolean isDbDefinedStatements() {
		return dbDefinedStatements;
	}
}
