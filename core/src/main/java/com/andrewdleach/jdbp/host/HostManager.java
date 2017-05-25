/**
 * 
 */
package com.andrewdleach.jdbp.host;

import java.util.ArrayList;
import java.util.List;

import com.andrewdleach.jdbp.driver.DriverStorage;
import com.andrewdleach.jdbp.properties.util.SqlUtil;

/**
 * @author andrew.leach
 */
public class HostManager {
	private static List<String> hostNames = null;
	private static List<Integer> portNumbers = null;
	private static final String DEFAULT_HOST_NAME = "localhost";

	public static String findOneHostName() {
		return (hostNames == null) ? DEFAULT_HOST_NAME : hostNames.isEmpty() ? DEFAULT_HOST_NAME : hostNames.get(0);
	}

	public static List<String> getHostNames() {
		return hostNames;
	}

	public static void setHostNames(List<String> hostNames) {
		HostManager.hostNames = hostNames;
	}

	public static int findOnePortNumber() {
		return (portNumbers == null) ? getDefaultPortNumber() : portNumbers.isEmpty() ? getDefaultPortNumber() : portNumbers.get(0);
	}

	public static List<Integer> getPortNumbers() {
		return portNumbers;
	}

	public static void setPortNumbers(List<String> portNumbersAsStrings) {
		HostManager.portNumbers = new ArrayList<>();
		for(String portNumberString: portNumbersAsStrings) {
			portNumbers.add(Integer.parseInt(portNumberString));
		}
	}

	private static int getDefaultPortNumber() {
		return SqlUtil.findDefaultPortNumberForDriver();
	}

}
