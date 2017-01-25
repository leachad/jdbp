/**
 * 
 */
package jdbp.db.statement;

import jdbp.db.properties.util.SyntaxUtil;
import jdbp.db.schema.SchemaManager;
import jdbp.db.statement.syntax.crud.CrudDelimiter;
import jdbp.db.statement.syntax.crud.CrudDynamicValueKey;
import jdbp.db.statement.syntax.crud.InsertStatement;

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
		String requestedDriverName = SchemaManager.getRequestedDriverName();
		InsertStatement insertStatement = SyntaxUtil.getSyntacticInsertStatement(requestedDriverName);
		String insertStatementTemplate = insertStatement.getStatementTemplate();
		CrudDelimiter crudDelimiter = CrudDelimiter.COMMA;
		insertStatementTemplate = insertStatementTemplate.replaceFirst(CrudDynamicValueKey.SCHEMA_NAME.getDynamicValueKey(), schemaName);
		insertStatementTemplate = insertStatementTemplate.replaceFirst(CrudDynamicValueKey.TABLE_NAME.getDynamicValueKey(), destinationTable);

		if(insertStatementTemplate.contains(CrudDynamicValueKey.COLUMN_NAME.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey())) {
			insertStatementTemplate = insertStatementTemplate.replaceFirst(CrudDynamicValueKey.COLUMN_NAME.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey() + crudDelimiter.getDelimiter(), columnValuesToUpdate);
		}

		if(insertStatementTemplate.contains(CrudDynamicValueKey.COLUMN_VALUE.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey())) {
			insertStatementTemplate = insertStatementTemplate.replaceFirst(CrudDelimiter.LEFT_PAREN.getEscapedDelimiter() + CrudDynamicValueKey.COLUMN_VALUE.getDynamicValueKey() + CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey() + crudDelimiter.getDelimiter() + CrudDelimiter.RIGHT_PAREN.getEscapedDelimiter(), valueTuplesToInsert);
		}
		return insertStatementTemplate;
	}

}
