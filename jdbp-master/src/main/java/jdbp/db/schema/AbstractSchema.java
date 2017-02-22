package jdbp.db.schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import jdbp.db.connection.ConnectionManager;
import jdbp.db.connection.ConnectionManagerProperties;
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

	private String schemaName;

	protected AbstractSchema(String schemaName, ConnectionManagerProperties connectionManagerProperties) {
		super(connectionManagerProperties);
		this.schemaName = schemaName;
	}

	/**
	 * @param dataSourceName
	 * @param rawQueryString
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	protected List<DBInfo> executeRawQueryStatement(String rawQueryString, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> dbInfos = null;

		ResultSet resultSet = null;
		Connection pooledConnection = getConnection();
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
				if(resultSet != null) {
					resultSet.close();
				}
				if(pooledConnection != null) {
					pooledConnection.close();
				}
			}
			catch(SQLException e) {
				JdbpException.throwException(e);
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
	protected boolean executePreparedUpdate(CrudOperation crudOperation, String destinationTable, List<DBInfo> infosToUpdate) throws JdbpException {
		boolean isSuccess = false;
		Connection pooledConnection = getConnection();
		PreparedStatement preparedUpdateStatement = null;
		try {
			String infosStringToUpdateUnsanitized = DBInfoTransposer.constructSQLUpdateString(schemaName, destinationTable, crudOperation, infosToUpdate);
			preparedUpdateStatement = pooledConnection.prepareStatement(infosStringToUpdateUnsanitized);
			int result = preparedUpdateStatement.executeUpdate();
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
				if(preparedUpdateStatement != null) {
					preparedUpdateStatement.close();
				}
				pooledConnection.close();
			}
			catch(SQLException e) {
				JdbpException.throwException(e);
			}
		}

		return isSuccess;
	}

	/**
	 * Abstract that executes an update that is expected to return a ResultSet [Ex: SELECT,
	 * 
	 * @param schemaName
	 * @param crudOperation
	 * @param destinationTableName
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	protected List<DBInfo> executePreparedQuery(CrudOperation crudOperation, String destinationTableName, String unsanitizedClause, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> dbInfos = null;

		ResultSet resultSet = null;
		Connection pooledConnection = getConnection();
		PreparedStatement preparedQueryStatement = null;
		try {
			String infosStringToUpdateUnsanitized = DBInfoTransposer.constructSqlQueryString(schemaName, destinationTableName, crudOperation, unsanitizedClause, containerClass);
			preparedQueryStatement = pooledConnection.prepareStatement(infosStringToUpdateUnsanitized);
			resultSet = preparedQueryStatement.executeQuery();
			dbInfos = ResultSetTransposer.transposeResultSet(resultSet, containerClass);
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
				if(preparedQueryStatement != null) {
					preparedQueryStatement.close();
				}
				if(resultSet != null) {
					resultSet.close();
				}
				pooledConnection.close();
			}
			catch(SQLException e) {
				JdbpException.throwException(e);
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
	protected List<DBInfo> executeCallableStatement(String dataSourceName, JdbpCallableStatement procedureInfo, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> dbInfos = null;

		ResultSet resultSet = null;
		Connection pooledConnection = getConnection();
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
				pooledConnection.close();
			}
			catch(SQLException e) {
				JdbpException.throwException(e);
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
