/**
 * 
 */
package jdbp.db.schema;

import java.util.List;

import jdbp.db.connection.ConnectionManagerProperties;
import jdbp.db.model.DBInfo;
import jdbp.db.statement.syntax.crud.CrudOperation;
import jdbp.db.statement.syntax.sproc.JdbpCallableStatement;
import jdbp.exception.JdbpException;

/**
 * @author andrew.leach
 */
public class JdbpSchema extends AbstractSchema {

	private String schemaName;
	private List<JdbpCallableStatement> statements;

	/**
	 * @param schemaName
	 */
	public JdbpSchema(String schemaName, ConnectionManagerProperties connectionManagerProperties) {
		super(schemaName, connectionManagerProperties);
		this.schemaName = schemaName;
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
		return executePreparedQuery(CrudOperation.SELECT, destinationTableName, clause, containerClass);
	}

	/**
	 * @param destinationTableName
	 * @param infosToInsert
	 * @return isSuccess
	 * @throws JdbpException
	 */
	public boolean executeInsert(String destinationTableName, List<DBInfo> infosToInsert) throws JdbpException {
		return executePreparedUpdate(CrudOperation.INSERT, destinationTableName, infosToInsert);
	}

	/**
	 * @param destinationTableName
	 * @param infosToUpdate
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeUpdate(String destinationTableName, List<DBInfo> infosToUpdate) throws JdbpException {
		return executePreparedUpdate(CrudOperation.UPDATE, destinationTableName, infosToUpdate);
	}

	/**
	 * @param destinationTableName
	 * @param infosToUpdate
	 * @return
	 * @throws JdbpException
	 */
	public boolean executeDelete(String destinationTableName, List<DBInfo> infosToUpdate) throws JdbpException {
		return executePreparedUpdate(CrudOperation.DELETE, destinationTableName, infosToUpdate);
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

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public void setAvailableStatements(List<JdbpCallableStatement> statements) {
		this.statements = statements;
	}

	public List<JdbpCallableStatement> getAvailableStatements() {
		return statements;
	}

}
