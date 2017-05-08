package com.andrewdleach.jdbp.statement.syntax.crud;

public enum CrudClause {

	WHERE("where"), OFFSET("offset"), LIMIT("limit"), AND("and"), SET("set"), ORDER_BY("order by");
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
			if(clause.toLowerCase().replaceAll("[_]", " ").contains(crudClause.getClause().toLowerCase())) {
				return crudClause;
			}
		}
		return null;

	}
}
