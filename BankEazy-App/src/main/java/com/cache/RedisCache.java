package com.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import exception.CustomBankException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCache<K, V> implements ICache<K, V>{
	JedisPool pool = new JedisPool("localhost", 6379);
	Jedis jedis = null;
	public RedisCache(int capacity) {
		jedis = pool.getResource();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) throws CustomBankException {
		byte[] keyBytes = getByteArray(key);
		byte[] resultArr = jedis.get(keyBytes);
		System.out.println(Arrays.toString(resultArr));
		if(resultArr == null) {
			return null;
		}
		else {
			Object obj = getObjectFromByteArray(resultArr);	
			System.out.println(obj.getClass());
			return (V)obj;
		}
	}

	@Override
	public void set(K key, V value) throws CustomBankException{
		byte[] keyBytes = getByteArray(key);
		byte[] valueBytes = getByteArray(value);
		jedis.set(keyBytes, valueBytes);
	}

	@Override
	public void remove(K key) throws CustomBankException {
		byte[] keyBytes = getByteArray(key);
		jedis.del(keyBytes);
	}
	
	private byte[] getByteArray(Object obj) throws CustomBankException{
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)){
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CustomBankException("Serialization failed!", e);
		}
	}
	
	private Object getObjectFromByteArray(byte[] bytes) throws CustomBankException{
		try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais);){
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new CustomBankException("Deserialization failed!", e);
		}
	}
}
