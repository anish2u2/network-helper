package org.network.helper.imple;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import org.network.helper.abstracts.AbstractNetworkDataWriter;
import org.network.helper.contracts.Network;
import org.network.helper.contracts.NetworkSupport;
import org.network.helper.contracts.Work;
import org.network.helper.exception.NetworkDataWriterException;
import org.network.helper.work.thread.ThreadUtility;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkDataWriterImpl extends AbstractNetworkDataWriter implements Work {

	private Object payloads;

	private String contentType;

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void writeData(Object payloads) throws NetworkDataWriterException {
		this.payloads = payloads;
	}

	public void doWork() {
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		try {
			System.out.println("Writing data to network..");
			if (threadUtility.get(NetworkSupport.CLOSE_NETWORK_CONNECTION) != null
					? ((Boolean) threadUtility.get(NetworkSupport.CLOSE_NETWORK_CONNECTION)) : false)
				((HttpURLConnection) getHttpsURLConnection()).disconnect();
			ObjectMapper mapper = new ObjectMapper();
			synchronized (this) {
				if (payloads != null) {
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(getHttpsURLConnection().getOutputStream()));

					boolean isJsonRequest = false;

					if (Network.CONTENT_TYPE_APPLICATION_JSON.equals(contentType)) {
						System.out.println("its a json request..");
						isJsonRequest = true;
					}
					System.out.println("its a  request..");
					if (isJsonRequest) {
						payloads = mapper.writeValueAsString(payloads == null ? "" : payloads);
						System.out.println("print :" + payloads);
					}
					System.out.println("Its a json request:" + payloads);
					if (ShutDownRequestVerifier.isShutDownRequest()) {
						writer.close();
						return;
					}
					writer.write(payloads.toString());
					writer.flush();
					writer.close();
				}
				this.notify();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
