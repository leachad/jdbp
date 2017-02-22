package jdbp.db.connection;

import java.util.Properties;

public class ConnectionManagerProperties {
	private String targetUrl;
	private boolean credentialsNoProperties;
	private boolean hikariEnablesCredentials;
	private String userName;
	private String password;
	private Properties propertiesInfo;
	private boolean propertiesNoCredentials;
	private boolean noPropertiesNoCredentials;

	public ConnectionManagerProperties() {}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public boolean isCredentialsNoProperties() {
		return credentialsNoProperties;
	}

	public void setCredentialsNoProperties(boolean credentialsNoProperties) {
		this.credentialsNoProperties = credentialsNoProperties;
	}

	public boolean isHikariEnablesCredentials() {
		return hikariEnablesCredentials;
	}

	public void setHikariEnablesCredentials(boolean hikariEnablesCredentials) {
		this.hikariEnablesCredentials = hikariEnablesCredentials;
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

	public Properties getPropertiesInfo() {
		return propertiesInfo;
	}

	public void setPropertiesInfo(Properties propertiesInfo) {
		this.propertiesInfo = propertiesInfo;
	}

	public boolean isPropertiesNoCredentials() {
		return propertiesNoCredentials;
	}

	public void setPropertiesNoCredentials(boolean propertiesNoCredentials) {
		this.propertiesNoCredentials = propertiesNoCredentials;
	}

	public boolean isNoPropertiesNoCredentials() {
		return noPropertiesNoCredentials;
	}

	public void setNoPropertiesNoCredentials(boolean noPropertiesNoCredentials) {
		this.noPropertiesNoCredentials = noPropertiesNoCredentials;
	}
}