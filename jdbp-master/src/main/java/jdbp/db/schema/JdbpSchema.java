/**
 * 
 */
package jdbp.db.schema;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jdbp.db.model.DBInfo;
import jdbp.db.statement.syntax.crud.CrudOperation;
import jdbp.db.statement.syntax.sproc.JdbpCallableStatement;
import jdbp.exception.JdbpException;

/**
 * @author andrew.leach
 */
public class JdbpSchema extends AbstractSchema {

	private String schemaName;
	private String requestedDriverName;
	private String hostName;
	private String targetUrl;
	private Map<String, String> urlParamArgPairs;
	private String userName;
	private String password;
	private Properties propertiesInfo;
	private boolean credentialsNoProperties;
	private boolean propertiesNoCredentials;
	private boolean noPropertiesNoCredentials;
	private List<JdbpCallableStatement> statements;
	private boolean hikariEnablesCredentials = false;

	/**
	 * Connection pool manager instance for this schema
	 */
	private HikariDataSource hikariDataSource;

	/**
	 * @param rawQueryString
	 * @param containerClass
	 *        is the container class definition for the query results returned
	 * @return List<DBInfo>
	 * @throws JdbpException
	 */
	public List<DBInfo> executeQuery(String rawQueryString, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executeRawQueryStatement(schemaName, rawQueryString, containerClass);
	}

	/**
	 * @param destinationTableName
	 * @param clause
	 *        (Must be a commaSeparatedString with tuples defined like id=1,name=someName,etc...)
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	public List<DBInfo> executeSelect(String destinationTableName, String clause, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executePreparedQuery(schemaName, CrudOperation.SELECT, destinationTableName, clause, containerClass);
	}

	/**
	 * @param destinationTableName
	 * @param infosToInsert
	 * @return isSuccess
	 * @throws JdbpException
	 */
	public boolean executeInsert(String destinationTableName, List<DBInfo> infosToInsert) throws JdbpException {
		return executePreparedUpdate(schemaName, CrudOperation.INSERT, destinationTableName, infosToInsert);
	}

	/**
	 * @param destinationTableName
	 * @param infosToUpdate
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeUpdate(String destinationTableName, List<DBInfo> infosToUpdate) throws JdbpException {
		return executePreparedUpdate(schemaName, CrudOperation.UPDATE, destinationTableName, infosToUpdate);
	}

	/**
	 * @param destinationTableName
	 * @param infosToUpdate
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeDelete(String destinationTableName, List<DBInfo> infosToUpdate) throws JdbpException {
		return executePreparedUpdate(schemaName, CrudOperation.DELETE, destinationTableName, infosToUpdate);
	}

	/**
	 * @param procedureName
	 * @param containerClass
	 * @return List<DBInfo>
	 * @throws JdbpException
	 */
	public List<DBInfo> executeStoredProcedure(String procedureName, Class<? extends DBInfo> containerClass) throws JdbpException {
		JdbpCallableStatement statementInfo = prepareStatementInfo(procedureName);
		return executeCallableStatement(procedureName, statementInfo, containerClass);

	}

	/**
	 * @param schemaName
	 */
	public JdbpSchema(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getRequestedDriverName() {
		return requestedDriverName;
	}

	public void setRequestedDriverName(String requestedDriverName) {
		this.requestedDriverName = requestedDriverName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Map<String, String> getUrlParamArgPairs() {
		return urlParamArgPairs;
	}

	public void setUrlParamArgPairs(Map<String, String> urlParamArgPairs) {
		this.urlParamArgPairs = urlParamArgPairs;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public Properties getPropertiesInfo() {
		return propertiesInfo;
	}

	public void setPropertiesInfo(Properties propertiesInfo) {
		this.propertiesInfo = propertiesInfo;
	}

	/**
	 * Schema has Credentials Available but No Properties were specified
	 * 
	 * @return hasCredentialsNoProperties
	 */
	public boolean hasCredentialsNoProperties() {
		return credentialsNoProperties;
	}

	public void setCredentialsNoProperties(boolean credentialsNoProperties) {
		this.credentialsNoProperties = credentialsNoProperties;
	}

	/**
	 * Schema has Properties Available but no Credentials were specified
	 * 
	 * @return hasPropertiesNoCredentials
	 */
	public boolean hasPropertiesNoCredentials() {
		return propertiesNoCredentials;
	}

	public void setPropertiesNoCredentials(boolean propertiesNoCredentials) {
		this.propertiesNoCredentials = propertiesNoCredentials;
	}

	/**
	 * @return
	 */
	public boolean hasNoPropertiesNoCredentials() {
		return noPropertiesNoCredentials;
	}

	public void setNoPropertiesNoCredentials(boolean noPropertiesNoCredentials) {
		this.noPropertiesNoCredentials = noPropertiesNoCredentials;
	}

	public void setAvailableStatements(List<JdbpCallableStatement> statements) {
		this.statements = statements;
	}

	public List<JdbpCallableStatement> getAvailableStatements() {
		return statements;
	}

	public void initializeDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(targetUrl);
		if(credentialsNoProperties) {
			config.setUsername(userName);
			config.setPassword(password);
		}

		hikariDataSource = new HikariDataSource(config);

	}

	public void closeDataSource() {
		hikariDataSource.close();
	}

	public Connection getConnection() throws JdbpException {
		Connection connection = null;
		try {
			if(credentialsNoProperties && hikariEnablesCredentials) {
				connection = hikariDataSource.getConnection(userName, password);
			}
			else {
				connection = hikariDataSource.getConnection();
			}
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return connection;
	}

}
