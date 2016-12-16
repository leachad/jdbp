package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import driver.JdbpDriverManager;
import exception.JdbpException;

/**
 * Connection Manager for the available database connections
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public abstract class JdbpConnectionManager {

	private static Map<String, Map<Integer, IndexedPoolableConnection>> jdbpConnectionPool = new HashMap<>();

	/**
	 * @param schemaName
	 * @param userName
	 * @param password
	 * @return
	 * @throws JdbpException
	 */
	public static synchronized Connection getConnection(String schemaName, String userName, String password) throws JdbpException {
		return getAvailableConnection(schemaName, userName, password);
	}

	/**
	 * @param schemaName
	 * @param propertyInfo
	 * @return
	 * @throws JdbpException
	 */
	public static synchronized Connection getConnection(String schemaName, Properties propertyInfo) throws JdbpException {
		return getAvailableConnection(schemaName, propertyInfo);
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	public static synchronized Connection getConnection(String schemaName) throws JdbpException {
		return getAvailableConnection(schemaName);
	}

	/**
	 * @param schemaName
	 * @param userName
	 * @param password
	 * @return
	 * @throws JdbpException
	 */
	private static synchronized Connection getAvailableConnection(String schemaName, String userName, String password) throws JdbpException {
		Connection leasedConnection = null;
		Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
		if(connectionPoolMap == null) {
			connectionPoolMap = new HashMap<>();
			IndexedPoolableConnection newConnectionForSchema = getNewConnection(schemaName, userName, password, 0);
			connectionPoolMap.put(0, newConnectionForSchema);
			jdbpConnectionPool.put(schemaName, connectionPoolMap);
			leasedConnection = newConnectionForSchema.getConnection();

		}
		else {
			try {
				leasedConnection = acquireNextAvailableConnection(schemaName);
				if(leasedConnection == null) {
					int nextIndexForPooledConnection = connectionPoolMap.size();
					IndexedPoolableConnection connection = getNewConnection(schemaName, userName, password, nextIndexForPooledConnection);
					connectionPoolMap.put(nextIndexForPooledConnection, connection);
					leasedConnection = connection.getConnection();
				}
			}
			catch(SQLException e) {
				JdbpException.throwException(e);
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
	private static synchronized Connection getAvailableConnection(String schemaName, Properties propertyInfo) throws JdbpException {
		Connection leasedConnection = null;
		Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
		if(connectionPoolMap == null) {
			connectionPoolMap = new HashMap<>();
			IndexedPoolableConnection newConnectionForSchema = getNewConnection(schemaName, propertyInfo, 0);
			connectionPoolMap.put(0, newConnectionForSchema);
			jdbpConnectionPool.put(schemaName, connectionPoolMap);
			leasedConnection = newConnectionForSchema.getConnection();

		}
		else {
			try {
				leasedConnection = acquireNextAvailableConnection(schemaName);
				if(leasedConnection == null) {
					int nextIndexForPooledConnection = connectionPoolMap.size();
					IndexedPoolableConnection connection = getNewConnection(schemaName, propertyInfo, nextIndexForPooledConnection);
					connectionPoolMap.put(nextIndexForPooledConnection, connection);
					leasedConnection = connection.getConnection();
				}
			}
			catch(SQLException e) {
				JdbpException.throwException(e);
			}
		}
		return leasedConnection;
	}

	/**
	 * @param schemaName
	 * @return
	 * @throws JdbpException
	 */
	private static synchronized Connection getAvailableConnection(String schemaName) throws JdbpException {
		Connection leasedConnection = null;
		Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
		if(connectionPoolMap == null) {
			connectionPoolMap = new HashMap<>();
			IndexedPoolableConnection newConnectionForSchema = getNewConnection(schemaName, 0);
			connectionPoolMap.put(0, newConnectionForSchema);
			jdbpConnectionPool.put(schemaName, connectionPoolMap);
			leasedConnection = newConnectionForSchema.getConnection();

		}
		else {
			try {
				leasedConnection = acquireNextAvailableConnection(schemaName);
				if(leasedConnection == null) {
					int nextIndexForPooledConnection = connectionPoolMap.size();
					IndexedPoolableConnection connection = getNewConnection(schemaName, nextIndexForPooledConnection);
					connectionPoolMap.put(nextIndexForPooledConnection, connection);
					leasedConnection = connection.getConnection();
				}
			}
			catch(SQLException e) {
				JdbpException.throwException(e);
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
	public static synchronized void releaseConnection(int connectionIndex, String schemaName) throws JdbpException {
		Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
		IndexedPoolableConnection connection = null;
		if(connectionPoolMap == null) {
			JdbpException.throwException(JdbpConnectionManagerConstants.NO_CONNECTIONS_FOR_REQUESTED_SCHEMA);
		}
		else {
			connection = connectionPoolMap.get(connectionIndex);
		}

		if(connection == null) {
			JdbpException.throwException(JdbpConnectionManagerConstants.NO_CONNECTIONS_FOR_REQUESTED_INDEX);
		}
		else {
			JdbpConnectionManager.releaseConnection(connection, schemaName, connectionIndex);
		}
	}

	/**
	 * This method will clean up the backing data structure references using the IndexedPoolableConnection object.
	 * 
	 * @param connection
	 * @param schemaName
	 * @param connectionIndex
	 * @throws JdbpException
	 */
	public static synchronized void releaseConnection(IndexedPoolableConnection connection, String schemaName, int connectionIndex) throws JdbpException {
		try {
			connection.close();
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}

		Map<Integer, IndexedPoolableConnection> connectionPoolMap = jdbpConnectionPool.get(schemaName);
		if(connectionPoolMap != null && connectionPoolMap.get(connectionIndex) != null) {
			boolean removeSuccessful = connectionPoolMap.remove(connectionIndex, connection);
			if(!removeSuccessful) {
				JdbpException.throwException(JdbpConnectionManagerConstants.FAILED_CONNECTION_RELEASE_REQUEST);
			}
		}
	}

	private static IndexedPoolableConnection getNewConnection(String schemaName, String userName, String password, int newConnectionIndex) throws JdbpException {
		Connection connection = null;
		try {
			String url = JdbpDriverManager.getUrlForSchemaName(schemaName);
			connection = DriverManager.getConnection(url, userName, password);
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return new IndexedPoolableConnection(connection, schemaName, newConnectionIndex);
	}

	private static IndexedPoolableConnection getNewConnection(String schemaName, Properties propertyInfo, int newConnectionIndex) throws JdbpException {
		Connection connection = null;
		try {
			String url = JdbpDriverManager.getUrlForSchemaName(schemaName);
			connection = DriverManager.getConnection(url, propertyInfo);
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return new IndexedPoolableConnection(connection, schemaName, newConnectionIndex);
	}

	private static IndexedPoolableConnection getNewConnection(String schemaName, int newConnectionIndex) throws JdbpException {
		Connection connection = null;
		try {
			String url = JdbpDriverManager.getUrlForSchemaName(schemaName);
			connection = DriverManager.getConnection(url);
		}
		catch(SQLException e) {
			JdbpException.throwException(e);
		}
		return new IndexedPoolableConnection(connection, schemaName, newConnectionIndex);
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
