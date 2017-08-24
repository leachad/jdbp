package com.andrewdleach.jdbp.connection;

import java.sql.Connection;
import java.sql.SQLException;

import com.andrewdleach.jdbp.connection.nosql.NoSqlDataSource;
import com.andrewdleach.jdbp.connection.nosql.NoSqlDataSourceConfig;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.properties.util.SqlUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Connection Manager for the available database connections
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class JdbpSchemaConnectionManager {

	private JdbpSchemaConnectionManagerProperties connectionManagerProperties;
	private HikariDataSource hikariDataSource;
	private NoSqlDataSource noSqlDataSource;
	private String schemaName;
	private String driverName;

	public JdbpSchemaConnectionManager(String schemaName, String driverName, JdbpSchemaConnectionManagerProperties connectionManagerProperties) {
		this.schemaName = schemaName;
		this.connectionManagerProperties = connectionManagerProperties;
		this.driverName = driverName;
		if(SqlUtil.isNoSqlDriver(driverName)) {
			initializeNoSqlDataSource(schemaName, driverName);
		}
		else {
			initializeHikariDataSource();
		}
	}

	protected String getSchemaName() {
		return schemaName;
	}

	protected String getDriverName() {
		return driverName;
	}

	protected void closeHikariDataSource() {
		getHikariDataSource().close();
	}

	protected void closeNoSqlDataSource() {
		getNoSqlDataSource().close();
	}

	protected Connection getConnection() throws JdbpException {
		return getHikariConnection();
	}

	protected NoSqlDataSource getNoSqlConnection() {
		return getNoSqlDataSource();
	}

	private Connection getHikariConnection() throws JdbpException {
		Connection connection = null;
		try {
			if(isCredentialsNoProperties() && isHikariEnablesCredentials()) {
				char[] password = getPassword();
				connection = getHikariDataSource().getConnection(getUsername(), new String(password));
			}
			else {
				connection = getHikariDataSource().getConnection();
			}
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return connection;
	}

	private void initializeHikariDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(getTargetUrl());
		if(isCredentialsNoProperties()) {
			config.setUsername(getUsername());
			char[] password = getPassword();
			config.setPassword(new String(password));
		}

		hikariDataSource = new HikariDataSource(config);
	}

	private void initializeNoSqlDataSource(String schemaName, String driverName) {
		NoSqlDataSourceConfig config = new NoSqlDataSourceConfig();
		config.setTargetUrl(getTargetUrl());
		config.setDriver(driverName);
		config.setHostName(getHostName());
		config.setPortNumber(getPortNumber());
		if(isCredentialsNoProperties()) {
			config.setUsername(getUsername());
			config.setPassword(getPassword());
		}
		noSqlDataSource = new NoSqlDataSource(config, schemaName);
	}

	private String getTargetUrl() {
		return connectionManagerProperties.getTargetUrl();
	}

	private char[] getPassword() {
		return connectionManagerProperties.getPassword();
	}

	private String getUsername() {
		return connectionManagerProperties.getUserName();
	}

	private boolean isHikariEnablesCredentials() {
		return connectionManagerProperties.isHikariEnablesCredentials();
	}

	private boolean isCredentialsNoProperties() {
		return connectionManagerProperties.isCredentialsNoProperties();
	}

	private String getHostName() {
		return connectionManagerProperties.getHostName();
	}

	private int getPortNumber() {
		return connectionManagerProperties.getPortNumber();
	}

	private HikariDataSource getHikariDataSource() {
		return hikariDataSource;
	}

	private NoSqlDataSource getNoSqlDataSource() {
		return noSqlDataSource;
	}
}
