package jdbp.db.statement.syntax.crud;

public interface SyntacticStatement {

	void addKeyword(CrudKeyword crudKeyword);

	void addDelimiter(CrudDelimiter crudDelimiter);

	void addDynamicValue(CrudDynamicValue crudDynamicValue);

	void addPlainText(String retVal);

	void addClause(CrudClause crudClause);

	void constructStatementTemplate();

	String getStatementTemplate();

}
