package com.andrewdleach.jdbp.properties.info;

import java.io.Serializable;
import java.sql.Driver;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @since 12.29.16
 * @author andrew.leach
 */
public class DriverPropertiesInfo implements Serializable {
	private static final long serialVersionUID = -2943279356066920245L;

	private String driverClassLabel;
	private String loadBalancedLabel;
	private boolean supportsLoadBalancing;
	private boolean supportsReplication;
	private int defaultLimit;
	private String requestedDriverName;
	private List<String> hostNames;
	private List<Integer> portNumbers;
	private List<String> schemaNames;
	private Map<String, String> urlParams;
	private String userName;
	private char[] password;
	private boolean propDefinedStatements;
	private boolean dbDefinedStatements;
	private boolean loadBalanced;
	private Driver driver;
	private Properties properties;
	private List<String> cacheableTableNames;
	
	public String getDriverClassLabel() {
		return driverClassLabel;
	}

	public void setDriverClassLabel(String driverClassLabel) {
		this.driverClassLabel = driverClassLabel;
	}

	public String getLoadBalancedLabel() {
		return loadBalancedLabel;
	}

	public void setLoadBalancedLabel(String loadBalancedLabel) {
		this.loadBalancedLabel = loadBalancedLabel;
	}

	public boolean isSupportsLoadBalancing() {
		return supportsLoadBalancing;
	}

	public void setSupportsLoadBalancing(boolean supportsLoadBalancing) {
		this.supportsLoadBalancing = supportsLoadBalancing;
	}

	public boolean isSupportsReplication() {
		return supportsReplication;
	}

	public void setSupportsReplication(boolean supportsReplication) {
		this.supportsReplication = supportsReplication;
	}

	public int getDefaultLimit() {
		return defaultLimit;
	}

	public void setDefaultLimit(int defaultLimit) {
		this.defaultLimit = defaultLimit;
	}

	public String getRequestedDriverName() {
		return requestedDriverName;
	}
	
	public void setRequestedDriverName(String requestedDriverName) {
		this.requestedDriverName = requestedDriverName;
		
	}

	public List<String> getHostNames() {
		return hostNames;
	}

	public void setHostNames(List<String> hostNames) {
		this.hostNames = hostNames;
	}

	public List<Integer> getPortNumbers() {
		return portNumbers;
	}

	public void setPortNumbers(List<Integer> portNumbers) {
		this.portNumbers = portNumbers;
	}

	public List<String> getSchemaNames() {
		return schemaNames;
	}

	public void setSchemaNames(List<String> schemaNames) {
		this.schemaNames = schemaNames;
	}

	public Map<String, String> getUrlParams() {
		return urlParams;
	}

	public void setUrlParams(Map<String, String> urlParams) {
		this.urlParams = urlParams;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public boolean isPropDefinedStatements() {
		return propDefinedStatements;
	}

	public void setPropDefinedStatements(boolean propDefinedStatements) {
		this.propDefinedStatements = propDefinedStatements;
	}

	public boolean isDbDefinedStatements() {
		return dbDefinedStatements;
	}

	public void setDbDefinedStatements(boolean dbDefinedStatements) {
		this.dbDefinedStatements = dbDefinedStatements;
	}

	public boolean isLoadBalanced() {
		return loadBalanced;
	}

	public void setLoadBalanced(boolean loadBalanced) {
		this.loadBalanced = loadBalanced;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Properties getJavaProperties() {
		return properties;
	}
	
	public void setJavaProperties(Properties properties) {
		this.properties = properties;
	}
	
	public List<String> getCacheableTableNames() {
		return cacheableTableNames;
	}
	
	public void setCacheableTableNames(List<String> cacheableTableNames) {
		this.cacheableTableNames = cacheableTableNames;
	}

}
