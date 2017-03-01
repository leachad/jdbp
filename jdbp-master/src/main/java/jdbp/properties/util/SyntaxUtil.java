package jdbp.properties.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdbp.properties.info.SyntaxPropertiesInfo;
import jdbp.statement.syntax.SyntaxUtilConstants;
import jdbp.statement.syntax.crud.AlterStatement;
import jdbp.statement.syntax.crud.CreateStatement;
import jdbp.statement.syntax.crud.CrudClause;
import jdbp.statement.syntax.crud.CrudDelimiter;
import jdbp.statement.syntax.crud.CrudDynamicValue;
import jdbp.statement.syntax.crud.CrudDynamicValueKey;
import jdbp.statement.syntax.crud.CrudKeyword;
import jdbp.statement.syntax.crud.CrudOperation;
import jdbp.statement.syntax.crud.DeleteStatement;
import jdbp.statement.syntax.crud.DropStatement;
import jdbp.statement.syntax.crud.InsertStatement;
import jdbp.statement.syntax.crud.SelectStatement;
import jdbp.statement.syntax.crud.SyntacticStatement;
import jdbp.statement.syntax.crud.UpdateStatement;

/**
 * @since 1.24.17
 * @author andrew.leach
 */
public class SyntaxUtil implements PropertySetUtility {
	private SyntaxUtil syntaxUtilInstance;
	private static Map<String, Map<CrudOperation, SyntaxPropertiesInfo>> syntaxUtilProperties = new HashMap<>();

	@Override
	public void readPropertiesForJdbpUtility() {
		ResourceBundle jdbpUtilProps = ResourceBundle.getBundle("resources.jdbpsyntax", Locale.getDefault(), SyntaxUtil.class.getClassLoader());
		Set<String> keySet = jdbpUtilProps.keySet();
		for(String key: keySet) {
			separateKeyIntoSubCategoriesAndBuildPropertyInfo(key, jdbpUtilProps.getString(key));
		}
	}

	@Override
	public PropertySetUtility getInstance() {
		if(syntaxUtilInstance == null) {
			syntaxUtilInstance = new SyntaxUtil();
		}
		return syntaxUtilInstance;
	}

	private void separateKeyIntoSubCategoriesAndBuildPropertyInfo(String key, String value) {
		String[] driverAndCrudOperation = key.split("[.]");

		Map<CrudOperation, SyntaxPropertiesInfo> syntacticStatementsForDriver = syntaxUtilProperties.get(driverAndCrudOperation[0].toLowerCase());
		if(syntacticStatementsForDriver == null) {
			syntacticStatementsForDriver = new HashMap<>();
			syntaxUtilProperties.put(driverAndCrudOperation[0].toLowerCase(), syntacticStatementsForDriver);
		}

		SyntaxPropertiesInfo syntaxPropertiesInfo = buildSyntaxPropertiesInfo(driverAndCrudOperation[1], value);
		syntacticStatementsForDriver.put(syntaxPropertiesInfo.getCrudOperation(), syntaxPropertiesInfo);

	}

	private SyntaxPropertiesInfo buildSyntaxPropertiesInfo(String operation, String value) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = null;
		Matcher matcher = Pattern.compile(SyntaxUtilConstants.RegexConstants.OPERATION_PATTERN).matcher(value);
		while(matcher.find()) {
			syntaxPropertiesInfo = new SyntaxPropertiesInfo();
			CrudOperation crudOperation = CrudOperation.findMatchingOperation(matcher.group());
			syntaxPropertiesInfo.setCrudOperation(crudOperation);
			syntaxPropertiesInfo.setSyntacticStatement(buildSyntacticStatement(crudOperation, value));
		}

		return syntaxPropertiesInfo;
	}

	public static InsertStatement getSyntacticInsertStatement(String driverName) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = syntaxUtilProperties.get(driverName).get(CrudOperation.INSERT);
		SyntacticStatement insertStatement = null;
		if(syntaxPropertiesInfo != null) {
			insertStatement = syntaxPropertiesInfo.getSyntacticStatement();
		}
		return (InsertStatement)insertStatement;
	}

	public static CreateStatement getSyntacticCreateStatement(String driverName) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = syntaxUtilProperties.get(driverName).get(CrudOperation.CREATE);
		SyntacticStatement createStatement = null;
		if(syntaxPropertiesInfo != null) {
			createStatement = syntaxPropertiesInfo.getSyntacticStatement();
		}
		return (CreateStatement)createStatement;
	}

	public static DeleteStatement getSyntacticDeleteStatement(String driverName) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = syntaxUtilProperties.get(driverName).get(CrudOperation.DELETE);
		SyntacticStatement deleteStatement = null;
		if(syntaxPropertiesInfo != null) {
			deleteStatement = syntaxPropertiesInfo.getSyntacticStatement();
		}
		return (DeleteStatement)deleteStatement;
	}

	public static SelectStatement getSyntacticSelectStatement(String driverName) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = syntaxUtilProperties.get(driverName).get(CrudOperation.SELECT);
		SyntacticStatement selectStatement = null;
		if(syntaxPropertiesInfo != null) {
			selectStatement = syntaxPropertiesInfo.getSyntacticStatement();
		}
		return (SelectStatement)selectStatement;
	}

	public static UpdateStatement getSyntacticUpdateStatement(String driverName) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = syntaxUtilProperties.get(driverName).get(CrudOperation.UPDATE);
		SyntacticStatement updateStatement = null;
		if(syntaxPropertiesInfo != null) {
			updateStatement = syntaxPropertiesInfo.getSyntacticStatement();
		}
		return (UpdateStatement)updateStatement;
	}

	public static AlterStatement getSyntacticAlterStatement(String driverName) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = syntaxUtilProperties.get(driverName).get(CrudOperation.ALTER);
		SyntacticStatement alterStatement = null;
		if(syntaxPropertiesInfo != null) {
			alterStatement = syntaxPropertiesInfo.getSyntacticStatement();
		}
		return (AlterStatement)alterStatement;
	}

	public static DropStatement getSyntacticDropStatement(String driverName) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = syntaxUtilProperties.get(driverName).get(CrudOperation.DROP);
		SyntacticStatement dropStatement = null;
		if(syntaxPropertiesInfo != null) {
			dropStatement = syntaxPropertiesInfo.getSyntacticStatement();
		}
		return (DropStatement)dropStatement;
	}

	private SyntacticStatement buildSyntacticStatement(CrudOperation crudOperation, String value) {
		SyntacticStatement statementInstance = null;
		try {
			statementInstance = crudOperation.getStatementClass().newInstance();

			Matcher matcher = Pattern.compile(SyntaxUtilConstants.RegexConstants.OPERATION_PATTERN + "|" + SyntaxUtilConstants.RegexConstants.KEYWORD_PATTERN + "|" + SyntaxUtilConstants.RegexConstants.DELIMITER_PATTERN + "|" + SyntaxUtilConstants.RegexConstants.DYNAMIC_VALUE_PATTERN + "|" + SyntaxUtilConstants.RegexConstants.CLAUSE_PATTERN + "|" + SyntaxUtilConstants.RegexConstants.PLAIN_TEXT_PATTERN).matcher(value);
			while(matcher.find()) {
				String retVal = matcher.group();
				if(retVal.matches(SyntaxUtilConstants.RegexConstants.KEYWORD_PATTERN)) {
					CrudKeyword crudKeyword = CrudKeyword.findMatchingKeyword(retVal);
					statementInstance.addKeyword(crudKeyword);

				}
				else if(retVal.matches(SyntaxUtilConstants.RegexConstants.DELIMITER_PATTERN)) {
					CrudDelimiter crudDelimiter = CrudDelimiter.findMatchingDelimiter(retVal);
					statementInstance.addDelimiter(crudDelimiter);
				}
				else if(retVal.matches(SyntaxUtilConstants.RegexConstants.DYNAMIC_VALUE_PATTERN)) {
					CrudDynamicValue crudDynamicValue = new CrudDynamicValue();
					CrudDynamicValueKey crudDynamicValueKey = CrudDynamicValueKey.findFirstMatchingDynamicValueKey(retVal);
					crudDynamicValue.setCrudDynamicValueKey(crudDynamicValueKey);
					if(retVal.contains(CrudDynamicValueKey.ALLOWS_MULTIPLES.getDynamicValueKey())) {
						crudDynamicValue.setAllowsMultiples(true);
					}

					statementInstance.addDynamicValue(crudDynamicValue);
				}
				else if(retVal.matches(SyntaxUtilConstants.RegexConstants.CLAUSE_PATTERN)) {
					CrudClause crudClause = CrudClause.findFirstMatchingClause(retVal);
					statementInstance.addClause(crudClause);

				}
				else if(retVal.matches(SyntaxUtilConstants.RegexConstants.PLAIN_TEXT_PATTERN)) {
					statementInstance.addPlainText(retVal);
				}
			}
			statementInstance.constructStatementTemplate();
		}
		catch(InstantiationException | IllegalAccessException e) {
			// TODO Log failure and do not throw exception
		}
		return statementInstance;
	}

}
