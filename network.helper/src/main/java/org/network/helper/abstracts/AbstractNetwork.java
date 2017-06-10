package org.network.helper.abstracts;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.network.helper.contracts.Network;
import org.network.helper.contracts.NetworkSupport;
import org.network.helper.exception.ConnectionInterruptedException;
import org.network.helper.work.thread.ThreadUtility;

import com.secure.contracts.HypernymsCipher.Cipher;

public abstract class AbstractNetwork implements Network {

	private URLConnection connection;
	private Cipher cipher;

	public NetworkDetails getNetworkDetails() {
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		return (NetworkDetails) threadUtility.get("networkDetails");
	}

	protected URLConnection getHttpsUrlConnection() {
		return connection;
	}

	protected void setHttpsURLConnection(URLConnection connection) {
		this.connection = connection;
	}

	public void initializeConnection(String url, String method, String requestType) {
		try {
			NetworkDetails networkDetails = new NetworkDetails();
			networkDetails.setContentType(requestType);
			networkDetails.setMethod(method);
			networkDetails.setUrl(url);
			ThreadUtility threadUtility = ThreadUtility.getInstance();
			threadUtility.add("networkDetails", networkDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setTocken(String tockenKey, String tockenValue) {
		getNetworkDetails().getRequestProperty().put(tockenKey, tockenValue);
	}

	public boolean isConnectionClosed() {
		return connection == null;
	}

	public boolean isConnectionAlive() {
		// TODO Auto-generated method stub
		return !(connection == null);
	}

	public void closeConnection() throws ConnectionInterruptedException {
		try {
			if (getHttpsUrlConnection() != null)
				((HttpURLConnection) getHttpsUrlConnection()).disconnect();

			ThreadUtility threadUtility = ThreadUtility.getInstance();
			threadUtility.add(NetworkSupport.CLOSE_NETWORK_CONNECTION, true);
			if (threadUtility.get("httpClient") != null)
				((CloseableHttpClient) threadUtility.get("httpClient")).close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static class NetworkDetails {
		private String url;
		private String contentType;
		private String method;
		private boolean isMultiPartRequest;
		private InputStream inputStream;
		private String multiPartParameterName;
		private OutputStream outputStream;

		private String fileName;

		private String writeToFileName;

		private boolean shouldBeEncrypted;

		private boolean shouldBeDecrypted;

		public String getMultiPartParameterName() {
			return multiPartParameterName;
		}

		public void setMultiPartParameterName(String multiPartParameterName) {
			this.multiPartParameterName = multiPartParameterName;
		}

		public boolean isShouldBeDecrypted() {
			return shouldBeDecrypted;
		}

		public void setShouldBeDecrypted(boolean shouldBeDecrypted) {
			this.shouldBeDecrypted = shouldBeDecrypted;
		}

		public String getWriteToFileName() {
			return writeToFileName;
		}

		public void setWriteToFileName(String writeToFileName) {
			this.writeToFileName = writeToFileName;
		}

		public InputStream getInputStream() {
			return inputStream;
		}

		public void setInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		public OutputStream getOutputStream() {
			return outputStream;
		}

		public void setOutputStream(OutputStream outputStream) {
			this.outputStream = outputStream;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public boolean isShouldBeEncrypted() {
			return shouldBeEncrypted;
		}

		public void setShouldBeEncrypted(boolean shouldBeEncrypted) {
			this.shouldBeEncrypted = shouldBeEncrypted;
		}

		public boolean isMultiPartRequest() {
			return isMultiPartRequest;
		}

		public void setMultiPartRequest(boolean isMultiPartRequest) {
			this.isMultiPartRequest = isMultiPartRequest;
		}

		private Map<String, String> requestProperty;

		public NetworkDetails() {
			requestProperty = new HashMap<String, String>();
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public Map<String, String> getRequestProperty() {
			if (requestProperty == null)
				new NetworkDetails();
			return requestProperty;
		}

		public void setRequestProperty(Map<String, String> requestProperty) {
			this.requestProperty = requestProperty;
		}

	}

	public void setMultiPartRequestBody(String parameterName, String fileName, InputStream inputStream,
			boolean shouldbeEncryptedRequest) {
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		NetworkDetails details = (NetworkDetails) threadUtility.get("networkDetails");
		details.setFileName(fileName);
		details.setInputStream(inputStream);
		details.setShouldBeEncrypted(shouldbeEncryptedRequest);
		details.setMultiPartParameterName(parameterName);
	}

	public void writeResponseToFile(String fileName, OutputStream outputStream, boolean shouldbeDecryptedRequest) {
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		NetworkDetails details = (NetworkDetails) threadUtility.get("networkDetails");
		details.setWriteToFileName(fileName);
		details.setOutputStream(outputStream);
		details.setShouldBeDecrypted(shouldbeDecryptedRequest);
		System.out.println("data success fully set to network details..");
	}

	public InputStream getResponseStream() {
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (InputStream) ThreadUtility.getInstance().get("responseInputStream");
	}

	public void setCipher(Object object) {
		this.cipher = (Cipher) object;
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		threadUtility.add("cipher", object);
	}

	protected Cipher getCipher() {
		return this.cipher;
	}

	

}
