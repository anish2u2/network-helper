package org.network.helper.contracts;

public interface Worker {

	public void assignWork(Work work);

	public void startWork();

	public boolean isWorkInProgress();

	public void shutDownWork();

	public boolean checkThreadAlive();

	public void releasWorker();

	public long getIdealTime();

	public void releaseResources();

	public void makeItDeamonWorker();
}
