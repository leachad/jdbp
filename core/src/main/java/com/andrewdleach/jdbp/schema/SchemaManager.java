/**
 * 
 */
package com.andrewdleach.jdbp.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.andrewdleach.jdbp.connection.JdbpSchemaConnectionManagerProperties;
import com.andrewdleach.jdbp.driver.DriverLocator;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.host.HostUtil;
import com.andrewdleach.jdbp.properties.info.DriverPropertiesInfo;
import com.andrewdleach.jdbp.properties.util.DriverUtil;
import com.andrewdleach.jdbp.properties.util.SqlUtil;
import com.andrewdleach.jdbp.properties.util.StatementUtil;
import com.andrewdleach.jdbp.statement.syntax.sproc.JdbpCallableStatement;

/**
 * @author andrew.leach
 */
public class SchemaManager {
	private static Map<String, AbstractSchema> schemaMap = new HashMap<>();


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
	public static AbstractSchema getSchema(String schemaName) throws JdbpException {
		AbstractSchema dbInstance = schemaMap.get(schemaName);
		if(dbInstance == null) {
			dbInstance = buildJdbpSchemaFromProperties(schemaName);
			schemaMap.put(schemaName, dbInstance);
		}
		return dbInstance;
	}



	/**
	 * Create Schemas for names obtained from user defined properties file
	 * 
	 * @throws JdbpException
	 */
	public static void createAllSchemasFromProperties() throws JdbpException {
		Map<String, DriverPropertiesInfo> definedDriverMap = DriverLocator.getDefinedDriverMap();
		if(definedDriverMap != null) {
			for (Map.Entry<String, DriverPropertiesInfo> driverPropertiesEntry: definedDriverMap.entrySet()) {
				for (String schemaName : driverPropertiesEntry.getValue().getSchemaNames()) {
					AbstractSchema dbInstance = buildJdbpSchemaFromProperties(schemaName, driverPropertiesEntry.getValue());
					schemaMap.put(schemaName, dbInstance);
				}
			}
		}
	}

	public static void closeAllDataSources() {
		List<AbstractSchema> schemaContainers = SchemaManager.getAvailableSchemas();
		for(AbstractSchema schemaContainer: schemaContainers) {
			schemaContainer.closeDataSource();
		}
	}

	private static AbstractSchema buildJdbpSchemaFromProperties(String schemaName) throws JdbpException {
		Map<String, DriverPropertiesInfo> driverMap = DriverLocator.getDefinedDriverMap();
		DriverPropertiesInfo driverPropertiesInfo = null;
		for (Map.Entry<String, DriverPropertiesInfo> driverPropertiesEntry: driverMap.entrySet()) {
			List<String> schemaNames = driverPropertiesEntry.getValue().getSchemaNames();
			if (schemaNames.contains(schemaName)) {
				driverPropertiesInfo = driverPropertiesEntry.getValue();
				break;
			}
		}
		if (driverPropertiesInfo != null) {
			return buildJdbpSchemaFromProperties(driverPropertiesInfo.getRequestedDriverName(), driverPropertiesInfo);
		}
		return null;
	}
	/**
	 * @param schemaName
	 * @param driverPropertiesInfo 
	 * @return a fully built schemaContainer
	 */
	private static AbstractSchema buildJdbpSchemaFromProperties(String schemaName, DriverPropertiesInfo driverPropertiesInfo) throws JdbpException {
		if(driverPropertiesInfo.getHostNames() == null) {
			JdbpException.throwException("database hostName cannot be null");
		}
		if(schemaName == null) {
			JdbpException.throwException(new IllegalArgumentException("schemaName must not be null"));
		}

		JdbpSchemaConnectionManagerProperties connectionManagerProperties = new JdbpSchemaConnectionManagerProperties();
		String targetUrl = buildTargetUrlForSchema(driverPropertiesInfo, schemaName);
		connectionManagerProperties.setTargetUrl(targetUrl);
		connectionManagerProperties.setHostName(HostUtil.findOneHostName(driverPropertiesInfo.getHostNames()));
		connectionManagerProperties.setPortNumber(HostUtil.findOnePortNumber(driverPropertiesInfo.getPortNumbers(), driverPropertiesInfo.getRequestedDriverName()));

		if(userCredentialsProvided(driverPropertiesInfo)) {
			connectionManagerProperties.setUserName(driverPropertiesInfo.getUserName());
			connectionManagerProperties.setPassword(driverPropertiesInfo.getPassword());
			connectionManagerProperties.setCredentialsNoProperties(true);
		}
		else if(!userCredentialsProvided(driverPropertiesInfo) && propertiesInfoProvided(driverPropertiesInfo)) {
			connectionManagerProperties.setPropertiesInfo(driverPropertiesInfo.getJavaProperties());
			connectionManagerProperties.setPropertiesNoCredentials(true);
		}
		else if(!userCredentialsProvided(driverPropertiesInfo) && !propertiesInfoProvided(driverPropertiesInfo)) {
			connectionManagerProperties.setNoPropertiesNoCredentials(true);
		}

		AbstractSchema schema = null;
		if(SqlUtil.isNoSqlDriver(driverPropertiesInfo.getRequestedDriverName())) {
			schema = new JdbpNoSqlSchema(schemaName, driverPropertiesInfo.getRequestedDriverName(), connectionManagerProperties);
		}
		else {
			schema = new JdbpSchema(schemaName, driverPropertiesInfo.getRequestedDriverName(), connectionManagerProperties);
		}

		if(driverPropertiesInfo.isPropDefinedStatements() || driverPropertiesInfo.isDbDefinedStatements()) {

			List<JdbpCallableStatement> statements = null;
			if(driverPropertiesInfo.isPropDefinedStatements()) {
				statements = StatementUtil.constructStatementContainersWithResourceBundle(schemaName);
			}
			else if (driverPropertiesInfo.isDbDefinedStatements()){
				statements = StatementUtil.constructStatementContainersWithClientTable();
			}
			if(statements != null && statements.size() > 0) {
				// TODO schema.setAvailableStatements(statements);
			}
		}

		return schema;
	}

	private static String buildTargetUrlForSchema(DriverPropertiesInfo driverPropertiesInfo, String schemaName) {
		StringBuilder targetUrlBuilder = new StringBuilder();
		String requestedDriverName = driverPropertiesInfo.getRequestedDriverName();
		String connectionString = DriverUtil.getDriverClassFlagForDriverName(requestedDriverName);
		if(driverPropertiesInfo.isLoadBalanced() && DriverUtil.isLoadBalancedSupportedForDriverName(requestedDriverName)) {
			connectionString = connectionString + DriverUtil.getLoadBalancedFlagForDriverName(requestedDriverName);
		}
		// TODO add in additional cases for replication, etc...
		targetUrlBuilder.append(connectionString);
		targetUrlBuilder.append("//");

		List<String> hostNames = driverPropertiesInfo.getHostNames();
		List<Integer> portNumbers = driverPropertiesInfo.getPortNumbers();
		int commaIndex = 0;
		int hostIndex = 0;
		for(String hostName: hostNames) {
			targetUrlBuilder.append(hostName);
			targetUrlBuilder.append(":");
			targetUrlBuilder.append(portNumbers.get(hostIndex));
			if(commaIndex < hostNames.size() - 1) {
				targetUrlBuilder.append(",");
				commaIndex++;
			}
			else {
				targetUrlBuilder.append("/");
			}
			hostIndex++;
		}

		String formattedHostName = targetUrlBuilder.toString();
		if(formattedHostName.charAt(formattedHostName.length() - 1) != 0x2F) {
			targetUrlBuilder.append("/");
		}
		targetUrlBuilder.append(schemaName);

		if(driverPropertiesInfo.getUrlParams() != null && driverPropertiesInfo.getUrlParams().size() > 0) {
			targetUrlBuilder.append("?");
			int argIndex = 1;
			for(Entry<String, String> paramArgPair: driverPropertiesInfo.getUrlParams().entrySet()) {
				targetUrlBuilder.append(paramArgPair.getKey() + "=" + paramArgPair.getValue());
				if(argIndex >= 1 && argIndex < driverPropertiesInfo.getUrlParams().size() && argIndex != driverPropertiesInfo.getUrlParams().size()) {
					targetUrlBuilder.append("&");
				}
				argIndex++;
			}
		}

		return targetUrlBuilder.toString();
	}

	private static List<AbstractSchema> getAvailableSchemas() {
		return new ArrayList<>(schemaMap.values());
	}

	private static boolean userCredentialsProvided(DriverPropertiesInfo driverPropertiesInfo) {
		return driverPropertiesInfo.getUserName() != null && driverPropertiesInfo.getPassword() != null;
	}

	private static boolean propertiesInfoProvided(DriverPropertiesInfo driverPropertiesInfo) {
		return driverPropertiesInfo.getJavaProperties() != null;
	}

}
