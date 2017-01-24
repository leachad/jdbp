package jdbp.db.properties.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdbp.db.properties.info.SyntaxPropertiesInfo;
import jdbp.db.statement.CrudDynamicValue;
import jdbp.db.statement.InsertStatement;
import jdbp.db.statement.StatementManager.CrudDelimiter;
import jdbp.db.statement.StatementManager.CrudDynamicValueKey;
import jdbp.db.statement.StatementManager.CrudKeyword;
import jdbp.db.statement.StatementManager.CrudOperation;
import jdbp.db.statement.SyntacticStatement;

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

		Map<CrudOperation, SyntaxPropertiesInfo> syntacticStatementsForDriver = syntaxUtilProperties.get(driverAndCrudOperation[0]);
		if(syntacticStatementsForDriver == null) {
			syntacticStatementsForDriver = new HashMap<>();
			syntaxUtilProperties.put(driverAndCrudOperation[0], syntacticStatementsForDriver);
		}

		SyntaxPropertiesInfo syntaxPropertiesInfo = buildSyntaxPropertiesInfo(driverAndCrudOperation[1], value);
		syntacticStatementsForDriver.put(syntaxPropertiesInfo.getCrudOperation(), syntaxPropertiesInfo);

	}

	private SyntaxPropertiesInfo buildSyntaxPropertiesInfo(String operation, String value) {
		SyntaxPropertiesInfo syntaxPropertiesInfo = null;
		Matcher matcher = Pattern.compile("operation#[A-Za-z]").matcher(value);
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
			syntaxPropertiesInfo.getSyntacticStatement();
		}
		return (InsertStatement)insertStatement;
	}

	private SyntacticStatement buildSyntacticStatement(CrudOperation crudOperation, String value) {
		SyntacticStatement statementInstance = null;
		try {
			statementInstance = crudOperation.getStatementClass().newInstance();

			Matcher matcher = Pattern.compile("keyword#[A-Za-z]+|delimiter#[/[/(,.;)/]]+|<[A-Za-z.]>+").matcher(value);
			while(matcher.find()) {
				String retVal = matcher.group();
				if(retVal.contains("keyword#[A-Za-z]+")) {
					CrudKeyword crudKeyword = CrudKeyword.findMatchingKeyword(retVal);
					statementInstance.addKeyword(crudKeyword);

				}
				else if(retVal.contains("delimiter#[/[/(,.;)/]]+")) {
					CrudDelimiter crudDelimiter = CrudDelimiter.findMatchingDelimiter(retVal);
					statementInstance.addDelimiter(crudDelimiter);
				}
				else if(retVal.contains("<[A-Za-z.]>+")) {
					CrudDynamicValue crudDynamicValue = new CrudDynamicValue();
					CrudDynamicValueKey crudDynamicValueKey = CrudDynamicValueKey.findFirstMatchingDynamicValueKey(retVal);
					crudDynamicValue.setCrudDynamicValueKey(crudDynamicValueKey);
					if(retVal.contains(CrudDynamicValueKey.ALLOWS_MULTIPLES.getCrudDynamicValueKey())) {
						crudDynamicValue.setAllowsMultiples(true);
					}

					statementInstance.addDynamicValue(crudDynamicValue);
				}
				else {
					statementInstance.addPlainText(retVal);
				}

			}
			statementInstance.setStatementSyntax(value);
		}
		catch(InstantiationException | IllegalAccessException e) {
			// TODO Log failure and do not throw exception
		}
		return statementInstance;
	}

}
