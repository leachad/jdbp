/**
 * 
 */
package jdbp.db.statement;

/**
 * @author andrew.leach
 */
public class StatementManager {

	/**
	 * @since 1.24.17
	 * @author andrew.leach
	 */
	public enum CrudOperation {
		CREATE("create", CreateStatement.class), SELECT("select", SelectStatement.class), UPDATE("update", UpdateStatement.class), DELETE("delete", DeleteStatement.class), INSERT("insert", InsertStatement.class), ALTER("alter", AlterStatement.class), DROP("drop", DropStatement.class);
		private String operation;
		private Class<? extends SyntacticStatement> statementClass;

		CrudOperation(String operation, Class<? extends SyntacticStatement> statementClass) {
			this.operation = operation;
			this.statementClass = statementClass;
		}

		public String getOperation() {
			return operation;
		}

		/**
		 * Used to retrieve matching operation based on a string operation
		 * 
		 * @param operation
		 * @return
		 */
		public static CrudOperation findMatchingOperation(String operation) {
			CrudOperation[] crudOperations = CrudOperation.values();
			for(CrudOperation crudOperation: crudOperations) {
				if(operation.toLowerCase().contains(crudOperation.getOperation().toLowerCase())) {
					return crudOperation;
				}
			}
			return null;

		}

		public Class<? extends SyntacticStatement> getStatementClass() {
			return statementClass;
		}
	}

	/**
	 * @since 1.24.17
	 * @author andrew.leach
	 */
	public enum CrudKeyword {
		INTO("into"), DATABASE("database"), TABLE("table"), INDEX("index"), VALUES("values");
		private String keyword;

		CrudKeyword(String keyword) {
			this.keyword = keyword;
		}

		public String getKeyword() {
			return keyword;
		}

		/**
		 * Used to retrieve matching keyword based on a string keyword
		 * 
		 * @param operation
		 * @return
		 */
		public static CrudKeyword findMatchingKeyword(String keyword) {
			CrudKeyword[] crudKeywords = CrudKeyword.values();
			for(CrudKeyword crudKeyword: crudKeywords) {
				if(keyword.toLowerCase().contains(crudKeyword.getKeyword().toLowerCase())) {
					return crudKeyword;
				}
			}
			return null;
		}
	}

	public enum CrudDelimiter {
		LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_PAREN("("), RIGHT_PAREN(")"), COMMA(","), PERIOD("."), SEMICOLON(";");
		private String delimiter;

		CrudDelimiter(String delimiter) {
			this.delimiter = delimiter;
		}

		public String getDelimiter() {
			return delimiter;
		}

		public static CrudDelimiter findMatchingDelimiter(String delimiter) {
			CrudDelimiter[] crudDelimiters = CrudDelimiter.values();
			for(CrudDelimiter crudDelimiter: crudDelimiters) {
				if(delimiter.toLowerCase().contains(crudDelimiter.getDelimiter().toLowerCase())) {
					return crudDelimiter;
				}
			}
			return null;
		}
	}

	public enum CrudDynamicValueKey {
		SCHEMA_NAME("SCHEMA_NAME"), TABLE_NAME("TABLE_NAME"), COLUMN_NAME("COLUMN_NAME"), COLUMN_VALUE("COLUMN_VALUE"), ALLOWS_MULTIPLES("...");

		private String crudDynamicValueKey;

		CrudDynamicValueKey(String crudDynamicValueKey) {
			this.crudDynamicValueKey = crudDynamicValueKey;
		}

		public String getCrudDynamicValueKey() {
			return crudDynamicValueKey;
		}

		public static CrudDynamicValueKey findFirstMatchingDynamicValueKey(String key) {
			CrudDynamicValueKey[] crudDynamicValueKeys = CrudDynamicValueKey.values();
			for(CrudDynamicValueKey crudDynamicValueKey: crudDynamicValueKeys) {
				if(key.toLowerCase().contains(crudDynamicValueKey.getCrudDynamicValueKey().toLowerCase())) {
					return crudDynamicValueKey;
				}
			}
			return null;
		}
	}

}
