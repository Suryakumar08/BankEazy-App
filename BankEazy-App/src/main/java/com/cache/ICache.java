package com.cache;

import exception.CustomBankException;

public interface ICache<K,V> {
	
	V get(K key) throws CustomBankException;
	
	void set(K key, V value) throws CustomBankException;
	
	void remove(K key) throws CustomBankException;
}
