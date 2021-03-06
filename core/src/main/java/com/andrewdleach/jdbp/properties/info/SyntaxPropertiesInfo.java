package com.andrewdleach.jdbp.properties.info;

import com.andrewdleach.jdbp.statement.syntax.crud.CrudOperation;
import com.andrewdleach.jdbp.statement.syntax.crud.SyntacticStatement;

public class SyntaxPropertiesInfo {

	private CrudOperation crudOperation;
	private SyntacticStatement syntacticStatement;

	public SyntaxPropertiesInfo() {}

	public CrudOperation getCrudOperation() {
		return crudOperation;
	}

	public void setCrudOperation(CrudOperation crudOperation) {
		this.crudOperation = crudOperation;
	}

	public SyntacticStatement getSyntacticStatement() {
		return syntacticStatement;
	}

	public void setSyntacticStatement(SyntacticStatement syntacticStatement) {
		this.syntacticStatement = syntacticStatement;
	};
}
