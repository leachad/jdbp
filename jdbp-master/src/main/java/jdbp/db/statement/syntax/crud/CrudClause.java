package jdbp.db.statement.syntax.crud;

public enum CrudClause {

	WHERE("where"), OFFSET("offset"), LIMIT("limit"), AND("and");
	private String clause;

	CrudClause(String clause) {
		this.clause = clause;
	}

	public String getClause() {
		return clause;
	}

	public static CrudClause findFirstMatchingClause(String clause) {
		CrudClause[] crudClauses = CrudClause.values();
		for(CrudClause crudClause: crudClauses) {
			if(clause.toLowerCase().contains(crudClause.getClause().toLowerCase())) {
				return crudClause;
			}
		}
		return null;

	}
}
