package org.network.helper.exception;

public class NetworkDataReaderException extends Exception {

	/**
	 * @author Anish Singh
	 */
	private static final long serialVersionUID = 5464565L;

	public NetworkDataReaderException(String message) {
		super(message);
		ExceptionLogger.logException(message);
	}

}
