/**
 * 
 */
package db.schema;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import db.model.DBInfo;
import db.statement.StatementContainer;
import exception.JdbpException;

/**
 * @author andrew.leach
 */
public class SchemaContainer extends AbstractDB {

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
	private List<StatementContainer> statements;

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
	 * @param procedureName
	 * @param containerClass
	 * @return List<DBInfo>
	 * @throws JdbpException
	 */
	public List<DBInfo> executeStoredProcedure(String procedureName, Class<? extends DBInfo> containerClass) throws JdbpException {
		StatementContainer statementInfo = prepareStatementInfo(procedureName);
		return executeCallableStatement(procedureName, statementInfo, containerClass);

	}

	/**
	 * @param schemaName
	 */
	public SchemaContainer(String schemaName) {
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

	public void setAvailableStatements(List<StatementContainer> statements) {
		this.statements = statements;
	}

	public List<StatementContainer> getAvailableStatements() {
		return statements;
	}
}
