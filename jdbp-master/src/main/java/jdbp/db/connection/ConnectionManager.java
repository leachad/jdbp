package jdbp.db.connection;

import java.sql.Connection;

import jdbp.db.schema.SchemaManager;
import jdbp.db.schema.JdbpSchema;
import jdbp.exception.JdbpException;

/**
 * Connection Manager for the available database connections
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class ConnectionManager {

	/**
	 * @param schemaContainer
	 * @return an implementation of javax.sql.Connection
	 * @throws JdbpException
	 */
	public static Connection getConnection(JdbpSchema schemaContainer) throws JdbpException {
		return schemaContainer.getConnection();
	}

	/**
	 * @param schemaName
	 * @return an implementation of javax.sql.Connection
	 * @throws JdbpException
	 */
	public static Connection getConnection(String schemaName) throws JdbpException {
		JdbpSchema schemaContainer = SchemaManager.getSchema(schemaName);
		return getConnection(schemaContainer);
	}
}
