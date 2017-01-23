/**
 * 
 */
package jdbp.db.statement;

import java.io.Serializable;

/**
 * @author andrew.leach
 */
public class StatementContainer implements Serializable {
	private String schemaName;
	private String tableName;
	private String typedCallableStatement;
	private String rawCallableStatement;

	public StatementContainer() {

	}

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
}
