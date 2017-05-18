package com.andrewdleach.jdbp.connection;

import java.sql.Connection;
import java.sql.SQLException;

import com.andrewdleach.jdbp.connection.nosql.NoSqlDataSource;
import com.andrewdleach.jdbp.connection.nosql.NoSqlDataSourceConfig;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.mongodb.MongoClient;
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
	private NoSqlDataSource noSqlDataSource;

	public ConnectionManager(ConnectionManagerProperties connectionManagerProperties) {
		this.connectionManagerProperties = connectionManagerProperties;
		if(connectionManagerProperties.isNoSqlSchema()) {
			initializeNoSqlDataSource();
		}
		else {
			initializeHikariDataSource();
		}
	}

	public void closeDataSource() {
		getHikariDataSource().close();
	}

	public Connection getConnection() throws JdbpException {
		return getHikariConnection();
	}

	public MongoClient getNoSqlMongoClient() {
		return getNoSqlDataSource().getMongoDataSource();
	}

	private Connection getHikariConnection() throws JdbpException {
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

	public void initializeHikariDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(getTargetUrl());
		if(isCredentialsNoProperties()) {
			config.setUsername(getUsername());
			config.setPassword(getPassword());
		}

		hikariDataSource = new HikariDataSource(config);
	}

	public void initializeNoSqlDataSource() {
		NoSqlDataSourceConfig config = new NoSqlDataSourceConfig();
		config.setTargetUrl(getTargetUrl());
		if(isCredentialsNoProperties()) {
			config.setUsername(getUsername());
			config.setPassword(getPassword());
		}
		noSqlDataSource = new NoSqlDataSource(config);
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

	private NoSqlDataSource getNoSqlDataSource() {
		return noSqlDataSource;
	}
}
