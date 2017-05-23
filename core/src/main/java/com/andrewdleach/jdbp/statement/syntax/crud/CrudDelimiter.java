package com.andrewdleach.jdbp.statement.syntax.crud;

public enum CrudDelimiter {
	LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_PAREN("("), RIGHT_PAREN(")"), COMMA(","), PERIOD("."), SEMICOLON(";"), ASSIGNMENT("=");
	private String delimiter;

	CrudDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public String getEscapedDelimiter() {
		return "\\" + delimiter;
	}

	public static CrudDelimiter findMatchingDelimiter(String delimiter) {
		CrudDelimiter[] crudDelimiters = CrudDelimiter.values();
		for(CrudDelimiter crudDelimiter: crudDelimiters) {
			if(delimiter.toLowerCase().contains(crudDelimiter.getDelimiter().toLowerCase())) {
				return crudDelimiter;
			}
		}
		return null;
	}
}