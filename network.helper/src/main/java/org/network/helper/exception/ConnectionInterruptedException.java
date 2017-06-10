package org.network.helper.exception;

public class ConnectionInterruptedException extends Exception {

	/**
	 * @author Anish Singh
	 */
	private static final long serialVersionUID = 4534654645L;

	public ConnectionInterruptedException(String message) {
		super(message);
		ExceptionLogger.logException(message);
	}

}
