/**
 * 
 */
package com.andrewdleach.jdbp.host;

import java.util.List;

import com.andrewdleach.jdbp.properties.util.SqlUtil;

/**
 * @author andrew.leach
 */
public class HostUtil {
	private static final String DEFAULT_HOST_NAME = "localhost";

	public static String findOneHostName(List<String> hostNames) {
		return (hostNames == null) ? DEFAULT_HOST_NAME : hostNames.isEmpty() ? DEFAULT_HOST_NAME : hostNames.get(0);
	}

	public static int findOnePortNumber(List<Integer> portNumbers, String driverName) {
		return (portNumbers == null) ? getDefaultPortNumber(driverName) : portNumbers.isEmpty() ? getDefaultPortNumber(driverName) : portNumbers.get(0);
	}

	private static int getDefaultPortNumber(String driverName) {
		return SqlUtil.findDefaultPortNumberForDriver(driverName);
	}

}
