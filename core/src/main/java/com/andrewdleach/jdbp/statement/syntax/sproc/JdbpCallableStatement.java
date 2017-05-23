/**
 * 
 */
package com.andrewdleach.jdbp.statement.syntax.sproc;

import com.andrewdleach.jdbp.statement.syntax.crud.CrudOperation;

/**
 * @author andrew.leach
 */
public class JdbpCallableStatement extends AbstractCallableStatement {
	private static final long serialVersionUID = 6078632163625536641L;
	private CrudOperation crudOperation;
	private String schemaName;
	private String tableName;
	private String typedCallableStatement;
	private String rawCallableStatement;

	public JdbpCallableStatement() {}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public void setTypedCallableStatement(String typedCallableStatement) {
		this.typedCallableStatement = typedCallableStatement;
	}

	public void setRawCallableStatement(String rawCallableStatement) {
		this.rawCallableStatement = rawCallableStatement;
	}

	public String getRawCallableStatement() {
		return rawCallableStatement;
	}

	public CrudOperation getCrudOperation() {
		return crudOperation;
	}

	public void setCrudOperation(CrudOperation crudOperation) {
		this.crudOperation = crudOperation;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getTypedCallableStatement() {
		return typedCallableStatement;
	}
}
