package org.network.helper.abstracts;

import java.net.URLConnection;

import org.network.helper.contracts.NetworkDataReader;

public abstract class AbstractNetworkDataReader implements NetworkDataReader {

	private URLConnection connection;

	public void setHttpsURLConnection(URLConnection connection) {
		this.connection = connection;
	}

	protected URLConnection getHttpsURLConnection() {
		return connection;
	}

}
