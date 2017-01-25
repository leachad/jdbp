package jdbp.db.statement.syntax.crud;

/**
 * @since 1.24.17
 * @author andrew.leach
 */
public enum CrudKeyword {
	INTO("into"), DATABASE("database"), TABLE("table"), INDEX("index"), VALUES("values"), FROM("from");
	private String keyword;

	CrudKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	/**
	 * Used to retrieve matching keyword based on a string keyword
	 * 
	 * @param operation
	 * @return
	 */
	public static CrudKeyword findMatchingKeyword(String keyword) {
		CrudKeyword[] crudKeywords = CrudKeyword.values();
		for(CrudKeyword crudKeyword: crudKeywords) {
			if(keyword.toLowerCase().contains(crudKeyword.getKeyword().toLowerCase())) {
				return crudKeyword;
			}
		}
		return null;
	}
}