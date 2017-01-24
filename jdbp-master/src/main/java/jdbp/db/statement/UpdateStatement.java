package jdbp.db.statement;

import java.util.List;

import jdbp.db.model.DBInfo;
import jdbp.db.statement.StatementManager.CrudDelimiter;
import jdbp.db.statement.StatementManager.CrudKeyword;

public class UpdateStatement implements SyntacticStatement {

	@Override
	public void addKeyword(CrudKeyword crudKeyword) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDelimiter(CrudDelimiter crudDelimiter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatementSyntax(String statementSyntax) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDynamicValue(CrudDynamicValue crudDynamicValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void appendDBInfos(List<Class<? extends DBInfo>> infosToConvert) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPlainText(String retVal) {
		// TODO Auto-generated method stub

	}

}
