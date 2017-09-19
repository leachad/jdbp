package com.andrewdleach.jdbp.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbpLogger {

	private static final String LOG_MODULE_PARENT = "com.andrewdleach.jdbp-core.";

	private static Logger getLogger(String fileToLog) {
		return Logger.getLogger(LOG_MODULE_PARENT + fileToLog);
	}

	public static void logInfo(String fileToLog, Exception e) {
		Logger specificLogger = getLogger(fileToLog);
		specificLogger.log(Level.INFO, e.getMessage());
	}

	public static void logInfo(String fileToLog, String message) {
		Logger specificLogger = getLogger(fileToLog);
		specificLogger.log(Level.INFO, message);
	}

	public static void logInfo(String fileToLog, String message, String... additionalParams) {
		Logger specificLogger = getLogger(fileToLog);
		specificLogger.log(Level.INFO, message, additionalParams);
	}

	public static void logError(String fileToLog, String message, Exception e) {
		Logger specificLogger = getLogger(fileToLog);
		specificLogger.log(Level.SEVERE, message, e);
	}
}
