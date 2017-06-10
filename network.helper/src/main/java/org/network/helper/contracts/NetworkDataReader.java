package org.network.helper.contracts;

import java.net.URLConnection;

import org.network.helper.exception.NetworkDataReaderException;

import com.secure.contracts.HypernymsCipher.Cipher;

public interface NetworkDataReader {

	public void setHttpsURLConnection(URLConnection connection);

	public Object readResponse() throws NetworkDataReaderException;
		
	public void readResponseToFile(String fileName,boolean useDecryption);
	
	public void setCipher(Cipher cipher);
	
}
