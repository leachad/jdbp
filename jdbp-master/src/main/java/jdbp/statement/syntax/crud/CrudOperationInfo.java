package jdbp.statement.syntax.crud;

public class CrudOperationInfo {
	private CrudOperation crudOperation;
	private String unsanitizedClause;

	public CrudOperationInfo(CrudOperation crudOperation) {
		this(crudOperation, null);
	}

	public CrudOperationInfo(CrudOperation crudOperation, String unsanitizedClause) {
		this.crudOperation = crudOperation;
		this.unsanitizedClause = unsanitizedClause;
	}

	public CrudOperation getCrudOperation() {
		return crudOperation;
	}

	public String getUnsanitizedClause() {
		return unsanitizedClause;
	}

}
