package com.andrewdleach.jdbp.properties.util;

import com.andrewdleach.jdbp.connection.nosql.NoSqlConstants;
import com.andrewdleach.jdbp.connection.sql.SqlConstants;
import com.andrewdleach.jdbp.driver.DriverStorage;

public class SqlUtil {

	public static boolean isNoSqlDriver(String requestedDriverName) {
		return isMongoDriver(requestedDriverName) || isCassandra(requestedDriverName) || isHBase(requestedDriverName);
	}

	public static boolean isMySqlDriver(String requestedDriverName) {
		return SqlConstants.MYSQL.toLowerCase().equals(requestedDriverName.toLowerCase());
	}

	public static boolean isSqlServerDriver(String requestedDriverName) {
		return SqlConstants.SQLSERVER.toLowerCase().equals(requestedDriverName.toLowerCase());
	}

	public static boolean isPostgreSqlDriver(String requestedDriverName) {
		return SqlConstants.POSTGRESQL.toLowerCase().equals(requestedDriverName.toLowerCase());
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

	public static int findDefaultPortNumberForDriver() {
		String requestedDriverName = DriverStorage.getRequestedDriverName();
		if(isNoSqlDriver(requestedDriverName)) {
			return findNoSQLPortNumber(requestedDriverName);
		}
		else {
			return findSqlPortNumber(requestedDriverName);
		}
	}

	private static int findNoSQLPortNumber(String requestedDriverName) {
		if(isMongoDriver(requestedDriverName)) {
			return 27017;
		}
		else if(isCassandra(requestedDriverName)) {
			return 7199;
		}
		else if(isHBase(requestedDriverName)) {
			return 2181;
		}
		else {
			return 27017;
		}
	}

	private static int findSqlPortNumber(String requestedDriverName) {
		if(isMySqlDriver(requestedDriverName)) {
			return 3306;
		}
		else if(isSqlServerDriver(requestedDriverName)) {
			return 1433;
		}
		else if(isPostgreSqlDriver(requestedDriverName)) {
			return 5432;
		}
		else {
			return 3306;
		}
	}

}
