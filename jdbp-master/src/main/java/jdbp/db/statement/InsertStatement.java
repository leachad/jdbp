package jdbp.db.statement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import jdbp.db.model.DBInfo;
import jdbp.db.statement.StatementManager.CrudDelimiter;
import jdbp.db.statement.StatementManager.CrudKeyword;
import jdbp.db.statement.StatementManager.CrudOperation;

public class InsertStatement implements SyntacticStatement {

	private String insertStatementSyntax;
	private CrudOperation crudOperation;
	private int currentTupleIndex;
	private Deque<StatementTuple<Integer, CrudKeyword>> crudKeywords;
	private Deque<StatementTuple<Integer, CrudDelimiter>> crudDelimiters;
	private Deque<StatementTuple<Integer, CrudDynamicValue>> crudDynamicValues;
	private Deque<StatementTuple<Integer, String>> crudPlainText;

	public InsertStatement() {
		crudOperation = CrudOperation.INSERT;
		crudKeywords = new ArrayDeque<>();
		crudDelimiters = new ArrayDeque<>();
	}

	@Override
	public void appendDBInfos(List<Class<? extends DBInfo>> infosToConvert) {
		// extract field names as represented in the db

	}

	@Override
	public void addKeyword(CrudKeyword crudKeyword) {
		crudKeywords.add(new StatementTuple<Integer, CrudKeyword>(currentTupleIndex, crudKeyword));
		currentTupleIndex++;
	}

	@Override
	public void addDelimiter(CrudDelimiter crudDelimiter) {
		crudDelimiters.add(new StatementTuple<Integer, CrudDelimiter>(currentTupleIndex, crudDelimiter));
		currentTupleIndex++;

	}

	@Override
	public void addDynamicValue(CrudDynamicValue crudDynamicValue) {
		crudDynamicValues.add(new StatementTuple<Integer, CrudDynamicValue>(currentTupleIndex, crudDynamicValue));
		currentTupleIndex++;
	}

	@Override
	public void setStatementSyntax(String statementSyntax) {
		this.insertStatementSyntax = statementSyntax;

	}

	@Override
	public void addPlainText(String retVal) {
		crudPlainText.add(new StatementTuple<Integer, String>(currentTupleIndex, retVal));
		currentTupleIndex++;
	}

}
