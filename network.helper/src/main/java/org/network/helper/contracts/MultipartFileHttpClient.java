package org.network.helper.contracts;

import java.io.InputStream;
import java.io.OutputStream;

public interface MultipartFileHttpClient {

	public void openConnection();

	public void writeData(String parameterName, String fileName, InputStream inputStream,
			boolean shouldBeEncryptedUsingCipher);

	public void readData(String fileName, OutputStream outputStream, boolean shouldBeDecryptedUsingCipher);

	public byte[] read();
}
