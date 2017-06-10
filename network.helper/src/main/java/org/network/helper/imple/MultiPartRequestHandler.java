package org.network.helper.imple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.network.helper.abstracts.AbstractMultiPartRequestHandler;
import org.network.helper.contracts.Work;
import org.network.helper.work.executore.NetworkThreadExecutore;
import org.network.helper.work.thread.ThreadUtility;

import com.secure.contracts.HypernymsCipher.Cipher;
import com.secure.imple.HypernymsFileBody;

public class MultiPartRequestHandler extends AbstractMultiPartRequestHandler {

	public void writeData(final String parameterName, final String fileName, final InputStream inputStream,
			final boolean shouldBeEncryptedUsingCipher) {

		NetworkThreadExecutore executor = NetworkThreadExecutore.getInstance();
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		final HttpUriRequest request = (HttpUriRequest) threadUtility.get("requestObject");
		final Map<String, Object> threadLocalMap = threadUtility.getMap();
		executor.executeWork(new Work() {
			public void doWork() {
				FileBody fileBody = null;

				fileBody = (shouldBeEncryptedUsingCipher) ? new HypernymsFileBody(new File(fileName))
						: new FileBody(new File(fileName));
				threadLocalMap.put("hypernymsFileBody", fileBody);
				if (shouldBeEncryptedUsingCipher) {
					((HypernymsFileBody) fileBody).setInputStream(inputStream);
					((HypernymsFileBody) fileBody).setCipher((Cipher) threadLocalMap.get("cipher"));
					System.out.println("setting cipher:" + threadLocalMap.get("cipher"));
				}
				HttpEntity entity = MultipartEntityBuilder.create().addPart(parameterName, fileBody).build();
				HttpPost postRequest = (HttpPost) request;
				if (ShutDownRequestVerifier.isShutDownRequest()) {
					postRequest.abort();
					return;
				}
				postRequest.setEntity(entity);
			}
		});

	}

	public void readData(final String fileName, final OutputStream outputStream,
			final boolean shouldBeDecryptedUsingCipher) {
		NetworkThreadExecutore executor = NetworkThreadExecutore.getInstance();
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		final CloseableHttpClient client = (CloseableHttpClient) threadUtility.get("httpClient");
		final HttpUriRequest request = (HttpUriRequest) threadUtility.get("requestObject");
		final HypernymsFileBody fileBody = (HypernymsFileBody) threadUtility.get("hypernymsFileBody");
		executor.executeWork(new Work() {
			public void doWork() {
				try {
					CloseableHttpResponse response = client.execute(request);
					HttpEntity entity = response.getEntity();
					if (shouldBeDecryptedUsingCipher) {
						fileBody.decryptFileBodyAndWriteFile(fileName, outputStream, entity.getContent());
					} else {
						OutputStream stream = null;
						if (outputStream == null)
							stream = new FileOutputStream(new File(fileName));
						else
							stream = outputStream;
						InputStream inStream = entity.getContent();
						byte[] buffer = new byte[4096];
						int length;
						while ((length = inStream.read(buffer)) != -1) {
							stream.write(buffer, 0, length);
							if (ShutDownRequestVerifier.isShutDownRequest()) {
								inStream.close();
								stream.close();
							}
						}
						stream.flush();
						stream.close();
						inStream.close();
					}
					EntityUtils.consume(entity);
					response.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	public byte[] read() {
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		final CloseableHttpClient client = (CloseableHttpClient) threadUtility.get("httpClient");
		final HttpUriRequest request = (HttpUriRequest) threadUtility.get("requestObject");
		try {
			CloseableHttpResponse response = client.execute(request);
			byte[] buffer = new byte[4096];
			response.getEntity().getContent().read(buffer);
			EntityUtils.consume(response.getEntity());
			response.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
