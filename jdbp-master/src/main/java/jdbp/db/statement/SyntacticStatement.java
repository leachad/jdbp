package jdbp.db.statement;

import java.util.List;

import jdbp.db.model.DBInfo;
import jdbp.db.statement.StatementManager.CrudDelimiter;
import jdbp.db.statement.StatementManager.CrudKeyword;

public interface SyntacticStatement {

	void addKeyword(CrudKeyword crudKeyword);

	void addDelimiter(CrudDelimiter crudDelimiter);

	void setStatementSyntax(String statementSyntax);

	void addDynamicValue(CrudDynamicValue crudDynamicValue);

	void appendDBInfos(List<Class<? extends DBInfo>> infosToConvert);

	void addPlainText(String retVal);
}
