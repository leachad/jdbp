package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import driver.Jdbp;
import exception.JdbpDriverException;

public abstract class JdbManager {

	private static Map<String, Map<Integer, Connection>> jdbConnectionPool = new HashMap<>();

	public static Connection getAvailableConnection(String schemaName) {
		Connection leasedConnection = null;
		Map<Integer, Connection> connectionPoolMap = jdbConnectionPool.get(schemaName);
		if(connectionPoolMap == null) {
			connectionPoolMap = new HashMap<>();
			try {
				Connection newConnectionForSchema = Jdbp.getConnection(schemaName);
				connectionPoolMap.put(0, newConnectionForSchema);

			}
			catch(JdbpDriverException e) {
				// TODO Log or throw this error appropriately
				e.printStackTrace();
			}
		}
		else {
			for(Entry<Integer, Connection> connectionEntry: connectionPoolMap.entrySet()) {
				int currentIndex = connectionEntry.getKey();
				Connection currentConnection = connectionEntry.getValue();
				try {
					if(currentConnection.isClosed()) {
						leasedConnection = currentConnection;
					}
				}
				catch(SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return leasedConnection;
	}

}
