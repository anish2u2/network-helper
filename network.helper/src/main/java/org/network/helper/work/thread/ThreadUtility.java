package org.network.helper.work.thread;

import java.util.HashMap;
import java.util.Map;

public class ThreadUtility {

	private ThreadLocal<Map<String, Object>> threadLocalData;

	private static ThreadUtility threadUtility;

	private ThreadUtility() {
		threadLocalData = new ThreadLocal<Map<String, Object>>();
	}

	public static ThreadUtility getInstance() {
		if (threadUtility == null) {
			threadUtility = new ThreadUtility();
		}
		return threadUtility;
	}

	public void add(String key, Object data) {
		if (this.threadLocalData.get() == null)
			this.threadLocalData.set(new HashMap<String, Object>());
		this.threadLocalData.get().put(key, data);
	}

	public void add(Map<String, Object> data) {
		if (this.threadLocalData.get() == null)
			this.threadLocalData.set(new HashMap<String, Object>());
		this.threadLocalData.get().putAll(data);
	}

	public Object get(String key) {
		if (this.threadLocalData.get() == null)
			this.threadLocalData.set(new HashMap<String, Object>());
		return this.threadLocalData.get().get(key);
	}

	public void removeData(String key) {
		this.threadLocalData.get().remove(key);
	}

	public void removeAllDataAssociatedWithThisThread() {
		this.threadLocalData.remove();
	}

	public Map<String, Object> getMap() {
		return threadLocalData.get();
	}
}
