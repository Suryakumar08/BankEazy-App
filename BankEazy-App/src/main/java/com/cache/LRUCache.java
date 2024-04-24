package com.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> implements ICache<K,V> {

	private Map<K, V> cache = null;

	public LRUCache(int capacity) {
		cache = new LinkedHashMap<K, V>() {
			private static final long serialVersionUID = 1L;

			protected boolean removeEldestEntry(Map.Entry<K, V> eld) {
				return size() > capacity;
			}
		};
	}

	@Override
	public synchronized V get(K key) {
		V value;
		if ((value = cache.get(key)) != null) {
			remove(key);
			set(key, value);
		}
		return value;
	}

	@Override
	public synchronized void set(K key, V obj) {
		cache.put(key, obj);
	}

	@Override
	public synchronized void remove(K key) {
		cache.remove(key);
	}

}
