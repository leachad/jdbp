package jdbp.db.statement.syntax.crud;

public enum CrudDynamicValueKey {
	SCHEMA_NAME("SCHEMA_NAME"), TABLE_NAME("TABLE_NAME"), COLUMN_NAME("COLUMN_NAME"), COLUMN_VALUE("COLUMN_VALUE"), CLAUSE_VALUE("CLAUSE_VALUE"), ALLOWS_MULTIPLES("...");

	private String dynamicValueKey;

	CrudDynamicValueKey(String dynamicValueKey) {
		this.dynamicValueKey = dynamicValueKey;
	}

	public String getDynamicValueKey() {
		return dynamicValueKey;
	}

	public static CrudDynamicValueKey findFirstMatchingDynamicValueKey(String key) {
		CrudDynamicValueKey[] crudDynamicValueKeys = CrudDynamicValueKey.values();
		for(CrudDynamicValueKey crudDynamicValueKey: crudDynamicValueKeys) {
			if(key.toLowerCase().contains(crudDynamicValueKey.getDynamicValueKey().toLowerCase())) {
				return crudDynamicValueKey;
			}
		}
		return null;
	}
}