/**
 * 
 */
package com.andrewdleach.jdbp.host;

import java.util.List;

/**
 * @author andrew.leach
 */
public class HostManager {
	private static List<String> hostNames = null;

	public static List<String> getHostNames() {
		return hostNames;
	}

	public static void setHostNames(List<String> hostNames) {
		HostManager.hostNames = hostNames;
	}

}
