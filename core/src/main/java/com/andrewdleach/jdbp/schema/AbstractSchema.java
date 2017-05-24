package com.andrewdleach.jdbp.schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.andrewdleach.jdbp.connection.JdbpSchemaConnectionManager;
import com.andrewdleach.jdbp.connection.JdbpSchemaConnectionManagerProperties;
import com.andrewdleach.jdbp.connection.nosql.NoSqlDataSource;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.logger.JdbpLogger;
import com.andrewdleach.jdbp.logger.JdbpLoggerConstants;
import com.andrewdleach.jdbp.model.DBInfo;
import com.andrewdleach.jdbp.parser.DBInfoTransposer;
import com.andrewdleach.jdbp.parser.ResultSetTransposer;
import com.andrewdleach.jdbp.properties.util.SQLUtil;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudOperationInfo;
import com.andrewdleach.jdbp.statement.syntax.sproc.JdbpCallableStatement;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @since 12.1.16
 * @author andrew.leach
 */
public abstract class AbstractSchema extends JdbpSchemaConnectionManager {

	private List<JdbpCallableStatement> statements;

	protected AbstractSchema(String schemaName, String driverName, JdbpSchemaConnectionManagerProperties connectionManagerProperties) {
		super(schemaName, driverName, connectionManagerProperties);
	}

	public void closeDataSource() {
		if(SQLUtil.isNoSQLDriver(getDriverName())) {
			closeNoSqlDataSource();
		}
		else {
			closeHikariDataSource();
		}
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
	protected boolean executePreparedUpdate(CrudOperationInfo crudOperationInfo, String destinationTable, List<DBInfo> infosToUpdate) throws JdbpException {
		boolean isSuccess = false;
		Connection pooledConnection = getConnection();
		PreparedStatement preparedUpdateStatement = null;
		try {
			String infosStringToUpdateUnsanitized = DBInfoTransposer.constructSQLUpdateString(getSchemaName(), destinationTable, crudOperationInfo, infosToUpdate);
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
	 * @param crudOperationInfo
	 * @param destinationTableName
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	protected List<DBInfo> executePreparedQuery(CrudOperationInfo crudOperationInfo, String destinationTableName, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> dbInfos = null;

		ResultSet resultSet = null;
		Connection pooledConnection = getConnection();
		PreparedStatement preparedQueryStatement = null;
		try {
			String infosStringToUpdateUnsanitized = DBInfoTransposer.constructSQLQueryString(getSchemaName(), destinationTableName, crudOperationInfo, containerClass);
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

	protected List<DBInfo> executeNoSqlGet(String destinationTableName, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> noSqlDBInfos = new ArrayList<>();
		if(SQLUtil.isMongoDriver(getDriverName())) {
			NoSqlDataSource noSqlDataSource = getNoSqlConnection();
			MongoDatabase mongoDatabase = noSqlDataSource.getMongoDatabase();
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(destinationTableName);
			noSqlDBInfos = DBInfoTransposer.convertToDBInfosFromDocuments(mongoCollection, containerClass);
		}
		return noSqlDBInfos;
	}

	protected boolean executeNoSqlUpdate(String destinationTableName, List<DBInfo> dbInfos, Class<? extends DBInfo> containerClass) {
		boolean isSuccess = true;
		if(SQLUtil.isMongoDriver(getDriverName())) {
			NoSqlDataSource noSqlDataSource = getNoSqlConnection();
			MongoDatabase mongoDatabase = noSqlDataSource.getMongoDatabase();
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(destinationTableName);
			List<Document> dbInfosConvertedToDocuments = null;
			try {
				dbInfosConvertedToDocuments = DBInfoTransposer.constructNoSqlUpdateJson(dbInfos, containerClass);
			}
			catch(JdbpException e) {
				isSuccess = false;
				JdbpLogger.logInfo(JdbpLoggerConstants.NOSQL, e);
			}
			mongoCollection.insertMany(dbInfosConvertedToDocuments);
		}
		return isSuccess;
	}

	protected List<DBInfo> executeNoSqlFindTopN(String destinationTableName, Class<? extends DBInfo> containerClass, int topN) throws JdbpException {
		List<DBInfo> dbInfos = new ArrayList<>(topN);
		if(SQLUtil.isMongoDriver(getDriverName())) {
			NoSqlDataSource noSqlDataSource = getNoSqlConnection();
			MongoDatabase mongoDatabase = noSqlDataSource.getMongoDatabase();
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(destinationTableName);
			dbInfos = DBInfoTransposer.convertToDBInfosFromDocuments(mongoCollection, containerClass);
		}
		return dbInfos.isEmpty() ? dbInfos : dbInfos.subList(0, topN);
	}

	public void setAvailableStatements(List<JdbpCallableStatement> statements) {
		this.statements = statements;
	}

}
