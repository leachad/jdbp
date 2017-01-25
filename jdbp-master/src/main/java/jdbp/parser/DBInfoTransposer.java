/**
 * 
 */
package jdbp.parser;

import java.lang.reflect.Field;
import java.util.List;

import jdbp.db.model.DBInfo;
import jdbp.db.statement.CrudStatementManager;
import jdbp.db.statement.syntax.crud.CrudDelimiter;
import jdbp.db.statement.syntax.crud.CrudOperation;

/**
 * @since 1.24.17
 * @author andrew.leach
 */
public class DBInfoTransposer {

	/**
	 * Utility method to effectively do the opposite of the ResultSetTransposer when SqlQuery or SqlStatement require info objects to populate the
	 * database. Uses the CrudStatementManager to further abstract the idea of statement creation. TODO CrudStatementManager should take two additional
	 * params: String of columns requiring values and String of value tuples to insert, update, delete etc.
	 * 
	 * @param schemaName
	 * @param destinationTable
	 * @param crudOperation
	 * @param infosToConvert
	 * @return
	 */
	public static String convertDbInfosToSQLString(String schemaName, String destinationTable, CrudOperation crudOperation, List<DBInfo> infosToConvert) {
		String sqlString = null;
		switch(crudOperation) {
			case CREATE:
				sqlString = null; // not yet supported
				break;
			case DELETE:
				sqlString = null; // not yet supported
				break;
			case SELECT:
				sqlString = null; // not yet supported
				break;
			case UPDATE:
				sqlString = null; // not yet supported
				break;
			case INSERT:
				sqlString = buildInsertSQLString(schemaName, destinationTable, infosToConvert);
				break;
			case ALTER:
				sqlString = null; // not yet supported
				break;
			case DROP:
				sqlString = null; // not yet supported
				break;
			default:
				break;

		}
		return sqlString;
	}

	private static String buildInsertSQLString(String schemaName, String destinationTable, List<DBInfo> infosToConvert) {
		CrudDelimiter sequenceDelimiter = CrudDelimiter.COMMA;

		DBInfo infoToConvert = infosToConvert.get(0);
		Field[] fieldsToConvert = infoToConvert.getClass().getDeclaredFields();
		List<String> convertedFieldNames = infoToConvert.convertCamelCaseAttributesToSql(fieldsToConvert);
		StringBuilder columnNamesToReplaceInTemplate = new StringBuilder();
		for(int i = 0; i < convertedFieldNames.size(); i++) {
			String nextColumn = convertedFieldNames.get(i);
			columnNamesToReplaceInTemplate.append(nextColumn);
			if(i < convertedFieldNames.size() - 1) {
				columnNamesToReplaceInTemplate.append(sequenceDelimiter.getDelimiter());
			}
		}

		StringBuilder columnValuesToReplaceInTemplate = new StringBuilder();
		for(int i = 0; i < infosToConvert.size(); i++) {
			columnValuesToReplaceInTemplate.append(CrudDelimiter.LEFT_PAREN.getDelimiter());
			columnValuesToReplaceInTemplate.append(infosToConvert.get(i).toCommaSeparatedString());

			columnValuesToReplaceInTemplate.append(CrudDelimiter.RIGHT_PAREN.getDelimiter());
			if(i < infosToConvert.size() - 1) {
				columnValuesToReplaceInTemplate.append(sequenceDelimiter.getDelimiter());
			}
		}

		return CrudStatementManager.buildInsertSQLStatement(schemaName, destinationTable, columnNamesToReplaceInTemplate.toString(), columnValuesToReplaceInTemplate.toString());
	}

}
