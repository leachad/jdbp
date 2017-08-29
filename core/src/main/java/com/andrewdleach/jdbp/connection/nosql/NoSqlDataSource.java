package com.andrewdleach.jdbp.connection.nosql;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.andrewdleach.jdbp.logger.JdbpLogger;
import com.andrewdleach.jdbp.logger.JdbpLoggerConstants;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;

public class NoSqlDataSource {
	private NoSqlDataSourceConfig config;
	private MongoClient mongoClient;
	private String schemaName;
	private boolean connectionIsAlive;

	public NoSqlDataSource(NoSqlDataSourceConfig config, String schemaName) {
		this.schemaName = schemaName;
		initializeDataSource(config);
	}

	public NoSqlDataSource(NoSqlDataSourceConfig config) {
		initializeDataSource(config);
	}

	private void initializeDataSource(NoSqlDataSourceConfig config) {
		this.config = config;
		String driver = config.getDriver();
		switch(driver) {
			case NoSqlConstants.MONGODB:
				initializeMongoDatasource(config);
				break;
			case NoSqlConstants.CASSANDRA:
				break;
			case NoSqlConstants.HBASE:
				break;
		}
	}

	private void initializeMongoDatasource(NoSqlDataSourceConfig config) {
		char[] password = config.getPassword();
		ClusterSettings clusterSettings = ClusterSettings.builder().hosts(Collections.singletonList(new ServerAddress(config.getHostName(), config.getPortNumber()))).serverSelectionTimeout(3, TimeUnit.SECONDS).build();
		ServerSettings serverSettings = ServerSettings.builder().addServerMonitorListener(buildServerListener()).build();
		MongoClientSettings mongoClientSettings = null;
		if(driverRequiresCredentials(config, password)) {
			MongoCredential mongoCredential = MongoCredential.createCredential(config.getUsername(), getSchemaName(), password);
			mongoClientSettings = MongoClientSettings.builder().clusterSettings(clusterSettings).serverSettings(serverSettings).credentialList(Collections.singletonList(mongoCredential)).build();
		}
		else {
			mongoClientSettings = MongoClientSettings.builder().clusterSettings(clusterSettings).serverSettings(serverSettings).build();
		}
		mongoClient = MongoClients.create(mongoClientSettings);

	}

	private boolean driverRequiresCredentials(NoSqlDataSourceConfig config, char[] password) {
		return (password != null && password.length > 0) && (config.getUsername() != null && config.getUsername().length() > 0);
	}

	private ServerMonitorListener buildServerListener() {
		return new ServerMonitorListener() {

			@Override
			public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
				connectionIsAlive = true;

			}

			@Override
			public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
				connectionIsAlive = false;

			}

			@Override
			public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {
				JdbpLogger.logInfo(JdbpLoggerConstants.NOSQL, "Server Heartbeat Started for connectionId: ", event.getConnectionId().toString(), new Timestamp(System.currentTimeMillis()).toString());
			}

		};
	}

	public MongoDatabase getMongoDatabase() {
		return mongoClient.getDatabase(getSchemaName());
	}

	public String getSchemaName() {
		return schemaName;
	}

	public boolean isConnectionAvailable() {
		return connectionIsAlive;
	}

	public void close() {
		switch(config.getDriver()) {
			case NoSqlConstants.MONGODB:
				mongoClient.close();
				break;
			case NoSqlConstants.CASSANDRA:
				break;
			case NoSqlConstants.HBASE:
				break;
		}
	}
}
