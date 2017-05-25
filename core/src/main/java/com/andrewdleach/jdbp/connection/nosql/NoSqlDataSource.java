package com.andrewdleach.jdbp.connection.nosql;

import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class NoSqlDataSource {
	private NoSqlDataSourceConfig config;
	private MongoClient mongoClient;
	private String schemaName;

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
		if((password != null && password.length > 0) && (config.getUsername() != null && config.getUsername().length() > 0)) {
			MongoCredential mongoCredential = MongoCredential.createCredential(config.getUsername(), getSchemaName(), password);
			mongoClient = new MongoClient(new ServerAddress(config.getHostName(), config.getPortNumber()), Arrays.asList(mongoCredential));
		}
		else {
			mongoClient = new MongoClient(new MongoClientURI(config.getTargetUrl()));
		}
	}

	public MongoDatabase getMongoDatabase() {
		return mongoClient.getDatabase(getSchemaName());
	}

	public String getSchemaName() {
		return schemaName;
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
