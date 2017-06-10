package org.network.helper.contracts;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import org.network.helper.exception.ConnectionInterruptedException;

public interface Network {

	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	
	public static final String CONTENT_TYPE_APPLICATION_JSON="application/json";
	public static final String CONTENT_TYPE_MULTIPART_FORM_DATA="multipart/form-data";
	public static final String CONTENT_TYPE_TEXT_HTML="text/html";
	public static final String CONTENT_TYPE_TEXT_PLAIN="text/plain";
	public void initializeConnection(String url, String method, String requestType);

	public void communicate();

	public void communicate(Object payloads);

	public boolean isConnectionClosed();

	public boolean isConnectionAlive();

	public void closeConnection() throws ConnectionInterruptedException;

	public Object getNetworkResponse();

	public void setTocken(String tockenKey, String tockenValue);

	public URLConnection getConnection();

	public void setMultiPartRequest(boolean isMultiPartRequest);

	public void setMultiPartRequestBody(String parameterName, String fileName, InputStream inputStream,
			boolean shouldbeEncryptedRequest);

	public void writeResponseToFile(String fileName, OutputStream outputStream, boolean shouldbeDecryptedRequest);

	public InputStream getResponseStream();

	public void setCipher(Object object);

	public void readFileResponseFromNetwork(String toFile, boolean useDecryption);
}
