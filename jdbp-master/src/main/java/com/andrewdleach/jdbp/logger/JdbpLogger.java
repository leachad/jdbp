package com.andrewdleach.jdbp.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbpLogger {

	public static void logInfo(String fileToLog, Exception e) {
		Logger specificLogger = Logger.getLogger("logs." + fileToLog);
		specificLogger.log(Level.INFO, e.getMessage());
	}
}
