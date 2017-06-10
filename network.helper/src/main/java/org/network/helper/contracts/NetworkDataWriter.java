package org.network.helper.contracts;

import java.net.URLConnection;

import org.network.helper.exception.NetworkDataWriterException;

public interface NetworkDataWriter {

	public void writeData(Object payloads) throws NetworkDataWriterException;

	public void setHttpsURLConnection(URLConnection connection);
	
	public void setContentType(String contentType);
}
