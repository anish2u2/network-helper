package org.network.helper.exception;

import java.io.File;
import java.io.FileOutputStream;

public class ExceptionLogger {

	private static ExceptionLogger logger = null;
	private static final String EXCEPTION_FILE_NAME = "network_exception.txt";

	private ExceptionLogger() {
		logger = new ExceptionLogger();
	}

	public static void logException(String exception) {
		if (logger == null)
			new ExceptionLogger();
		if (logger.checkFileExsits()) {
			logger.appendMessage(exception);
		} else {
			logger.createFile();
			logger.appendMessage(exception);
		}
	}

	private boolean checkFileExsits() {
		File file = new File(EXCEPTION_FILE_NAME);
		return file.exists();
	}

	private void createFile() {
		try {
			File file = new File(EXCEPTION_FILE_NAME);
			file.createNewFile();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void appendMessage(String message) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(EXCEPTION_FILE_NAME));
			fileOutputStream.write(message.getBytes());
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
