package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exception.JdbpDriverException;

public class BaseJdb extends JdbManager {

	/**
	 * @param sessionContext
	 * @param dataSourceName
	 * @return
	 * @throws SQLException
	 * @throws JdbpDriverException
	 */
	public static PreparedStatement getCallableStatement(String dataSourceName, String statementName) throws SQLException, JdbpDriverException {
		Statement ps = null;
		if(dataSourceName != null) {
			Connection connection = getAvailableConnection(dataSourceName);
			ps = connection.prepareCall(statementName);
		}
		return (PreparedStatement)ps;
	}

	/**
	 * @param sessionContext
	 * @param callableStatement
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet executeCallableStatement(PreparedStatement callableStatement) throws SQLException {
		ResultSet resultSet = null;
		boolean hasRecordsAvailable = callableStatement.execute();
		while(hasRecordsAvailable || callableStatement.getUpdateCount() != -1) {
			if(hasRecordsAvailable) {
				resultSet = callableStatement.getResultSet();
				break;
			}
			else {
				int queryResult = callableStatement.getUpdateCount();
				if(queryResult == -1) {
					break;
				}
			}
			hasRecordsAvailable = callableStatement.getMoreResults();
		}
		return resultSet;
	}

}
