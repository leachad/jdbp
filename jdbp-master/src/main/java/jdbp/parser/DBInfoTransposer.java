/**
 * 
 */
package jdbp.parser;

import java.lang.reflect.Field;
import java.util.List;

import jdbp.model.DBInfo;
import jdbp.statement.CrudStatementManager;
import jdbp.statement.syntax.crud.CrudClause;
import jdbp.statement.syntax.crud.CrudDelimiter;
import jdbp.statement.syntax.crud.CrudOperation;

/**
 * @since 1.24.17
 * @author andrew.leach
 */
public class DBInfoTransposer {

	/**
	 * Utility method to effectively do the opposite of the ResultSetTransposer when SqlQuery or SqlStatement require info objects to populate the
	 * database. Uses the CrudStatementManager to further abstract the idea of statement creation. TODO CrudStatementManager should take two
	 * additional params: String of columns requiring values and String of value tuples to insert, update, delete etc.
	 * 
	 * @param schemaName
	 * @param destinationTable
	 * @param crudOperation
	 * @param infosToConvert
	 * @return
	 */
	public static String constructSQLUpdateString(String schemaName, String destinationTable, CrudOperation crudOperation, List<DBInfo> infosToConvert) {
		String sqlString = null;
		switch(crudOperation) {
			case CREATE:
				sqlString = null; // not yet supported
				break;
			case DELETE:
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

	public static String constructSqlQueryString(String schemaName, String destinationTable, CrudOperation crudOperation, String unSanitizedClause, Class<? extends DBInfo> containerClass) {
		String sqlString = null;
		if(crudOperation.equals(CrudOperation.SELECT)) {
			sqlString = buildSelectSQLString(schemaName, destinationTable, unSanitizedClause, containerClass);
		}
		return sqlString;
	}

	private static String buildSelectSQLString(String schemaName, String destinationTable, String unSanitizedClause, Class<? extends DBInfo> containerClass) {
		CrudDelimiter sequenceDelimiter = CrudDelimiter.COMMA;

		Field[] fieldsToSelect = containerClass.getDeclaredFields();
		List<String> convertedFieldNames = ConversionUtil.convertCamelCaseAttributesToSql(fieldsToSelect);
		StringBuilder columnNamesToReplaceInTemplate = new StringBuilder();
		for(int i = 0; i < convertedFieldNames.size(); i++) {
			String nextColumn = convertedFieldNames.get(i);
			columnNamesToReplaceInTemplate.append(nextColumn);
			if(i < convertedFieldNames.size() - 1) {
				columnNamesToReplaceInTemplate.append(sequenceDelimiter.getDelimiter());
			}
		}

		StringBuilder clauseForSelectStatement = new StringBuilder();
		// if num clauses > 1 append 'AND' next clause to sb
		if(unSanitizedClause != null) {
			String[] splitClauses = unSanitizedClause.split("[,]");
			for(int i = 0; i < splitClauses.length; i++) {
				String[] nameAndValue = splitClauses[i].split("[=]");
				if(nameAndValue.length > 1) {
					int equivalentConvertedNameIndex = findEquivalentColumnNameIndex(convertedFieldNames, nameAndValue[0]);
					if(equivalentConvertedNameIndex != -1) {
						String columnNameToUse = convertedFieldNames.get(equivalentConvertedNameIndex);

						clauseForSelectStatement.append(columnNameToUse);
						clauseForSelectStatement.append(CrudDelimiter.ASSIGNMENT.getDelimiter());
						clauseForSelectStatement.append("'" + nameAndValue[1] + "'");
						if(i > 0 && i < (splitClauses.length - 1)) {
							clauseForSelectStatement.append(" ");
							clauseForSelectStatement.append(CrudClause.AND.getClause());
							clauseForSelectStatement.append(" ");
						}

					}
				}

			}
		}

		return CrudStatementManager.buildSelectSQLStatement(schemaName, destinationTable, columnNamesToReplaceInTemplate.toString(), clauseForSelectStatement.toString());
	}

	private static int findEquivalentColumnNameIndex(List<String> convertedFieldNames, String columnName) {
		for(int i = 0; i < convertedFieldNames.size(); i++) {
			String convertedColumnName = convertedFieldNames.get(i);
			if(convertedColumnName.equals(columnName)) {
				return i;
			}
			else {
				String convertedColumnNameToTest = ConversionUtil.convertCamelCaseAttributeToSql(columnName);
				if(convertedColumnName.equals(convertedColumnNameToTest)) {
					return i;
				}
			}
		}
		return -1;
	}

	private static String buildInsertSQLString(String schemaName, String destinationTable, List<DBInfo> infosToConvert) {
		CrudDelimiter sequenceDelimiter = CrudDelimiter.COMMA;

		DBInfo infoToConvert = infosToConvert.get(0);
		Field[] fieldsToConvert = infoToConvert.getClass().getDeclaredFields();
		List<String> convertedFieldNames = ConversionUtil.convertCamelCaseAttributesToSql(fieldsToConvert);
		StringBuilder columnNamesToReplaceInTemplate = new StringBuilder();
		for(int i = 0; i < convertedFieldNames.size(); i++) {
			String nextColumn = convertedFieldNames.get(i);
			if(!nextColumn.equals("id")) {
				columnNamesToReplaceInTemplate.append(nextColumn);
				if(i < convertedFieldNames.size() - 1) {
					columnNamesToReplaceInTemplate.append(sequenceDelimiter.getDelimiter());
				}
			}
		}

		StringBuilder columnValuesToReplaceInTemplate = new StringBuilder();
		for(int i = 0; i < infosToConvert.size(); i++) {
			columnValuesToReplaceInTemplate.append(CrudDelimiter.LEFT_PAREN.getDelimiter());
			String commaSepColumnValues = ConversionUtil.toCommaSeparatedString(infosToConvert.get(i));
			columnValuesToReplaceInTemplate.append(commaSepColumnValues);

			columnValuesToReplaceInTemplate.append(CrudDelimiter.RIGHT_PAREN.getDelimiter());
			if(i < infosToConvert.size() - 1) {
				columnValuesToReplaceInTemplate.append(sequenceDelimiter.getDelimiter());
			}
		}

		return CrudStatementManager.buildInsertSQLStatement(schemaName, destinationTable, columnNamesToReplaceInTemplate.toString(), columnValuesToReplaceInTemplate.toString());
	}

}
