package com.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import model.Account;
import model.Customer;

public class AccountLRUCache implements ICache {

	private static final int defaultCapacity = 50;

	private static LinkedHashMap<Long, Account> accountCache = new LinkedHashMap<>() {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Map.Entry<Long, Account> eld) {
			return size() > defaultCapacity;
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> V get(K accountNo) {
		Account account;
		if((account = (Account)accountCache.get((long)accountNo)) != null) {
			remove((long)accountNo);
			set((long)accountNo, account);
		}
		return (V)account;
	}

	@Override
	public <K, T> void set(K key, T obj) {
		accountCache.put((long)key, (Account)obj);
	}

	@Override
	public <K> void remove(K key) {
		accountCache.remove((long)key);
	}

}
