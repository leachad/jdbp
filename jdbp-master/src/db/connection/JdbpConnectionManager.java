package db.connection;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import db.schema.JdbpSchemaManager;
import db.schema.SchemaContainer;
import exception.JdbpException;

/**
 * Connection Manager for the available database connections
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class JdbpConnectionManager {
	private static final JdbpConnectionManager connectionManager = new JdbpConnectionManager();
	private static final LockingConnectionManager lockingConnectionManager = connectionManager.new LockingConnectionManager();
	private static Map<String, Map<Integer, IndexedPoolableConnection>> jdbpConnectionPool = new HashMap<>();

	private class LockingConnectionManager implements Serializable {
		private static final long serialVersionUID = -2119449610680614633L;

		private LockingConnectionManager() {}
	}

	/**
	 * @param schemaContainer
	 * @return an implementation of javax.sql.Connection
	 * @throws JdbpException
	 */
	public static IndexedPoolableConnection getConnection(SchemaContainer schemaContainer) throws JdbpException {
		return getAvailableConnection(schemaContainer);
	}

	/**
	 * @param schemaName
	 * @return an implementation of javax.sql.Connection
	 * @throws JdbpException
	 */
	public static IndexedPoolableConnection getConnection(String schemaName) throws JdbpException {
		return getAvailableConnection(JdbpSchemaManager.fetchDB(schemaName));
	}

	private static IndexedPoolableConnection getAvailableConnection(SchemaContainer schemaContainer) throws JdbpException {
		IndexedPoolableConnection leasedConnection = null;
		synchronized(lockingConnectionManager) {
			String schemaName = schemaContainer.getSchemaName();
			Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
			if(connectionPoolMap == null) {
				connectionPoolMap = new HashMap<>();
				IndexedPoolableConnection newConnectionForSchema = getNewConnection(schemaContainer);
				connectionPoolMap.put(newConnectionForSchema.getConnectionId(), newConnectionForSchema);
				jdbpConnectionPool.put(schemaName, connectionPoolMap);
				leasedConnection = newConnectionForSchema;

			}
			else {
				try {
					leasedConnection = acquireNextAvailableConnection(schemaName);
					if(leasedConnection == null) {
						IndexedPoolableConnection connection = getNewConnection(schemaContainer);
						connectionPoolMap.put(connection.getConnectionId(), connection);
						leasedConnection = connection;
					}
				}
				catch(SQLException e) {
					JdbpException.throwException(e);
				}
			}
		}
		return leasedConnection;
	}

	/**
	 * Utility release method if implementing Layered Architecture uses a Connection Pool strategy
	 * 
	 * @param connectionIndex
	 * @param schemaName
	 * @throws JdbpException
	 */
	public static void releaseConnection(Connection connection, String schemaName) throws JdbpException {
		synchronized(lockingConnectionManager) {
			Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
			IndexedPoolableConnection indexedPoolableConnection = null;
			if(connectionPoolMap == null) {
				JdbpException.throwException(JdbpConnectionManagerConstants.NO_CONNECTIONS_FOR_REQUESTED_SCHEMA);
			}
			else {
				indexedPoolableConnection = connectionPoolMap.get(connection.hashCode());
			}

			if(indexedPoolableConnection == null) {
				JdbpException.throwException(JdbpConnectionManagerConstants.NO_INDEXED_CONNECTIONS_FOR_REQUESTED_CONNECTION_ID);
			}
			else {
				indexedPoolableConnection.release();
			}
		}
	}

	/**
	 * Utility method to release all connections for the specified schemaName.
	 * 
	 * @param schemaName
	 * @throws JdbpException
	 */
	public static void releaseConnection(String schemaName) throws JdbpException {
		synchronized(lockingConnectionManager) {
			Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
			if(connectionPoolMap == null) {
				JdbpException.throwException(JdbpConnectionManagerConstants.NO_CONNECTIONS_FOR_REQUESTED_SCHEMA);
			}
			else {
				for(Map.Entry<Integer, IndexedPoolableConnection> connectionEntryForSchema: connectionPoolMap.entrySet()) {
					connectionEntryForSchema.getValue().release();
				}
			}
		}
	}

	private static IndexedPoolableConnection getNewConnection(SchemaContainer schemaContainer) throws JdbpException {
		Connection connection = null;
		try {
			String targetUrl = schemaContainer.getTargetUrl();
			if(schemaContainer.hasCredentialsNoProperties()) {
				connection = DriverManager.getConnection(targetUrl, schemaContainer.getUserName(), schemaContainer.getPassword());
			}
			else if(schemaContainer.hasPropertiesNoCredentials()) {
				connection = DriverManager.getConnection(targetUrl, schemaContainer.getPropertiesInfo());
			}
			else if(schemaContainer.hasNoPropertiesNoCredentials()) {
				connection = DriverManager.getConnection(targetUrl);
			}

		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return new IndexedPoolableConnection(connection, schemaContainer.getSchemaName());
	}

	private static IndexedPoolableConnection acquireNextAvailableConnection(String schemaName) throws SQLException {
		IndexedPoolableConnection leasedConnection = null;
		Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
		for(Map.Entry<Integer, IndexedPoolableConnection> connectionPoolEntry: connectionPoolMap.entrySet()) {
			IndexedPoolableConnection validConnection = connectionPoolEntry.getValue();
			if(!validConnection.isClosed() && validConnection.isAvailable()) {
				validConnection.acquireLock();
				leasedConnection = validConnection;
				break;
			}
		}
		return leasedConnection;
	}

}
