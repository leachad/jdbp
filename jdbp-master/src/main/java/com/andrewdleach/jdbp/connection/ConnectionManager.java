package com.andrewdleach.jdbp.connection;

import java.sql.Connection;
import java.sql.SQLException;

import com.andrewdleach.jdbp.exception.JdbpException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Connection Manager for the available database connections
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class ConnectionManager {

	private ConnectionManagerProperties connectionManagerProperties;
	private HikariDataSource hikariDataSource;

	public ConnectionManager(ConnectionManagerProperties connectionManagerProperties) {
		this.connectionManagerProperties = connectionManagerProperties;
		initializeDataSource();
	}

	public void closeDataSource() {
		getHikariDataSource().close();
	}

	public Connection getConnection() throws JdbpException {
		Connection connection = null;
		try {
			if(isCredentialsNoProperties() && isHikariEnablesCredentials()) {
				connection = getHikariDataSource().getConnection(getUsername(), getPassword());
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

	public void initializeDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(getTargetUrl());
		if(isCredentialsNoProperties()) {
			config.setUsername(getUsername());
			config.setPassword(getPassword());
		}

		hikariDataSource = new HikariDataSource(config);

	}

	private String getTargetUrl() {
		return connectionManagerProperties.getTargetUrl();
	}

	private String getPassword() {
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

	private HikariDataSource getHikariDataSource() {
		return hikariDataSource;
	}
}
