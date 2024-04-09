package com.cache;

public interface ICache {
	
	<K, V>V get(K id);
	
	<K, T>void set(K key, T obj);
	
	<K>void remove(K key);
}
