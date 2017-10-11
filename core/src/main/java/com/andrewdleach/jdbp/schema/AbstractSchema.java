package com.andrewdleach.jdbp.schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import com.andrewdleach.jdbp.properties.util.SqlUtil;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudOperationInfo;
import com.andrewdleach.jdbp.statement.syntax.sproc.JdbpCallableStatement;
import com.andrewdleach.jdbp.tools.JdbpTypeUtil;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

/**
 * @since 12.1.16
 * @author andrew.leach
 */
public abstract class AbstractSchema extends JdbpSchemaConnectionManager {

	protected AbstractSchema(String schemaName, String driverName, JdbpSchemaConnectionManagerProperties connectionManagerProperties) {
		super(schemaName, driverName, connectionManagerProperties);
	}

	public void closeDataSource() {
		if(SqlUtil.isNoSqlDriver(getDriverName())) {
			closeNoSqlDataSource();
		}
		else {
			closeHikariDataSource();
		}
	}

	public boolean isNoSqlDriver() {
		return SqlUtil.isNoSqlDriver(getDriverName());
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
			String infosStringToUpdateUnsanitized = DBInfoTransposer.constructSQLUpdateString(getSchemaName(), destinationTable, crudOperationInfo, infosToUpdate, getDriverName());
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
			String infosStringToUpdateUnsanitized = DBInfoTransposer.constructSQLQueryString(getSchemaName(), destinationTableName, crudOperationInfo, containerClass, getDriverName());
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
		if(SqlUtil.isMongoDriver(getDriverName())) {
			NoSqlDataSource noSqlDataSource = getNoSqlConnection();
			if(noSqlDataSource != null) {
				MongoDatabase mongoDatabase = noSqlDataSource.getMongoDatabase();
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(destinationTableName);
				noSqlDBInfos = DBInfoTransposer.executeUnconditionalFindAndReturnDBInfos(mongoCollection, containerClass);
			}
		}
		return noSqlDBInfos;
	}

	protected boolean executeNoSqlUpdateOne(String destinationTableName, DBInfo dbInfo) throws JdbpException {
		boolean isSuccess = false;
		CompletableFuture<Boolean> result = new CompletableFuture<>();
		if(SqlUtil.isMongoDriver(getDriverName())) {
			NoSqlDataSource noSqlDataSource = getNoSqlConnection();
			if(noSqlDataSource != null) {
				MongoDatabase mongoDatabase = noSqlDataSource.getMongoDatabase();
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(destinationTableName);
				Document dbInfoAsDocument = null;
				try {
					dbInfoAsDocument = DBInfoTransposer.convertToDocumentFromDBInfo(dbInfo);
				}
				catch(JdbpException e) {
					isSuccess = false;
					JdbpLogger.logInfo(JdbpLoggerConstants.NOSQL, e);
				}
				Map<String, Object> noSqlUpsertConditions = JdbpTypeUtil.findNoSqlCollectionUpsertConditions(dbInfo);
				Document filter = null;
				
				if(!noSqlUpsertConditions.isEmpty()) {
					filter = new Document(noSqlUpsertConditions);
					mongoCollection.replaceOne(filter, dbInfoAsDocument, new UpdateOptions().upsert(true), new SingleResultCallback<UpdateResult>() {

						@Override
						public void onResult(UpdateResult updateResult, Throwable error) {
							if (error == null && updateResult.wasAcknowledged()) {
								result.complete(true);
							}else {
								result.complete(false);
							}
						}
						
					});
				}
				else {
					mongoCollection.insertOne(dbInfoAsDocument, new SingleResultCallback<Void>() {
						@Override
						public void onResult(Void voidElement, Throwable error) {
							if (error == null) {
								result.complete(true);
							}else {
								result.complete(false);
							}
						}
						
					});
				}
			}
		}
		try {
			isSuccess = result.get();
		} catch (InterruptedException | ExecutionException e) {
			JdbpLogger.logError(JdbpLoggerConstants.NOSQL, "MongoDB Callback Failed", e);
		}
		return isSuccess;
	}

	protected boolean executeNoSqlUpdateMany(String destinationTableName, List<DBInfo> dbInfos) {
		boolean isSuccess = true;
		if(SqlUtil.isMongoDriver(getDriverName())) {
			if(getNoSqlConnection().isConnectionAvailable()) {
				for(DBInfo dbInfo: dbInfos) {
					try {
						executeNoSqlUpdateOne(destinationTableName, dbInfo);
					}
					catch(JdbpException e) {
						isSuccess = false;
						JdbpLogger.logInfo(JdbpLoggerConstants.NOSQL, e);
					}
				}
			}
		}
		return isSuccess;
	}

	protected List<DBInfo> executeNoSqlFindTopN(String destinationTableName, Class<? extends DBInfo> containerClass, int topN, Map<String, Object> equalityFiltersForFind) throws JdbpException {
		List<DBInfo> dbInfos = new ArrayList<>(topN);
		if(SqlUtil.isMongoDriver(getDriverName())) {
			NoSqlDataSource noSqlDataSource = getNoSqlConnection();
			if(noSqlDataSource != null) {
				MongoDatabase mongoDatabase = noSqlDataSource.getMongoDatabase();
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(destinationTableName);
				dbInfos = DBInfoTransposer.executeConditionalFindAndReturnsDBInfos(mongoCollection, containerClass, new Document(equalityFiltersForFind));
			}
		}
		return dbInfos.isEmpty() ? dbInfos : dbInfos.subList(0, topN);
	}
	
	protected List<DBInfo> executeNoSqlFindTopN(String destinationTableName, Class<? extends DBInfo> containerClass, int topN) throws JdbpException {
		List<DBInfo> dbInfos = new ArrayList<>(topN);
		if(SqlUtil.isMongoDriver(getDriverName())) {
			NoSqlDataSource noSqlDataSource = getNoSqlConnection();
			if(noSqlDataSource != null) {
				MongoDatabase mongoDatabase = noSqlDataSource.getMongoDatabase();
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(destinationTableName);
				dbInfos = DBInfoTransposer.executeUnconditionalFindAndReturnDBInfos(mongoCollection, containerClass);
			}
		}
		return dbInfos.isEmpty() ? dbInfos : dbInfos.subList(0, topN);
	}

}
