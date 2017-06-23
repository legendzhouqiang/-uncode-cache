package cn.uncode.cache.store.redis.cluster;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import cn.uncode.cache.framework.util.ByteUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * redis cluster 支持byte[]
 * 
 * @author meff
 * @since 2016-4-21
 */
public class JedisClusterCustom extends JedisCluster {

	public JedisClusterCustom(Set<HostAndPort> jedisClusterNode, int timeout, int maxRedirections, GenericObjectPoolConfig poolConfig) {
		super(jedisClusterNode, timeout, maxRedirections, poolConfig);
	}
	
	public Boolean exists(byte[] bytes) {
		return this.exists(ByteUtil.byteToString(bytes));
	}

	public byte[] get(byte[] bytes) {
		return ByteUtil.stringToByte(this.get(ByteUtil.byteToString(bytes)));
	}

	public String set(byte[] bytes, byte[] object) {
		return this.set(ByteUtil.byteToString(bytes), ByteUtil.byteToString(object));
	}

	public void expire(byte[] bytes, Integer seconds) {
		this.expire(ByteUtil.byteToString(bytes), seconds);
	}

	/**
	 * 自定义keys方法
	 * 
	 * @author meff
	 * @return
	 */

	public TreeSet<String> keys(String pattern) {
		TreeSet<String> keys = new TreeSet<>();
		Map<String, JedisPool> clusterNodes = getClusterNodes();
		for (String key : clusterNodes.keySet()) {
			JedisPool jp = clusterNodes.get(key);
			Jedis connection = jp.getResource();
			try {
				keys.addAll(connection.keys(pattern));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				connection.close();
			}
		}
		return keys;
	}
	
	public Set<byte[]> keys(final byte[] pattern) {
		Set<byte[]> keys = new TreeSet<>();
		Map<String, JedisPool> clusterNodes = getClusterNodes();
		for (String key : clusterNodes.keySet()) {
			JedisPool jp = clusterNodes.get(key);
			Jedis connection = jp.getResource();
			try {
				keys.addAll(connection.keys(pattern));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				connection.close();
			}
		}
		return keys;
	}

}
