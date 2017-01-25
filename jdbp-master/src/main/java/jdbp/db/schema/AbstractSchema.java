package jdbp.db.schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jdbp.db.connection.ConnectionManager;
import jdbp.db.model.DBInfo;
import jdbp.db.statement.syntax.crud.CrudOperation;
import jdbp.db.statement.syntax.sproc.JdbpCallableStatement;
import jdbp.exception.JdbpException;
import jdbp.parser.DBInfoTransposer;
import jdbp.parser.ResultSetTransposer;

/**
 * @since 12.1.16
 * @author andrew.leach
 */
public abstract class AbstractSchema extends ConnectionManager {

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
			Connection pooledConnection = getConnection(dataSourceName);
			Statement rawStatement = null;
			try {
				rawStatement = pooledConnection.createStatement();
				resultSet = rawStatement.executeQuery(rawQueryString);
				dbInfos = ResultSetTransposer.transposeResultSet(resultSet, containerClass);
				if(!pooledConnection.getAutoCommit()) {
					pooledConnection.commit();
				}
			}
			catch(SQLException e) {
				try {
					pooledConnection.rollback();
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
			}
		}
		return dbInfos;
	}

	/**
	 * @param dataSourceName
	 * @param rawUpdateString
	 * @param infosToUpdate
	 * @return
	 * @throws JdbpException
	 */
	protected boolean executeRawQueryUpdate(String dataSourceName, CrudOperation crudOperation, String destinationTable, List<DBInfo> infosToUpdate) throws JdbpException {
		boolean isSuccess = false;
		if(dataSourceName != null) {
			Connection pooledConnection = getConnection(dataSourceName);
			PreparedStatement rawStatement = null;
			try {
				String infosStringToUpdateUnsanitized = DBInfoTransposer.convertDbInfosToSQLString(dataSourceName, destinationTable, crudOperation, infosToUpdate);
				rawStatement = pooledConnection.prepareStatement(infosStringToUpdateUnsanitized);
				int result = rawStatement.executeUpdate();
				isSuccess = result == 1 ? true : false;
				if(!pooledConnection.getAutoCommit()) {
					pooledConnection.commit();
				}
			}
			catch(SQLException e) {
				try {
					if(!pooledConnection.getAutoCommit()) {
						pooledConnection.rollback();
					}
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
				}
				catch(SQLException e) {
					JdbpException.throwException(e);
				}
			}
		}
		return isSuccess;
	}

	/**
	 * @param dataSourceName
	 * @param procedureInfo
	 * @param rawQueryString
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	protected List<DBInfo> executeCallableStatement(String dataSourceName, JdbpCallableStatement procedureInfo, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> dbInfos = null;

		if(dataSourceName != null) {
			ResultSet resultSet = null;
			Connection pooledConnection = getConnection(dataSourceName);
			PreparedStatement callableStatement = null;
			try {
				callableStatement = pooledConnection.prepareCall(procedureInfo.getRawCallableStatement());
				resultSet = executeCallableStatement(callableStatement);
				dbInfos = ResultSetTransposer.transposeResultSet(resultSet, containerClass);
				if(!pooledConnection.getAutoCommit()) {
					pooledConnection.commit();
				}
			}
			catch(SQLException e) {
				try {
					pooledConnection.rollback();
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
	protected JdbpCallableStatement prepareStatementInfo(String statementName) {
		JdbpCallableStatement statementInfo = new JdbpCallableStatement();
		statementInfo.setRawCallableStatement(statementName);
		return statementInfo;

	}

}
