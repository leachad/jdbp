package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exception.JdbpException;

/**
 * @since 12.1.16
 * @author andrew.leach
 */
public class BaseJdb extends JdbpConnectionManager {

	/**
	 * @param dataSourceName
	 *        is the unique name of the requested schema
	 * @param statementName
	 *        is the unique name of the stored procedure or function to be executed
	 * @return
	 * @throws SQLException
	 * @throws JdbpException
	 */
	public static PreparedStatement getCallableStatement(String dataSourceName, String statementName) throws JdbpException {
		Statement ps = null;
		if(dataSourceName != null) {
			Connection connection = getConnection(dataSourceName);
			try {
				ps = connection.prepareCall(statementName);
			}
			catch(SQLException e) {
				JdbpException.throwException(e);
			}
		}
		return (PreparedStatement)ps;
	}

	/**
	 * @param callableStatement
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet executeCallableStatement(PreparedStatement callableStatement) throws JdbpException {
		ResultSet resultSet = null;
		try {
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
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return resultSet;
	}

}
