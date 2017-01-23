/**
 * 
 */
package jdbp.main;

import java.util.List;

import jdbp.db.driver.JdbpDriverLocator;
import jdbp.db.properties.JdbpPropertySetManager;
import jdbp.db.schema.JdbpSchemaManager;
import jdbp.db.schema.SchemaContainer;
import jdbp.exception.JdbpException;

/**
 * Main Class for the Jdbp [ <b>J</b>ava <b>D</b>ata<b>b</b>ase <b>P</b>arser ] project
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class Jdbp {

	private Jdbp() {
		// private constructor to hide default public constructor
	}

	/**
	 * @throws JdbpException
	 */
	public static void initialize() throws JdbpException {
		JdbpDriverLocator.findJdbcDriver();
		JdbpPropertySetManager.loadAllProperties();
		JdbpSchemaManager.createAllSchemasFromProperties();
	}

	public static void destroy() throws JdbpException {
		List<SchemaContainer> schemaContainers = JdbpSchemaManager.getAvailableSchemas();
		for(SchemaContainer schemaContainer: schemaContainers) {
			schemaContainer.closeDataSource();
		}
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	public static SchemaContainer getDatabase(String schemaName) throws JdbpException {
		return JdbpSchemaManager.getSchema(schemaName);
	}
}
