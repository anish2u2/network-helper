package org.network.helper.imple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.network.helper.abstracts.AbstractNetworkDataReader;
import org.network.helper.contracts.NetworkSupport;
import org.network.helper.contracts.Work;
import org.network.helper.exception.NetworkDataReaderException;
import org.network.helper.work.thread.ThreadUtility;

import com.secure.contracts.HypernymsCipher.Cipher;

public class NetworkDataReaderImpl extends AbstractNetworkDataReader implements Work {

	private Object response;
	private boolean isConnectionInterrupted;
	private Cipher cipher;

	public void setCipher(Cipher cipher) {
		this.cipher = cipher;
	}

	private String fileName;
	private boolean useDecryption;

	public Object readResponse() throws NetworkDataReaderException {
		try {
			while (response == null && !isConnectionInterrupted)
				Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	public boolean isResponseOk() {
		try {
			if (((HttpURLConnection) getHttpsURLConnection()).getResponseCode() == HttpURLConnection.HTTP_OK) {
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(((HttpURLConnection) getHttpsURLConnection()).getErrorStream()));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				line = reader.readLine();
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("error", buffer.toString());
			this.response = map;
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public void doWork() {
		try {
			if (!isResponseOk())
				return;
			if (fileName != null) {
				readFileResponseFromNetwork(fileName, useDecryption);
				fileName = null;
				useDecryption = false;
				isConnectionInterrupted = true;
				return;
			}
			ThreadUtility threadUtility = ThreadUtility.getInstance();
			BufferedReader reader = new BufferedReader(new InputStreamReader(getHttpsURLConnection().getInputStream()));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			Map<String, Object> response = new HashMap<String, Object>(1);
			while ((line = reader.readLine()) != null) {
				if (threadUtility.get(NetworkSupport.CLOSE_NETWORK_CONNECTION) != null
						? ((Boolean) threadUtility.get(NetworkSupport.CLOSE_NETWORK_CONNECTION)) : false)
					((HttpURLConnection) getHttpsURLConnection()).disconnect();
				buffer.append(line);
				if (ShutDownRequestVerifier.isShutDownRequest()) {
					buffer = null;
					reader.close();
					response.put("error", "ShutQown Hook triggered..");
					return;
				}
			}

			if (((HttpURLConnection) getHttpsURLConnection()).getResponseCode() == 200)
				response.put("success", buffer.toString());
			else
				response.put("error", buffer.toString());
			System.out.println("response:" + buffer);
			buffer = null;
			reader.close();
			this.response = response;
		} catch (Exception ex) {
			isConnectionInterrupted = true;
			ex.printStackTrace();
		}
	}

	public void readResponseToFile(String fileName, boolean useDecryption) {
		this.fileName = fileName;
		this.useDecryption = useDecryption;
	}

	public void readFileResponseFromNetwork(String toFile, boolean useDecryption) {
		String fileNameFromResponseHeader = ((HttpURLConnection) getHttpsURLConnection()).getHeaderField("file-name");
		long fileLength = Long
				.valueOf((String) ((((HttpURLConnection) getHttpsURLConnection()).getHeaderField("file-length") != null)
						? (((HttpURLConnection) getHttpsURLConnection()).getHeaderField("file-length")) : "0"));
		toFile = fileNameFromResponseHeader != null ? fileNameFromResponseHeader : toFile;
		try (OutputStream outputStream = useDecryption
				? this.cipher.getDecryptCipherOutputStream(new FileOutputStream(new File(toFile)))
				: new FileOutputStream(new File(toFile));
				InputStream reader = getHttpsURLConnection().getInputStream();) {
			//System.out.println("Starting reading..");
			int contentLength;
			byte[] buffer = new byte[4096];
			while ((contentLength = reader.read(buffer)) != -1) {
				//System.out.println(new String(buffer,"UTF-8"));
				outputStream.write(buffer, 0, contentLength);
				if ((fileLength / 1000000) > 1)
					Thread.sleep(800);
				if (ShutDownRequestVerifier.isShutDownRequest()) {
					//System.out.println("Clossing stream as Thread release called..");
					reader.close();
					outputStream.close();
					response = "Aborted By ShutDown Hook";
					return;
				}
			}
			response = "success";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
