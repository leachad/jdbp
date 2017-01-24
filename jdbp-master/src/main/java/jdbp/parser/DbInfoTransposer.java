/**
 * 
 */
package jdbp.parser;

import java.util.List;

import jdbp.db.model.DBInfo;
import jdbp.db.properties.util.SyntaxUtil;
import jdbp.db.schema.SchemaManager;
import jdbp.db.statement.InsertStatement;
import jdbp.db.statement.StatementManager.CrudOperation;

/**
 * @author andrew.leach
 */
public class DbInfoTransposer {

	public static String convertDbInfosToSQLString(String schemaName, String destinationTable, CrudOperation crudOperation, List<Class<? extends DBInfo>> infosToConvert) {
		String sqlString = null;
		switch(crudOperation) {
			case CREATE:
				sqlString = null; // not yet supported
				break;
			case DELETE:
				sqlString = buildDeleteSQLString(schemaName, destinationTable, infosToConvert);
				break;
			case SELECT:
				sqlString = null; // not yet supported
				break;
			case UPDATE:
				sqlString = buildUpdateSQLString(schemaName, destinationTable, infosToConvert);
				break;
			case INSERT:
				sqlString = buildInsertSQLString(schemaName, destinationTable, infosToConvert);
				break;

		}
		return sqlString;
	}

	private static String buildInsertSQLString(String schemaName, String destinationTable, List<Class<? extends DBInfo>> infosToConvert) {
		String requestedDriverName = SchemaManager.getRequestedDriverName();
		InsertStatement insertStatement = SyntaxUtil.getSyntacticInsertStatement(requestedDriverName);
		insertStatement.appendDBInfos(infosToConvert);
		return null;
	}

	private static String buildUpdateSQLString(String schemaName, String destinationTable, List<Class<? extends DBInfo>> infosToConvert) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String buildDeleteSQLString(String schemaName, String destinationTable, List<Class<? extends DBInfo>> infosToConvert) {
		// TODO Auto-generated method stub
		return null;
	}
}
