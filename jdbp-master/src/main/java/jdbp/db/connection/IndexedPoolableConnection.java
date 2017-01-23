package jdbp.db.connection;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wrapper object for jdbp.db.driver's implementation of javax.sql.Connection.
 * 
 * @since 12.16.16
 * @author andrew.leach
 */
public class IndexedPoolableConnection implements Serializable {
	private static final long serialVersionUID = 610294057357110457L;
	private transient Connection connection;
	private String schemaName;
	private int connectionId;
	private boolean available;
	private int timesRecycled = -1;

	/**
	 * @param connection
	 * @param schemaName
	 * @param connectionId
	 */
	public IndexedPoolableConnection(Connection connection, String schemaName) {
		this.connection = connection;
		this.schemaName = schemaName;
		this.connectionId = connection.hashCode();
		acquireLock();
	}

	public Connection getConnection() {
		return connection;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public int getConnectionId() {
		return connectionId;
	}

	/**
	 * Wrapper method for the java.sql.Connection interface contained in IndexedPoolableConnection
	 * 
	 * @throws SQLException
	 */
	public boolean isClosed() throws SQLException {
		return connection.isClosed();
	}

	public boolean isAvailable() {
		return available;
	}

	public int getTimesRecycled() {
		return timesRecycled;
	}

	/**
	 * Resets the availability state of this IndexedPoolConnection
	 */
	public void release() {
		this.available = true;
	}

	/**
	 * Wrapper method for the java.sql.Connection interface contained in IndexedPoolableConnection
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		if(!connection.getAutoCommit()) {
			connection.commit();
		}
	}

	/**
	 * Wrapper method for the java.sql.Connection interface contained in IndexedPoolableConnection
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		if(!connection.getAutoCommit()) {
			connection.rollback();
		}
	}

	/**
	 * Sets the state of this IndexedPoolConnection to unavailable TODO decide if this logic should be migrated to 'getConnection()'
	 */
	public void acquireLock() {
		this.available = false;
		timesRecycled++;
	}

}
