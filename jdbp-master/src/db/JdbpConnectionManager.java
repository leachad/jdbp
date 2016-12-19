package db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
	 * @param schemaName
	 * @param userName
	 * @param password
	 * @return
	 * @throws JdbpException
	 */
	protected static Connection getConnection(String schemaName, String userName, String password) throws JdbpException {
		return getAvailableConnection(schemaName, userName, password);
	}

	/**
	 * @param schemaName
	 * @param propertyInfo
	 * @return
	 * @throws JdbpException
	 */
	protected static Connection getConnection(String schemaName, Properties propertyInfo) throws JdbpException {
		return getAvailableConnection(schemaName, propertyInfo);
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	protected static Connection getConnection(String schemaName) throws JdbpException {
		return getAvailableConnection(schemaName);
	}

	/**
	 * @param schemaName
	 * @param userName
	 * @param password
	 * @return
	 * @throws JdbpException
	 */
	private static Connection getAvailableConnection(String schemaName, String userName, String password) throws JdbpException {
		Connection leasedConnection = null;
		synchronized(lockingConnectionManager) {
			Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
			if(connectionPoolMap == null) {
				connectionPoolMap = new HashMap<>();
				IndexedPoolableConnection newConnectionForSchema = getNewConnection(schemaName, userName, password);
				connectionPoolMap.put(newConnectionForSchema.getConnectionId(), newConnectionForSchema);
				jdbpConnectionPool.put(schemaName, connectionPoolMap);
				leasedConnection = newConnectionForSchema.getConnection();

			}
			else {
				try {
					leasedConnection = acquireNextAvailableConnection(schemaName);
					if(leasedConnection == null) {
						IndexedPoolableConnection connection = getNewConnection(schemaName, userName, password);
						connectionPoolMap.put(connection.getConnectionId(), connection);
						leasedConnection = connection.getConnection();
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
	 * @param schemaName
	 * @param propertyInfo
	 * @return
	 * @throws JdbpException
	 */
	private static Connection getAvailableConnection(String schemaName, Properties propertyInfo) throws JdbpException {
		Connection leasedConnection = null;
		synchronized(lockingConnectionManager) {
			Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
			if(connectionPoolMap == null) {
				connectionPoolMap = new HashMap<>();
				IndexedPoolableConnection newConnectionForSchema = getNewConnection(schemaName, propertyInfo);
				connectionPoolMap.put(newConnectionForSchema.getConnectionId(), newConnectionForSchema);
				jdbpConnectionPool.put(schemaName, connectionPoolMap);
				leasedConnection = newConnectionForSchema.getConnection();

			}
			else {
				try {
					leasedConnection = acquireNextAvailableConnection(schemaName);
					if(leasedConnection == null) {
						IndexedPoolableConnection connection = getNewConnection(schemaName, propertyInfo);
						connectionPoolMap.put(connection.getConnectionId(), connection);
						leasedConnection = connection.getConnection();
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
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	private static Connection getAvailableConnection(String schemaName) throws JdbpException {
		Connection leasedConnection = null;
		synchronized(lockingConnectionManager) {
			Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
			if(connectionPoolMap == null) {
				connectionPoolMap = new HashMap<>();
				IndexedPoolableConnection newConnectionForSchema = getNewConnection(schemaName);
				connectionPoolMap.put(newConnectionForSchema.getConnectionId(), newConnectionForSchema);
				jdbpConnectionPool.put(schemaName, connectionPoolMap);
				leasedConnection = newConnectionForSchema.getConnection();

			}
			else {
				try {
					leasedConnection = acquireNextAvailableConnection(schemaName);
					if(leasedConnection == null) {
						IndexedPoolableConnection connection = getNewConnection(schemaName);
						connectionPoolMap.put(connection.getConnectionId(), connection);
						leasedConnection = connection.getConnection();
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

	private static IndexedPoolableConnection getNewConnection(String schemaName, String userName, String password) throws JdbpException {
		Connection connection = null;
		try {
			String url = JdbpDriverManager.getUrlForSchemaName(schemaName);
			connection = DriverManager.getConnection(url, userName, password);
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return new IndexedPoolableConnection(connection, schemaName);
	}

	private static IndexedPoolableConnection getNewConnection(String schemaName, Properties propertyInfo) throws JdbpException {
		Connection connection = null;
		try {
			String url = JdbpDriverManager.getUrlForSchemaName(schemaName);
			connection = DriverManager.getConnection(url, propertyInfo);
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return new IndexedPoolableConnection(connection, schemaName);
	}

	private static IndexedPoolableConnection getNewConnection(String schemaName) throws JdbpException {
		Connection connection = null;
		try {
			String url = JdbpDriverManager.getUrlForSchemaName(schemaName);
			connection = DriverManager.getConnection(url);
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return new IndexedPoolableConnection(connection, schemaName);
	}

	private static Connection acquireNextAvailableConnection(String schemaName) throws SQLException {
		Connection leasedConnection = null;
		Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
		for(Map.Entry<Integer, IndexedPoolableConnection> connectionPoolEntry: connectionPoolMap.entrySet()) {
			IndexedPoolableConnection validConnection = connectionPoolEntry.getValue();
			if(!validConnection.isClosed() && validConnection.isAvailable()) {
				validConnection.acquireLock();
				leasedConnection = validConnection.getConnection();
				break;
			}
		}
		return leasedConnection;
	}

}
