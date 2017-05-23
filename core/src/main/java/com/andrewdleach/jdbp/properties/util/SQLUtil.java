package com.andrewdleach.jdbp.properties.util;

import com.andrewdleach.jdbp.connection.nosql.NoSqlConstants;

public class SQLUtil {

	public static boolean isNoSQLDriver(String requestedDriverName) {
		return isMongoDriver(requestedDriverName) || isCassandra(requestedDriverName) || isHBase(requestedDriverName);
	}

	public static boolean isMongoDriver(String requestedDriverName) {
		return NoSqlConstants.MONGODB.toLowerCase().equals(requestedDriverName.toLowerCase());
	}

	public static boolean isCassandra(String requestedDriverName) {
		return NoSqlConstants.CASSANDRA.toLowerCase().equals(requestedDriverName.toLowerCase());
	}

	public static boolean isHBase(String requestedDriverName) {
		return NoSqlConstants.HBASE.toLowerCase().equals(requestedDriverName.toLowerCase());
	}

}
