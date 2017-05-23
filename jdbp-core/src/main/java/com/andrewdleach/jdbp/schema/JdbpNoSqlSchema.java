package com.andrewdleach.jdbp.schema;

import java.util.List;

import com.andrewdleach.jdbp.connection.JdbpSchemaConnectionManagerProperties;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.model.DBInfo;

public class JdbpNoSqlSchema extends AbstractSchema {

	protected JdbpNoSqlSchema(String schemaName, String driverName, JdbpSchemaConnectionManagerProperties connectionManagerProperties) {
		super(schemaName, driverName, connectionManagerProperties);
	}

	public List<DBInfo> getCollection(String destinationTableName, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executeNoSqlGet(destinationTableName, containerClass);
	}

	public boolean insertCollection(String destinationTableName, List<DBInfo> dbInfoCollection, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executeNoSqlUpdate(destinationTableName, dbInfoCollection, containerClass);
	}

}
