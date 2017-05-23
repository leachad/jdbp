package com.andrewdleach.jdbp.connection.nosql;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
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
		mongoClient = new MongoClient(new MongoClientURI(config.getTargetUrl()));
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
