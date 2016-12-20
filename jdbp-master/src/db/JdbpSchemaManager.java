/**
 * 
 */
package db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.JdbpException;

/**
 * @author andrew.leach
 */
public class JdbpSchemaManager {
	private static Map<String, SchemaContainer> schemaMap = new HashMap<>();
	private static List<String> schemaNames = null;

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	public static SchemaContainer fetchDB(String schemaName) throws JdbpException {
		SchemaContainer dbInstance = schemaMap.get(schemaName);
		if(dbInstance == null) {
			dbInstance = createNewSchema(schemaName);
			schemaMap.put(schemaName, dbInstance);
		}
		return dbInstance;
	}

	public static void setSchemaNames(List<String> schemaNames) {
		JdbpSchemaManager.schemaNames = schemaNames;
	}

	private static SchemaContainer createNewSchema(String schemaName) throws JdbpException {
		return JdbpDriverManager.buildSchemaContainerFromProperties(schemaName);
	}
}
