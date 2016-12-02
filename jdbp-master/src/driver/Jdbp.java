/**
 * 
 */
package driver;

import java.sql.Connection;

import exception.JdbpDriverException;

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
	 * @throws JdbpDriverException
	 */
	public static void initialize() throws JdbpDriverException {
		JdbpDriverLocator.findJdbcDriver();
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpDriverException
	 */
	public static Connection getConnection(String schemaName) throws JdbpDriverException {
		return JdbpDriverManager.getConnection(schemaName);
	}
}
