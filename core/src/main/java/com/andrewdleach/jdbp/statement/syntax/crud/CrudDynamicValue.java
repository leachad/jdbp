package com.andrewdleach.jdbp.statement.syntax.crud;

public class CrudDynamicValue {

	private CrudDynamicValueKey crudDynamicValueKey;
	private boolean allowsMultiples;

	public CrudDynamicValue() {}

	public void setCrudDynamicValueKey(CrudDynamicValueKey crudDynamicValueKey) {
		this.crudDynamicValueKey = crudDynamicValueKey;

	}

	public void setAllowsMultiples(boolean allowsMultiples) {
		this.allowsMultiples = allowsMultiples;

	}

	public CrudDynamicValueKey getCrudDynamicValueKey() {
		return crudDynamicValueKey;
	}

	public boolean isAllowsMultiples() {
		return allowsMultiples;
	}
}
