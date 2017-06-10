package org.network.helper.work.thread;

import org.network.helper.abstracts.AbstractNetworkThread;
import org.network.helper.contracts.NetworkSupport;

public class NetworkThread extends AbstractNetworkThread {

	public NetworkThread() {
		super();
	}

	@Override
	public void run() {
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		threadUtility.add(NetworkSupport.CLOSE_NETWORK_CONNECTION, false);
		while (true) {
			if (shouldWeStartJob && !shutDownCurrentWork && !isWorkDone) {
				System.out.println("Starting execution of thread..");
				if (this.shutDownCurrentWork) {
					threadUtility.add(NetworkSupport.CLOSE_NETWORK_CONNECTION, true);
					return;
				}
				getAssignedWork().doWork();
				isWorkDone = true;
				shouldWeStartJob = false;
			} else {
				try {
					Thread.sleep(10000);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (isReleaseWorkerCommand){
					threadUtility.removeAllDataAssociatedWithThisThread();
					break;
				}
			}
		}
	}

	public boolean checkThreadAlive() {
		return super.isAlive();
	}

}
