package org.network.helper.handler;

import java.net.URLConnection;

import org.network.helper.contracts.Network;
import org.network.helper.imple.NetworkImple;

public class NetworkHandler extends NetworkImple {

	private static Network network;

	private NetworkHandler() {

	}

	public URLConnection getConnection() {
		return getHttpsUrlConnection();
	}

	public static Network getInstance() {
		if (network == null)
			network = new NetworkHandler();
		return network;
	}

	public void setMultiPartRequest(boolean isMultiPartRequest) {
		getNetworkDetails().setMultiPartRequest(isMultiPartRequest);
	}

}
