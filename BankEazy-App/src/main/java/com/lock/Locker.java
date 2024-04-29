package com.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Locker {
	private static Map<String, Lock> locks = new ConcurrentHashMap<String, Lock>();;

	private static class Lock {
		private int count = 0;

		public synchronized int getCount() {
			return count;
		}

		public synchronized void addCount() {
			this.count += 1;
		}

		public synchronized void reduceCount() {
			this.count -= 1;
		}
	}

	public static Object lock(String key) {
		synchronized (locks) {
			Lock currLock = locks.get(key);
			if (currLock == null) {
				currLock = new Lock();
				currLock.addCount();
				locks.put(key, currLock);
			}
			return currLock;
		}
	}

	public static void unLock(String key) {
		Lock lockObj = locks.get(key);
		if (lockObj != null) {
			lockObj.reduceCount();
			if (lockObj.getCount() == 0) {
				synchronized (locks) {
					if (lockObj.getCount() == 0) {
						locks.remove(key);
					}
				}
			}
		}
	}


//	static {
//		
//		ScheduledExecutorService lockGCExecutor = Executors.newSingleThreadScheduledExecutor();
//		Runnable task = () ->{
//			if(locks != null && locks.size() > 1000) {
//				for(Map.Entry<String, Lock> el : locks.entrySet()) {
//					if(el.getValue().getCount() == 0) {
//						locks.remove(el.getKey());
//					}
//				}
//			}
//		};
//		
//		lockGCExecutor.scheduleAtFixedRate(task, 0, 1, TimeUnit.HOURS);
//	}

}
