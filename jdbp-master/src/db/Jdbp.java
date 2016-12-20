/**
 * 
 */
package db;

import exception.JdbpException;

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
		JdbpDriverUtil.readDriverUtilProperties();
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	public static SchemaContainer getDatabase(String schemaName) throws JdbpException {
		return JdbpSchemaManager.fetchDB(schemaName);
	}
}
