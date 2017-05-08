package com.andrewdleach.jdbp.statement.syntax.crud;

import java.util.ArrayDeque;
import java.util.Deque;

import com.andrewdleach.jdbp.statement.syntax.StatementTuple;

public class SelectStatement implements SyntacticStatement {

	private static final String WHITESPACE_CHAR = " ";
	private CrudOperation crudOperation;
	private int currentTupleIndex;
	private String statementTemplate;
	private Deque<StatementTuple<Integer, CrudKeyword>> crudKeywords;
	private Deque<StatementTuple<Integer, CrudDelimiter>> crudDelimiters;
	private Deque<StatementTuple<Integer, CrudDynamicValue>> crudDynamicValues;
	private Deque<StatementTuple<Integer, CrudClause>> crudClauses;
	private Deque<StatementTuple<Integer, String>> crudPlainText;

	public SelectStatement() {
		crudOperation = CrudOperation.SELECT;
		crudKeywords = new ArrayDeque<>();
		crudDelimiters = new ArrayDeque<>();
		crudDynamicValues = new ArrayDeque<>();
		crudClauses = new ArrayDeque<>();
		crudPlainText = new ArrayDeque<>();
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
	public void addPlainText(String retVal) {
		crudPlainText.add(new StatementTuple<Integer, String>(currentTupleIndex, retVal));
		currentTupleIndex++;
	}

	@Override
	public void constructStatementTemplate() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(crudOperation.getOperation());
		stringBuilder.append(WHITESPACE_CHAR);
		for(int i = 0; i < currentTupleIndex; i++) {
			if(crudKeywords.peekFirst() != null && crudKeywords.peekFirst().getStatementKey().equals(i)) {
				String retVal = crudKeywords.removeFirst().getStatementValue().getKeyword().trim();
				stringBuilder.append(WHITESPACE_CHAR);
				stringBuilder.append(retVal);
				stringBuilder.append(WHITESPACE_CHAR);
			}
			else if(crudDynamicValues.peekFirst() != null && crudDynamicValues.peekFirst().getStatementKey().equals(i)) {
				CrudDynamicValue dynamicValue = crudDynamicValues.removeFirst().getStatementValue();
				stringBuilder.append(dynamicValue.getCrudDynamicValueKey().getDynamicValueKey());
				if(dynamicValue.isAllowsMultiples()) {
					stringBuilder.append(CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey());
				}
			}
			else if(crudDelimiters.peekFirst() != null && crudDelimiters.peekFirst().getStatementKey().equals(i)) {
				String retVal = crudDelimiters.removeFirst().getStatementValue().getDelimiter().trim();
				stringBuilder.append(retVal);
			}
			else if(crudPlainText.peekFirst() != null && crudPlainText.peekFirst().getStatementKey().equals(i)) {
				String retVal = crudPlainText.removeFirst().getStatementValue().trim();
				stringBuilder.append(retVal);
				stringBuilder.append(WHITESPACE_CHAR);
			}
			else if(crudClauses.peekFirst() != null && crudClauses.peekFirst().getStatementKey().equals(i)) {
				CrudClause crudClause = crudClauses.removeFirst().getStatementValue();
				stringBuilder.append(WHITESPACE_CHAR);
				stringBuilder.append(crudClause.getClause());
				stringBuilder.append(WHITESPACE_CHAR);
			}
		}
		statementTemplate = stringBuilder.toString();
	}

	@Override
	public String getStatementTemplate() {
		return statementTemplate;
	}

	@Override
	public void addClause(CrudClause clause) {
		crudClauses.add(new StatementTuple<Integer, CrudClause>(currentTupleIndex, clause));
		currentTupleIndex++;
	}

}
