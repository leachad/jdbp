package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import exception.JdbpException;
import model.DBInfo;
import parser.ResultSetTransposer;

/**
 * @since 12.1.16
 * @author andrew.leach
 */
public abstract class AbstractDB extends JdbpConnectionManager {

	/**
	 * @param dataSourceName
	 * @param rawQueryString
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	protected List<DBInfo> executeRawQueryStatement(String dataSourceName, String rawQueryString, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> dbInfos = null;

		if(dataSourceName != null) {
			ResultSet resultSet = null;
			IndexedPoolableConnection indexedConnection = getConnection(dataSourceName);
			Statement rawStatement = null;
			try {
				rawStatement = indexedConnection.getConnection().createStatement();
				resultSet = rawStatement.executeQuery(rawQueryString);
				dbInfos = ResultSetTransposer.transposeResultSet(resultSet, containerClass);
				indexedConnection.commit();
			}
			catch(SQLException e) {
				try {
					indexedConnection.rollback();
				}
				catch(SQLException eE) {
					JdbpException.throwException(eE);
				}
				JdbpException.throwException(e);
			}
			finally {
				try {
					if(rawStatement != null) {
						rawStatement.close();
					}
					if(resultSet != null) {
						resultSet.close();
					}
				}
				catch(SQLException e) {
					JdbpException.throwException(e);
				}
				indexedConnection.release();
			}
		}
		return dbInfos;
	}

	/**
	 * @param dataSourceName
	 * @param procedureInfo
	 * @param rawQueryString
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	protected List<DBInfo> executeCallableStatement(String dataSourceName, StatementContainer procedureInfo, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> dbInfos = null;

		if(dataSourceName != null) {
			ResultSet resultSet = null;
			IndexedPoolableConnection indexedConnection = getConnection(dataSourceName);
			PreparedStatement callableStatement = null;
			try {
				callableStatement = indexedConnection.getConnection().prepareCall(procedureInfo.getRawCallableStatement());
				resultSet = executeCallableStatement(callableStatement);
				dbInfos = ResultSetTransposer.transposeResultSet(resultSet, containerClass);
				indexedConnection.commit();
			}
			catch(SQLException e) {
				try {
					indexedConnection.rollback();
				}
				catch(SQLException eE) {
					JdbpException.throwException(eE);
				}
				JdbpException.throwException(e);
			}
			finally {
				try {
					if(callableStatement != null) {
						callableStatement.close();
					}
					if(resultSet != null) {
						resultSet.close();
					}
				}
				catch(SQLException e) {
					JdbpException.throwException(e);
				}
				indexedConnection.release();
			}
		}
		return dbInfos;
	}

	/**
	 * @param callableStatement
	 * @return
	 * @throws SQLException
	 */
	private ResultSet executeCallableStatement(PreparedStatement callableStatement) throws JdbpException {
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

	/**
	 * @param statementName
	 * @return statementInfo for processing callable statement TODO Continue improving on this method
	 */
	protected StatementContainer prepareStatementInfo(String statementName) {
		StatementContainer statementInfo = new StatementContainer();
		statementInfo.setRawCallableStatement(statementName);
		return statementInfo;

	}

}
