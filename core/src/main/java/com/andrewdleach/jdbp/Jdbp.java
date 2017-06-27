/**
 * 
 */
package com.andrewdleach.jdbp;

import com.andrewdleach.jdbp.driver.DriverLocator;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.logger.JdbpLogger;
import com.andrewdleach.jdbp.properties.PropertySetManager;
import com.andrewdleach.jdbp.schema.AbstractSchema;
import com.andrewdleach.jdbp.schema.SchemaManager;

/**
 * Main Class for the Jdbp [ <b>J</b>ava <b>D</b>ata<b>b</b>ase <b>P</b>arser ] project
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class Jdbp {
	private static Jdbp jdbp = null;

	private Jdbp() throws JdbpException {
		DriverLocator.findJdbcDrivers();
		PropertySetManager.loadAllProperties();
		SchemaManager.createAllSchemasFromProperties();
	}

	public static Jdbp getInstance() {
		if(jdbp == null) {
			try {
				jdbp = new Jdbp();
			}
			catch(JdbpException e) {
				JdbpLogger.logInfo("Unable to instantiate Jdbp", e);
			}
		}
		return jdbp;
	}

	/**
	 * Invokes the close method on all HikariDataSource objects
	 * 
	 * @throws JdbpException
	 */
	public void destroy() throws JdbpException {
		SchemaManager.closeAllDataSources();
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	public AbstractSchema getDatabase(String schemaName) throws JdbpException {
		return SchemaManager.getSchema(schemaName);
	}
}
