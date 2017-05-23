package com.andrewdleach.jdbp.statement.syntax;

public class StatementTuple<K, V> {

	private K statementKey;
	private V statementValue;

	public StatementTuple(K statementKey, V statementValue) {
		this.statementKey = statementKey;
		this.statementValue = statementValue;
	}

	public K getStatementKey() {
		return statementKey;
	}

	public V getStatementValue() {
		return statementValue;
	}
}
