package com.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import exception.CustomBankException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import yamlConvertor.YamlMapper;

public class RedisCache<K, V> implements ICache<K, V> {
	private static JedisPoolConfig config = new JedisPoolConfig();
	private static JedisPool pool = null;
	private static YamlMapper mapper = null;

	public RedisCache(int port) {
		config.setMaxTotal(50);
		config.setMaxIdle(5);
		pool = new JedisPool(config, "localhost", port);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) throws CustomBankException {
		checkRedisConf();
		try (Jedis jedis = pool.getResource()) {
			byte[] keyBytes = getByteArray(key);
			byte[] resultArr = jedis.get(keyBytes);
			if (resultArr == null) {
				return null;
			} else {
				Object obj = getObjectFromByteArray(resultArr);
				return (V) obj;
			}
		}
	}

	@Override
	public void set(K key, V value) throws CustomBankException {
		checkRedisConf();
		try (Jedis jedis = pool.getResource()) {
			byte[] keyBytes = getByteArray(key);
			byte[] valueBytes = getByteArray(value);
			jedis.set(keyBytes, valueBytes);
		}
	}

	@Override
	public void remove(K key) throws CustomBankException {
		checkRedisConf();
		try (Jedis jedis = pool.getResource()) {
			byte[] keyBytes = getByteArray(key);
			jedis.del(keyBytes);
		}
	}

	private byte[] getByteArray(Object obj) throws CustomBankException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CustomBankException("Serialization failed!", e);
		}
	}

	private Object getObjectFromByteArray(byte[] bytes) throws CustomBankException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais);) {
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new CustomBankException("Deserialization failed!", e);
		}
	}

	public boolean checkRedisConf() throws CustomBankException{
		boolean result = false;
		try {
			mapper = new YamlMapper();
			result = mapper.useRedisCache();
		} catch (CustomBankException e) {
		}
		if(result == false) {
			throw new CustomBankException("Redis Cache usage is blocked as of now!");
		}
		return true;
	}
}
