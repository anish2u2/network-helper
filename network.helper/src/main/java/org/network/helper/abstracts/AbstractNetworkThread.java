package org.network.helper.abstracts;

import java.util.Date;

import org.network.helper.contracts.Work;
import org.network.helper.contracts.Worker;
import org.network.helper.work.thread.ThreadUtility;

public abstract class AbstractNetworkThread extends Thread implements Worker {

	private Work work;

	protected boolean shouldWeStartJob;

	protected boolean isWorkDone;

	protected boolean shutDownCurrentWork;

	protected boolean keepAlive;

	protected boolean isReleaseWorkerCommand;

	private Date lastWorkingTime;

	public long getIdealTime() {
		return (System.currentTimeMillis() - lastWorkingTime.getTime());
	}

	public void releasWorker() {
		isReleaseWorkerCommand = true;
	}

	protected Work getAssignedWork() {
		return work;
	}

	public void assignWork(Work work) {
		this.work = work;

	}

	public void startWork() {
		shouldWeStartJob = true;
		isWorkDone = false;
		// System.out.println("stating thread..");
	}

	public boolean isWorkInProgress() {

		return !isWorkDone;
	}

	public void shutDownWork() {
		shutDownCurrentWork = true;
	}

	public void releaseResources() {
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		threadUtility.removeAllDataAssociatedWithThisThread();
	}

	public void makeItDeamonWorker() {
		this.setDaemon(true);
	}
}
