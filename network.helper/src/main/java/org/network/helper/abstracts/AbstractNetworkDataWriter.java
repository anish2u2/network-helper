package org.network.helper.abstracts;

import java.net.URLConnection;

import org.network.helper.contracts.NetworkDataWriter;

public abstract class AbstractNetworkDataWriter implements NetworkDataWriter {

	private URLConnection connection;

	protected URLConnection getHttpsURLConnection() {
		return connection;
	}

	public void setHttpsURLConnection(URLConnection connection) {
		this.connection = connection;
	}

}
