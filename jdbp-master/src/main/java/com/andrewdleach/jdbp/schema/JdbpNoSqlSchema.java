package com.andrewdleach.jdbp.schema;

import java.util.List;

import com.andrewdleach.jdbp.connection.ConnectionManagerProperties;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.model.DBInfo;

public class JdbpNoSqlSchema extends AbstractSchema {
	private String schemaName;

	protected JdbpNoSqlSchema(String schemaName, ConnectionManagerProperties connectionManagerProperties) {
		super(schemaName, connectionManagerProperties);
		this.schemaName = schemaName;
	}

	public List<DBInfo> getCollection(String destinationTableName, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executeNoSqlGet(destinationTableName, containerClass);
	}

	public List<DBInfo> insertCollection(String destinationTableName, List<DBInfo> dbInfoCollection, Class<? extends DBInfo> containerClass) throws JdbpException {
		return executeNoSqlInsert(destinationTableName, dbInfoCollection, containerClass);
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

}
