package jdbp.statement.syntax.crud;

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