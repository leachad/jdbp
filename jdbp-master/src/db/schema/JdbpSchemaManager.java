/**
 * 
 */
package db.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.driver.JdbpDriverManager;
import exception.JdbpException;

/**
 * @author andrew.leach
 */
public class JdbpSchemaManager {
	private static Map<String, SchemaContainer> schemaMap = new HashMap<>();
	private static List<String> schemaNames = new ArrayList<>();

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
		JdbpSchemaManager.schemaNames.addAll(schemaNames);
	}

	public static void createAllSchemasFromProperties() throws JdbpException {
		if(schemaNames != null) {
			for(String schemaName: schemaNames) {
				SchemaContainer dbInstance = createNewSchema(schemaName);
				schemaMap.put(schemaName, dbInstance);
			}
		}
	}

	private static SchemaContainer createNewSchema(String schemaName) throws JdbpException {
		return JdbpDriverManager.buildSchemaContainerFromProperties(schemaName);
	}
}
