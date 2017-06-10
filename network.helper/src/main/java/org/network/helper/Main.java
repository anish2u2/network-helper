package org.network.helper;

import java.util.HashMap;
import java.util.Map;

import org.network.helper.contracts.Network;
import org.network.helper.exception.ConnectionInterruptedException;
import org.network.helper.handler.NetworkHandler;

import com.secure.hypernyms.Hypernyms;

public class Main {

	public static void main(String[] args) throws ConnectionInterruptedException {
		Network network = NetworkHandler.getInstance();
		// http://localhost:8080/file/fileTransfer.html?reciever=anish
		network.initializeConnection("https://digi-yojak.rhcloud.com/login.json", Network.POST,
				Network.CONTENT_TYPE_APPLICATION_JSON);
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", "anish2u2");
		map.put("password", "anish2u2");
		network.communicate(map);
		System.out.println("response:" + network.getNetworkResponse());
		network.initializeConnection("https://digi-yojak.rhcloud.com/fileTransfer.html?reciever=anish&tocken=MTY=",
				Network.POST, Network.CONTENT_TYPE_MULTIPART_FORM_DATA);
		network.setTocken("x-sanchalak-tocken", "MTY=");
		network.setMultiPartRequest(true);
		Hypernyms hypernyms = Hypernyms.getInstance();
		hypernyms.setKey("anish@2017");
		hypernyms.setKeyFileAbsolutePath("D:/hypernyms.key");
		hypernyms.generateCipherKey();
		hypernyms.getCipher();
		try {
			network.setMultiPartRequestBody("file", "D:/cdlsi_mitsubishi_logo.jpg", null, true);
			// Map<String, String> map = new HashMap<String, String>();
			// map.put("userName", "anish2u2");
			// map.put("password", "anish2u2");
			network.setCipher(hypernyms.getCipher());
			network.communicate();

			System.out.println(network.getNetworkResponse());
			network.closeConnection();

			/*
			 * BufferedReader bufferedInputStream = new BufferedReader(new
			 * InputStreamReader(network.getResponseStream())); String line =
			 * bufferedInputStream.readLine(); while (line != null) {
			 * System.out.println("line:" + line); } network.closeConnection();
			 */
			// ThreadUtility.getInstance().removeAllDataAssociatedWithThisThread();

			/*network.initializeConnection("https://digi-yojak.rhcloud.com/fileTransfer.html?reciever=anish&tocken=MTY=",
					Network.GET, "plain/text"); // network.setMultiPartRequest(true);
			network.setCipher(hypernyms.getCipher());
			System.out.println("communicating to server.."); //
			network.writeResponseToFile("D:/_newDecryptedFile.jpg", null, true);
			network.communicate();
			network.readFileResponseFromNetwork("D:/_anish_x_newDecryptedFile.jpg", true); //
			network.closeConnection();*/

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		network.closeConnection();
	}

}
