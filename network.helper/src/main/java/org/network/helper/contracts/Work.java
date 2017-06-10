package org.network.helper.contracts;

import org.network.helper.work.thread.ThreadUtility;

public interface Work {

	public void doWork();
	
	public static class ShutDownRequestVerifier {

		public static boolean isShutDownRequest() {
			ThreadUtility threadUtility = ThreadUtility.getInstance();
			if (threadUtility.get(NetworkSupport.CLOSE_NETWORK_CONNECTION) != null
					&& Boolean.valueOf(threadUtility.get(NetworkSupport.CLOSE_NETWORK_CONNECTION).toString())) {
				//System.out.println("Shooting down thread..");
				return true;
			}
			return false;
		}

	}

}
