package org.network.helper.exception;

public class NetworkDataWriterException extends Exception {

	/**
	 * @author Anish Singh
	 */
	private static final long serialVersionUID = 2342343L;

	public NetworkDataWriterException(String message) {
		super(message);
		ExceptionLogger.logException(message);
	}

}
