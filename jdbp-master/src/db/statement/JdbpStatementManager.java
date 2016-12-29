/**
 * 
 */
package db.statement;

/**
 * @author andrew.leach
 */
public class JdbpStatementManager {

	/**
	 * @author andrew.leach
	 */
	public enum CrudKey {
		CREATE("create"), SELECT("select"), UPDATE("update"), DELETE("delete");
		private String operation;

		CrudKey(String operation) {
			this.operation = operation;
		}

		public String getOperation() {
			return operation;
		}
	}

}
