package org.network.helper.work.executore;

import java.util.LinkedList;
import java.util.List;

import org.network.helper.contracts.Work;
import org.network.helper.contracts.Worker;
import org.network.helper.work.thread.NetworkThread;
import org.network.helper.work.thread.ThreadUtility;

public class NetworkThreadExecutore {

	private static NetworkThreadExecutore networkThreadExecutore;

	private List<Worker> workers;

	private boolean isMemoryCheckerStarted;

	private NetworkThreadExecutore() {
		workers = new LinkedList<Worker>();
	}

	public static NetworkThreadExecutore getInstance() {
		if (networkThreadExecutore == null)
			networkThreadExecutore = new NetworkThreadExecutore();
		// System.out.println("Instanciating NetworkThraedExecutor..");
		return networkThreadExecutore;
	}

	public void executeWork(Work work) {
		Worker worker = checkIsAnyThreadAvailableToProcessWork();
		/*
		 * ThreadUtility threadUtility = ThreadUtility.getInstance();
		 * 
		 * if (threadUtility.get("isDeamon") != null &&
		 * Boolean.valueOf(threadUtility.get("isDeamon").toString())) {
		 * worker.makeItDeamonWorker(); }
		 */
		worker.assignWork(work);
		worker.startWork();
		if (!worker.checkThreadAlive())
			((Thread) worker).start();
		System.out.println(((Thread) worker).getName());
		if (!isMemoryCheckerStarted) {
			startDeamonThreadToControlMemoryUsage();
			// threadUtility.removeAllDataAssociatedWithThisThread();
		}
		// System.out.println("execution started..");
	}

	private Worker checkIsAnyThreadAvailableToProcessWork() {
		for (Worker worker : workers) {
			if (!worker.isWorkInProgress()) {
				return worker;
			}
		}
		Worker networkThread = new NetworkThread();
		workers.add(networkThread);
		return networkThread;
	}

	public void startDeamonThreadToControlMemoryUsage() {
		isMemoryCheckerStarted = true;
		new Thread(new Runnable() {
			private final Runtime runtime = Runtime.getRuntime();

			@Override
			public void run() {
				System.out.println("Starting deamon Thread which will monitor memory usage..");
				while (true) {
					System.out.println("Checking memory..");
					try {
						System.out.println("Total Memory:" + (runtime.totalMemory() / 1000000) + "M.B. Free Memory:"
								+ (runtime.freeMemory() / 1000000) + "M.B. Memory Consumed:"
								+ ((runtime.totalMemory() - runtime.freeMemory()) / 1000000) + "M.B.");
						if (((runtime.totalMemory() - runtime.freeMemory()) / 1000000) > 3 && workers != null
								&& !workers.isEmpty()) {

							while (workers.listIterator().hasNext()) {
								Worker worker = workers.listIterator().next();
								if (!worker.isWorkInProgress()) {
									// worker.releaseResources();
									worker.releasWorker();
									worker.shutDownWork();
									System.out.println("Releasing worker:" + ((Thread) worker).getName());
									workers.remove(worker);
								}
							}
							runtime.gc();
						}

						Thread.sleep(10000);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			}

		}).start();
		ThreadUtility threadUtility = ThreadUtility.getInstance();
		threadUtility.add("isDeamon", true);

	}

}
