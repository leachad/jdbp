/**
 * 
 */
package driver;

import java.sql.Connection;

import db.IndexedPoolableConnection;
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
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	public static Connection getConnection(String schemaName) throws JdbpException {
		return JdbpDriverManager.getConnection(schemaName);
	}

	public static IndexedPoolableConnection getConnection(String schemaName, int index) throws JdbpException {
		Connection connection = JdbpDriverManager.getConnection(schemaName);
		return new IndexedPoolableConnection(connection, schemaName, index);
	}
}
