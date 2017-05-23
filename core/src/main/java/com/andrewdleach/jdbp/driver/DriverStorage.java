package com.andrewdleach.jdbp.driver;

public class DriverStorage {

	private static String requestedDriverName;

	public static String getRequestedDriverName() {
		return requestedDriverName;
	}

	public static void setRequestedDriverName(String requestedDriverName) {
		DriverStorage.requestedDriverName = requestedDriverName;
	}

}
