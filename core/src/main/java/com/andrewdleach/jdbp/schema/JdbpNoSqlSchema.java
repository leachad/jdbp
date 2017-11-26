package com.andrewdleach.jdbp.schema;

import java.util.List;
import java.util.Map;

import com.andrewdleach.jdbp.connection.JdbpSchemaConnectionManagerProperties;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.model.DBInfo;

public class JdbpNoSqlSchema extends AbstractSchema {

	protected JdbpNoSqlSchema(String schemaName, String driverName, JdbpSchemaConnectionManagerProperties connectionManagerProperties) {
		super(schemaName, driverName, connectionManagerProperties);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getCollection(String destinationTableName, Class<? extends DBInfo> containerClass) throws JdbpException {
		return (List<T>)executeNoSqlGet(destinationTableName, containerClass);
	}

	public boolean upsertMany(String destinationTableName, List<DBInfo> dbInfoCollection) throws JdbpException {
		return executeNoSqlUpdateMany(destinationTableName, dbInfoCollection);
	}

	@SuppressWarnings("unchecked")
	public <T> T findOne(String destinationTableName, Class<? extends DBInfo> containerClass, Map<String, Object> equalityFiltersForFind) throws JdbpException {
		List<T> dbInfos = (List<T>)executeNoSqlFindTopN(destinationTableName, containerClass, 1, equalityFiltersForFind);
		return dbInfos.isEmpty() ? null : dbInfos.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> findAll(String destinationTableName, Class<? extends DBInfo> containerClass, Map<String, Object> equalityFiltersForFind) throws JdbpException {
		return (List<T>)executeNoSqlFindAll(destinationTableName, containerClass, equalityFiltersForFind);
	}

	public boolean upsertOne(String destinationTableName, DBInfo dbInfo) throws JdbpException {
		return executeNoSqlUpdateOne(destinationTableName, dbInfo);
	}
	
	public boolean upsertPush(String destinationTableName, Map<String, Object> queryOperation, Map<String, Object> pushOperation) throws JdbpException {
		return executeNoSqlUpdatePushAttributes(destinationTableName, queryOperation, pushOperation);
	}

}
