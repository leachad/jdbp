/**
 * 
 */
package db.host;

import java.util.List;

/**
 * @author andrew.leach
 */
public class JdbpHostManager {
	private static List<String> hostNames = null;

	public static List<String> getHostNames() {
		return hostNames;
	}

	public static void setHostNames(List<String> hostNames) {
		JdbpHostManager.hostNames = hostNames;
	}

}
