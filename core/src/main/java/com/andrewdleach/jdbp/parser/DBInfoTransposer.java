/**
 * 
 */
package com.andrewdleach.jdbp.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.bson.Document;

import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.logger.JdbpLogger;
import com.andrewdleach.jdbp.logger.JdbpLoggerConstants;
import com.andrewdleach.jdbp.model.DBInfo;
import com.andrewdleach.jdbp.statement.CrudStatementManager;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudClause;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudDelimiter;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudOperation;
import com.andrewdleach.jdbp.statement.syntax.crud.CrudOperationInfo;
import com.andrewdleach.jdbp.tools.JdbpTypeUtil;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Projections;

/**
 * @since 1.24.17
 * @author andrew.leach
 */
public class DBInfoTransposer {

	/**
	 * Utility method to effectively do the opposite of the ResultSetTransposer when SqlQuery or SqlStatement require info objects to populate the
	 * database. Uses the CrudStatementManager to further abstract the idea of statement creation.
	 * 
	 * @param schemaName
	 * @param destinationTable
	 * @param crudOperation
	 * @param infosToConvert
	 * @return
	 * @throws JdbpException
	 */
	public static String constructSQLUpdateString(String schemaName, String destinationTable, CrudOperationInfo crudOperationInfo, List<DBInfo> infosToConvert, String requestedDriverName) throws JdbpException {
		String sqlString = null;
		switch(crudOperationInfo.getCrudOperation()) {
			case UPDATE:
				sqlString = buildUpdateSQLString(schemaName, destinationTable, crudOperationInfo, infosToConvert, requestedDriverName);
				break;
			case INSERT:
				sqlString = buildInsertSQLString(schemaName, destinationTable, crudOperationInfo, infosToConvert, requestedDriverName);
				break;
			default:
				break;

		}
		return sqlString;
	}

	/**
	 * @param schemaName
	 * @param destinationTable
	 * @param crudOperationInfo
	 * @param containerClass
	 * @return
	 */
	public static String constructSQLQueryString(String schemaName, String destinationTable, CrudOperationInfo crudOperationInfo, Class<? extends DBInfo> containerClass, String requestedDriverName) {
		String sqlString = null;
		if(crudOperationInfo.getCrudOperation().equals(CrudOperation.SELECT)) {
			sqlString = buildSelectSQLString(schemaName, destinationTable, crudOperationInfo, containerClass, requestedDriverName);
		}
		return sqlString;
	}

	private static String buildUpdateSQLString(String schemaName, String destinationTable, CrudOperationInfo crudOperationInfo, List<DBInfo> infosToConvert, String requestedDriverName) {
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
		columnValuesToReplaceInTemplate.append(CrudDelimiter.LEFT_PAREN.getDelimiter());
		String commaSepColumnValues = ConversionUtil.toCommaSeparatedString(infoToConvert);
		columnValuesToReplaceInTemplate.append(commaSepColumnValues);
		columnValuesToReplaceInTemplate.append(CrudDelimiter.RIGHT_PAREN.getDelimiter());

		StringBuilder clauseForUpdateStatement = new StringBuilder();

		// TODO Factor in clause creation logic
		return CrudStatementManager.buildUpdateSQLStatement(schemaName, destinationTable, columnNamesToReplaceInTemplate.toString(), columnValuesToReplaceInTemplate.toString(), clauseForUpdateStatement.toString(), requestedDriverName);
	}

	private static String buildInsertSQLString(String schemaName, String destinationTable, CrudOperationInfo crudOperationInfo, List<DBInfo> infosToConvert, String requestedDriverName) {
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

		// TODO Factor in clause creation logic
		return CrudStatementManager.buildInsertSQLStatement(schemaName, destinationTable, columnNamesToReplaceInTemplate.toString(), columnValuesToReplaceInTemplate.toString(), requestedDriverName);
	}

	private static String buildSelectSQLString(String schemaName, String destinationTable, CrudOperationInfo crudOperationInfo, Class<? extends DBInfo> containerClass, String requestedDriverName) {
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
		if(crudOperationInfo.getUnsanitizedClause() != null) {
			String[] splitClauses = crudOperationInfo.getUnsanitizedClause().split("[,]");
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

		return CrudStatementManager.buildSelectSQLStatement(schemaName, destinationTable, columnNamesToReplaceInTemplate.toString(), clauseForSelectStatement.toString(), requestedDriverName);
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

	public static List<Document> convertToDocumentsFromDBInfos(List<DBInfo> dbInfos, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<Document> documents = new ArrayList<>();
		for(DBInfo dbInfo: dbInfos) {
			Document basicDBObject = Document.parse(JdbpTypeUtil.convertDBInfoToJson(dbInfo));
			documents.add(basicDBObject);
		}
		return documents;
	}

	public static List<DBInfo> executeUnconditionalFindAndReturnDBInfos(MongoCollection<Document> mongoCollection, Class<? extends DBInfo> containerClass) throws JdbpException {
		List<DBInfo> dbInfos = new ArrayList<>();
		CompletableFuture<List<DBInfo>> result = new CompletableFuture<>();
		mongoCollection.find().projection(Projections.exclude(JdbpTypeUtil.findNoSqlCollectionExcludedFields(containerClass))).map(Document::toJson).into(new HashSet<String>(), new SingleResultCallback<HashSet<String>>() {

			@Override
			public void onResult(HashSet<String> documentsAsStrings, Throwable t) {
				List<DBInfo> dbInfos = documentsAsStrings.stream().map(jsonString -> {
					return JdbpTypeUtil.convertJsonToDBInfo(jsonString, containerClass);
				}).collect(Collectors.toList());
				result.complete(dbInfos);
			}
		});
		try {
			dbInfos = result.get();
		}
		catch(InterruptedException | ExecutionException e) {
			JdbpLogger.logError(JdbpLoggerConstants.NOSQL, "Could Not Convert Json To Java Object: " + containerClass.getName(), e);
		}
		return dbInfos;
	}
	
	public static List<DBInfo> executeConditionalFindAndReturnsDBInfos(MongoCollection<Document> mongoCollection, Class<? extends DBInfo> containerClass, Document filterDocument) throws JdbpException {
		List<DBInfo> dbInfos = new ArrayList<>();
		CompletableFuture<List<DBInfo>> result = new CompletableFuture<>();
		mongoCollection.find(filterDocument).projection(Projections.exclude(JdbpTypeUtil.findNoSqlCollectionExcludedFields(containerClass))).map(Document::toJson).into(new HashSet<String>(), new SingleResultCallback<HashSet<String>>() {

			@Override
			public void onResult(HashSet<String> documentsAsStrings, Throwable t) {
				List<DBInfo> dbInfos = documentsAsStrings.stream().map(jsonString -> {
					return JdbpTypeUtil.convertJsonToDBInfo(jsonString, containerClass);
				}).collect(Collectors.toList());
				result.complete(dbInfos);
			}
		});
		try {
			dbInfos = result.get();
		}
		catch(InterruptedException | ExecutionException e) {
			JdbpLogger.logError(JdbpLoggerConstants.NOSQL, "Could Not Convert Json To Java Object: " + containerClass.getName(), e);
		}
		return dbInfos;
	}

	public static Document convertToDocumentFromDBInfo(DBInfo dbInfo) throws JdbpException {
		return Document.parse(JdbpTypeUtil.convertDBInfoToJson(dbInfo));
	}
}
