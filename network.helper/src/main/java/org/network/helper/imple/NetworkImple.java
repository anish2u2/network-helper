package org.network.helper.imple;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.network.helper.abstracts.AbstractNetwork;
import org.network.helper.contracts.MultipartFileHttpClient;
import org.network.helper.contracts.Network;
import org.network.helper.contracts.NetworkDataReader;
import org.network.helper.contracts.NetworkDataWriter;
import org.network.helper.contracts.Work;
import org.network.helper.work.executore.NetworkThreadExecutore;
import org.network.helper.work.thread.ThreadUtility;

public abstract class NetworkImple extends AbstractNetwork {

	private Object networkResponse;
	private boolean isNetworkWorkDone;

	private String writeToFile;
	private boolean useDecryption;

	private NetworkDataReader networkDataReader;

	private NetworkDataWriter networkDataWriter;

	private static NetworkThreadExecutore executor;

	public NetworkImple() {
		executor = NetworkThreadExecutore.getInstance();
	}

	private void createConnection() throws Exception {
		NetworkDetails details = getNetworkDetails();
		URLConnection urlConnection = new URL(details.getUrl()).openConnection();
		setHttpsURLConnection(urlConnection);
		((HttpURLConnection) getHttpsUrlConnection()).setRequestMethod(details.getMethod());
		getHttpsUrlConnection().setRequestProperty("Content-Type", details.getContentType());
		getHttpsUrlConnection()
				.setDoOutput(!details.getMethod().equals(Network.GET) && !details.getMethod().equals("DELETE"));
		getHttpsUrlConnection().setDoInput(true);
	}

	public void communicate() {
		final Network network = this;
		System.out.println("executing comunicate..");
		if (getNetworkDetails().isMultiPartRequest()) {
			ThreadUtility threadUtility = ThreadUtility.getInstance();
			MultipartFileHttpClient multiPartRequest = new MultiPartRequestHandler();
			multiPartRequest.openConnection();
			threadUtility.add("multiPartConnection", multiPartRequest);
			NetworkDetails networkDetails = getNetworkDetails();
			System.out.println("Its a Multipart request..");
			synchronized (this) {
				synchronized (network) {
					if (Network.POST.equals(networkDetails.getMethod())
							|| Network.PUT.equals(networkDetails.getMethod())) {
						multiPartRequest.writeData(networkDetails.getMultiPartParameterName(),
								networkDetails.getFileName(), networkDetails.getInputStream(),
								networkDetails.isShouldBeEncrypted());
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (networkDetails.getWriteToFileName() != null || networkDetails.getInputStream() != null) {
						System.out.println("now executing write to file..");
						multiPartRequest.readData(networkDetails.getWriteToFileName(), networkDetails.getOutputStream(),
								networkDetails.isShouldBeDecrypted());
					} else {
						threadUtility.add("responseInputStream", multiPartRequest.read());
					}
					this.notify();
					network.notify();
					System.out.println("Notifying network wait threads..");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				((CloseableHttpClient) threadUtility.get("httpClient")).close();
				((PoolingHttpClientConnectionManager) threadUtility.get("connectionPoolManager")).close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}
		try {
			createConnection();
			startCommunicationThread(null, getNetworkDetails().getContentType());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("executing comunicate.. going to executor..");

	}

	private void startCommunicationThread(final Object payloads, final String contentType) {
		final Network network = this;
		executor.executeWork(new Work() {
			public void doWork() {
				try {
					System.out.println("Doing work in another thread..");
					instanseciateNetworkWork();
					synchronized (networkDataWriter) {
						synchronized (network) {
							networkDataWriter.setContentType(contentType);
							networkDataWriter.setHttpsURLConnection(getHttpsUrlConnection());
							networkDataWriter.writeData(payloads);
							executor.executeWork((Work) networkDataWriter);
							networkDataWriter.wait();
							if (ShutDownRequestVerifier.isShutDownRequest()) {
								network.notify();
								return;
							}
							System.out.println("Notified by thread..");
							executor.executeWork((Work) networkDataReader);
							networkResponse = networkDataReader.readResponse();
							((HttpURLConnection) getHttpsUrlConnection()).disconnect();
							isNetworkWorkDone = true;
							network.notify();
							System.out.println("Notiying network holding locks threads");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void communicate(final Object payloads) {

		try {
			createConnection();
			startCommunicationThread(payloads, getNetworkDetails().getContentType());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void instanseciateNetworkWork() {
		this.networkDataReader = new NetworkDataReaderImpl();
		this.networkDataWriter = new NetworkDataWriterImpl();
		this.networkDataReader.setHttpsURLConnection(getHttpsUrlConnection());
		this.networkDataWriter.setHttpsURLConnection(getHttpsUrlConnection());
		this.networkDataReader.readResponseToFile(writeToFile, useDecryption);
		this.networkDataReader.setCipher(getCipher());
	}

	public Object getNetworkResponse() {
		while (!isNetworkWorkDone) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return networkResponse;
	}

	public void readFileResponseFromNetwork(String toFile, boolean useDecryption) {
		this.writeToFile = toFile;
		this.useDecryption = useDecryption;

	}
}
