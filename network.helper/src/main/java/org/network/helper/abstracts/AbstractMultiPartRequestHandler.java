package org.network.helper.abstracts;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.network.helper.abstracts.AbstractNetwork.NetworkDetails;
import org.network.helper.contracts.MultipartFileHttpClient;
import org.network.helper.contracts.Network;
import org.network.helper.work.thread.ThreadUtility;

public abstract class AbstractMultiPartRequestHandler implements MultipartFileHttpClient {

	/*
	 * private static final PoolingHttpClientConnectionManager
	 * connectionManager;
	 * 
	 * static { connectionManager = new PoolingHttpClientConnectionManager();
	 * connectionManager.setMaxTotal(200);
	 * connectionManager.setDefaultMaxPerRoute(20);
	 * 
	 * }
	 */

	public void openConnection() {
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		NetworkDetails details = (NetworkDetails) threadUtility.get("networkDetails");
		try {
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
			CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
			HttpUriRequest request = createRequestObject(details.getUrl(), details.getMethod());
			for (String key : details.getRequestProperty().keySet())
				request.addHeader(key, details.getRequestProperty().get(key));
			threadUtility.add("httpClient", httpClient);
			threadUtility.add("requestObject", request);
			threadUtility.add("connectionPoolManager", connectionManager);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private HttpUriRequest createRequestObject(String url, String method) {
		HttpUriRequest request = null;
		if (Network.POST.equals(method)) {
			request = new HttpPost(url);
		} else if (Network.GET.equals(method)) {
			request = new HttpGet(url);
		} else if (Network.PUT.equals(method)) {
			request = new HttpPut(url);
		} else if (Network.DELETE.equals(method)) {
			request = new HttpDelete(url);
		}
		return request;
	}

}
