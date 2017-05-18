package com.andrewdleach.jdbp.connection.nosql;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class NoSqlDataSource {
	private MongoClient mongoClient;

	public NoSqlDataSource(NoSqlDataSourceConfig config) {
		initializeDataSource(config);
	}

	private void initializeDataSource(NoSqlDataSourceConfig config) {

		switch(config.getDriver()) {
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

	public MongoClient getMongoDataSource() {
		return mongoClient;
	}
}
