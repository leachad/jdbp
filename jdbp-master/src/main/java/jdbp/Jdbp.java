/**
 * 
 */
package jdbp;

import jdbp.db.schema.JdbpSchema;
import jdbp.exception.JdbpException;

/**
 * Main Class for the Jdbp [ <b>J</b>ava <b>D</b>ata<b>b</b>ase <b>P</b>arser ] project
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class Jdbp {

	public Jdbp() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Initializes HikariDataSource object for the properties defined in Jdbp properties
	 * 
	 * @throws JdbpException
	 */
	public static void initialize() throws JdbpException {
		DriverLocator.findJdbcDriver();
		PropertySetManager.loadAllProperties();
		SchemaManager.createAllSchemasFromProperties();
	}

	/**
	 * Invokes the close method on all HikariDataSource objects
	 * 
	 * @throws JdbpException
	 */
	public static void destroy() throws JdbpException {
		SchemaManager.closeAllDataSources();
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	public static JdbpSchema getDatabase(String schemaName) throws JdbpException {
		return SchemaManager.getSchema(schemaName);
	}
}
