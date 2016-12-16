package db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @since 12.16.16
 * @author andrew.leach
 */
public class IndexedPoolableConnection {
	private Connection connection;
	private String schemaName;
	private int connectionIndex;
	private boolean available;

	/**
	 * @param connection
	 * @param schemaName
	 * @param connectionIndex
	 */
	public IndexedPoolableConnection(Connection connection, String schemaName, int connectionIndex) {
		this.connection = connection;
		this.schemaName = schemaName;
		this.connectionIndex = connectionIndex;
		this.available = false;
	}

	public Connection getConnection() {
		return connection;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public int getConnectionIndex() {
		return connectionIndex;
	}

	/**
	 * Invokes the close method on java.sql.Connection
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		connection.close();
	}

	public boolean isClosed() throws SQLException {
		return connection.isClosed();
	}

	public boolean isAvailable() {
		return available;
	}

	/**
	 * Resets the availability state of this IndexedPoolConnection
	 */
	public void release() {
		this.available = true;
	}

	/**
	 * Sets the state of this IndexedPoolConnection to unavailable TODO decide if this logic should be migrated to 'getConnection()'
	 */
	public void acquireLock() {
		this.available = false;
	}

}
