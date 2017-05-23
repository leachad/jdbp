/**
 * 
 */
package com.andrewdleach.jdbp.statement;

import com.andrewdleach.jdbp.driver.DriverStorage;
import com.andrewdleach.jdbp.properties.util.DriverUtil;
import com.andrewdleach.jdbp.properties.util.SyntaxUtil;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudClause;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudDelimiter;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudDynamicValueKey;
import com.andrewdleach.jdbp.statement.syntax.crud.InsertStatement;
import com.andrewdleach.jdbp.statement.syntax.crud.SelectStatement;

/**
 * @author andrew.leach
 */
public class CrudStatementManager {

	/**
	 * @param schemaName
	 * @param destinationTable
	 * @param infosToConvert
	 * @return
	 */
	public static String buildInsertSQLStatement(String schemaName, String destinationTable, String columnValuesToUpdate, String valueTuplesToInsert) {
		String requestedDriverName = DriverStorage.getRequestedDriverName();
		InsertStatement insertStatement = SyntaxUtil.getSyntacticInsertStatement(requestedDriverName);
		String insertStatementTemplate = insertStatement.getStatementTemplate();
		insertStatementTemplate = insertStatementTemplate.replaceFirst(CrudDynamicValueKey.SCHEMA_NAME.getDynamicValueKey(), schemaName);
		insertStatementTemplate = insertStatementTemplate.replaceFirst(CrudDynamicValueKey.TABLE_NAME.getDynamicValueKey(), destinationTable);

		if(insertStatementTemplate.contains(CrudDynamicValueKey.COLUMN_NAME.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey())) {
			insertStatementTemplate = insertStatementTemplate.replaceFirst(CrudDynamicValueKey.COLUMN_NAME.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey() + CrudDelimiter.COMMA.getDelimiter(), columnValuesToUpdate);
		}

		if(insertStatementTemplate.contains(CrudDynamicValueKey.COLUMN_VALUE.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey())) {
			insertStatementTemplate = insertStatementTemplate.replaceFirst(CrudDelimiter.LEFT_PAREN.getEscapedDelimiter() + CrudDynamicValueKey.COLUMN_VALUE.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey() + CrudDelimiter.COMMA.getDelimiter() + CrudDelimiter.RIGHT_PAREN.getEscapedDelimiter(), valueTuplesToInsert);
		}
		return insertStatementTemplate;
	}

	public static String buildSelectSQLStatement(String schemaName, String destinationTable, String columnValuesToUpdate, String clauseToRestrictResults) {
		String requestedDriverName = DriverStorage.getRequestedDriverName();
		SelectStatement selectStatement = SyntaxUtil.getSyntacticSelectStatement(requestedDriverName);
		String selectStatementTemplate = selectStatement.getStatementTemplate();

		selectStatementTemplate = selectStatementTemplate.replaceFirst(CrudDynamicValueKey.SCHEMA_NAME.getDynamicValueKey(), schemaName);
		selectStatementTemplate = selectStatementTemplate.replaceFirst(CrudDynamicValueKey.TABLE_NAME.getDynamicValueKey(), destinationTable);

		if(selectStatementTemplate.contains(CrudDynamicValueKey.COLUMN_NAME.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey())) {
			selectStatementTemplate = selectStatementTemplate.replaceFirst(CrudDynamicValueKey.COLUMN_NAME.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey() + CrudDelimiter.COMMA.getDelimiter(), columnValuesToUpdate);
		}

		String keyValueSubstring = CrudDynamicValueKey.CLAUSE_KEY_VALUE.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey() + " " + CrudClause.AND.getClause();
		if(selectStatementTemplate.contains(keyValueSubstring)) {
			selectStatementTemplate = selectStatementTemplate.replaceFirst(keyValueSubstring, clauseToRestrictResults);
			if(clauseToRestrictResults.length() == 0) {
				selectStatementTemplate = selectStatementTemplate.replaceFirst(CrudClause.WHERE.getClause(), "");
			}
		}

		// if offset is undefined, remove it
		String offSetSubstring = CrudClause.OFFSET.getClause() + " " + CrudDynamicValueKey.CLAUSE_VALUE;
		if(selectStatementTemplate.contains(offSetSubstring)) {
			selectStatementTemplate = selectStatementTemplate.replaceAll(offSetSubstring, "");
		}

		// if limit is undefined, insert the default value from DriverUtil
		String limitSubstring = CrudClause.LIMIT.getClause() + " " + CrudDynamicValueKey.CLAUSE_VALUE;
		if(selectStatementTemplate.contains(limitSubstring)) {
			selectStatementTemplate = selectStatementTemplate.replaceAll(limitSubstring, CrudClause.LIMIT.getClause() + " " + DriverUtil.getDefaultLimit(requestedDriverName));
		}

		return selectStatementTemplate;
	}

	public static String buildUpdateSQLStatement(String schemaName, String destinationTable, String columnValuesToUpdate, String valueTuplesToInsert, String clauseToRestrictResults) {
		// TODO Auto-generated method stub
		return null;
	}

}
