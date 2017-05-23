/**
 * 
 */
package com.andrewdleach.jdbp.schema;

import java.util.Collections;
import java.util.List;

import com.andrewdleach.jdbp.connection.JdbpSchemaConnectionManagerProperties;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.model.DBInfo;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudOperation;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudOperationInfo;
import com.andrewdleach.jdbp.statement.syntax.sproc.JdbpCallableStatement;

/**
 * @author andrew.leach
 */
public class JdbpSchema extends AbstractSchema {

	/**
	 * @param schemaName
	 */
	public JdbpSchema(String schemaName, String driverName, JdbpSchemaConnectionManagerProperties connectionManagerProperties) {
		super(schemaName, driverName, connectionManagerProperties);
	}

	/**
	 * @param rawQueryString
	 * @param containerClass
	 *        is the container class definition for the query results returned
	 * @return List<DBInfo>
	 * @throws JdbpException
	 */
	public List<DBInfo> executeQuery(String rawQueryString, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executeRawQueryStatement(rawQueryString, containerClass);
	}

	/**
	 * @param destinationTableName
	 * @param clause
	 *        (Must be a commaSeparatedString with tuples defined like id=1,name=someName,etc...)
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	public List<DBInfo> executeSelect(String destinationTableName, String clause, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executePreparedQuery(new CrudOperationInfo(CrudOperation.SELECT, clause), destinationTableName, containerClass);
	}

	/**
	 * @param destinationTableName
	 * @param containerClass
	 * @return
	 * @throws JdbpException
	 */
	public List<DBInfo> executeSelect(String destinationTableName, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executePreparedQuery(new CrudOperationInfo(CrudOperation.SELECT), destinationTableName, containerClass);
	}

	/**
	 * @param destinationTableName
	 * @param clause
	 *        (Must be a commaSeparatedString with tuples defined like id=1,name=someName,etc...)
	 * @param infosToInsert
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeInsert(String destinationTableName, String clause, List<DBInfo> infosToInsert) throws JdbpException {
		return executePreparedUpdate(new CrudOperationInfo(CrudOperation.INSERT, clause), destinationTableName, infosToInsert);
	}

	/**
	 * @param destinationTableName
	 * @param infosToInsert
	 * @return isSuccess
	 * @throws JdbpException
	 */
	public boolean executeInsert(String destinationTableName, List<DBInfo> infosToInsert) throws JdbpException {
		return executePreparedUpdate(new CrudOperationInfo(CrudOperation.INSERT), destinationTableName, infosToInsert);
	}

	/**
	 * @param destinationTableName
	 * @param clause
	 *        (Must be a commaSeparatedString with tuples defined like id=1,name=someName,etc...)
	 * @param infoToUpdate
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeUpdate(String destinationTableName, String clause, DBInfo infoToUpdate) throws JdbpException {
		return executePreparedUpdate(new CrudOperationInfo(CrudOperation.UPDATE, clause), destinationTableName, Collections.singletonList(infoToUpdate));
	}

	/**
	 * @param destinationTableName
	 * @param infoToUpdate
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeUpdate(String destinationTableName, DBInfo infoToUpdate) throws JdbpException {
		return executePreparedUpdate(new CrudOperationInfo(CrudOperation.UPDATE), destinationTableName, Collections.singletonList(infoToUpdate));
	}

	/**
	 * @param destinationTableName
	 * @param clause
	 *        (Must be a commaSeparatedString with tuples defined like id=1,name=someName,etc...)
	 * @param infosToUpdate
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeDelete(String destinationTableName, String clause, List<DBInfo> infosToUpdate) throws JdbpException {
		return executePreparedUpdate(new CrudOperationInfo(CrudOperation.DELETE, clause), destinationTableName, infosToUpdate);
	}

	/**
	 * @param destinationTableName
	 * @param infosToUpdate
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeDelete(String destinationTableName, List<DBInfo> infosToUpdate) throws JdbpException {
		return executePreparedUpdate(new CrudOperationInfo(CrudOperation.DELETE), destinationTableName, infosToUpdate);
	}

	/**
	 * @param procedureName
	 * @param containerClass
	 * @return List<DBInfo>
	 * @throws JdbpException
	 */
	public List<DBInfo> executeStoredProcedure(String procedureName, Class<? extends DBInfo> containerClass) throws JdbpException {
		JdbpCallableStatement statementInfo = prepareStatementInfo(procedureName);
		return executeCallableStatement(procedureName, statementInfo, containerClass);

	}

}
