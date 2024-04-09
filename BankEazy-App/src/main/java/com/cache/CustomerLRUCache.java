package com.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import model.Customer;

public class CustomerLRUCache implements ICache{
	
	private static final int defaultCapacity = 50;
	
	private static LinkedHashMap<Integer, Customer> customerCache = new LinkedHashMap<>() {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Map.Entry<Integer, Customer> eld) {
			return size() > defaultCapacity;
		}
	};

	@Override
	public <K, T> void set(K key, T obj) {
		customerCache.put((int)key, (Customer)obj);
	}

	@Override
	public <K> void remove(K key) {
		customerCache.remove((int)key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> V get(K id) {
		Customer currCustomer;
		if((currCustomer = (Customer)customerCache.get((int)id)) != null) {
			remove((int)id);
			customerCache.put((int)id, currCustomer);
		}
		return (V)currCustomer;
	}
}
