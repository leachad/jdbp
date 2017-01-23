package jdbp.db.connection;

import java.sql.Connection;

import jdbp.db.schema.JdbpSchemaManager;
import jdbp.db.schema.SchemaContainer;
import jdbp.exception.JdbpException;

/**
 * Connection Manager for the available database connections
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class JdbpConnectionManager {

	/**
	 * @param schemaContainer
	 * @return an implementation of javax.sql.Connection
	 * @throws JdbpException
	 */
	public static Connection getConnection(SchemaContainer schemaContainer) throws JdbpException {
		return schemaContainer.getConnection();
	}

	/**
	 * @param schemaName
	 * @return an implementation of javax.sql.Connection
	 * @throws JdbpException
	 */
	public static Connection getConnection(String schemaName) throws JdbpException {
		SchemaContainer schemaContainer = JdbpSchemaManager.getSchema(schemaName);
		return getConnection(schemaContainer);
	}
}
