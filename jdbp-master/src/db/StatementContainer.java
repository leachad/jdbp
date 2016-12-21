/**
 * 
 */
package db;

/**
 * @author andrew.leach
 */
public class StatementContainer {
	private String rawCallableStatement;

	public StatementContainer() {

	}

	public String getRawCallableStatement() {
		return rawCallableStatement;
	}

	public void setRawCallableStatement(String rawCallableStatement) {
		this.rawCallableStatement = rawCallableStatement;
	}
}
