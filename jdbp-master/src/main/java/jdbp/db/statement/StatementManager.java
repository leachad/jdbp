/**
 * 
 */
package jdbp.db.statement;

/**
 * @author andrew.leach
 */
public class StatementManager {

	/**
	 * @author andrew.leach
	 */
	public enum CrudOperation {
		CREATE("create"), SELECT("select"), UPDATE("update"), DELETE("delete");
		private String operation;

		CrudOperation(String operation) {
			this.operation = operation;
		}

		public String getOperation() {
			return operation;
		}
	}

}
