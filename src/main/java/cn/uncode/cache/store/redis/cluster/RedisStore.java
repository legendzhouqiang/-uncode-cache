package cn.uncode.cache.store.redis.cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.uncode.cache.CacheUtils;
import cn.uncode.cache.framework.ICache;
import cn.uncode.cache.framework.util.ByteUtil;
import cn.uncode.cache.framework.util.SerializeUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisStore implements ICache<Object, Object> {

	private static final Logger LOG = LoggerFactory.getLogger(CacheUtils.class);

	private JedisClusterCustom jedisCluster;
	

	public void clear() {
	}

	@Override
	public Serializable get(Object key) {
		byte[] reslut = null;
		byte[] tkey = SerializeUtil.serialize(key);
		try {
			if (jedisCluster.exists(tkey)) {
				reslut = jedisCluster.get(tkey);
				Object object = SerializeUtil.unserialize(reslut);
				if (LOG.isDebugEnabled())
					LOG.debug("-->[get] read from redis success!key:"
							+ key.toString() + ",result:" + object.toString());
				return (Serializable) object;
			} else {
				if (LOG.isDebugEnabled())
					LOG.debug("-->" + " [read] not exists in redis!");
				return null;
			}
		} catch (Exception e) {
			LOG.error("[read] redis cache error", e);
			return null;
		} 
	}

	@Override
	public void put(Object key, Object value) {
		byte[] tkey = SerializeUtil.serialize(key.toString());
		try {
			jedisCluster.set(tkey, SerializeUtil.serialize(value));
			String valStr = "";
			if (value != null) {
				valStr = value.toString();
			}
			LOG.debug("-->[put] write redis success!key:" + key.toString()
					+ ",value:" + valStr);
		} catch (Exception e) {
			LOG.error("[put] redis cache error", e);
		}
	}

	@Override
	public void put(Object key, Object value, int expireTime) {
		byte[] tkey = SerializeUtil.serialize(key.toString());
		try {
			jedisCluster.set(tkey, SerializeUtil.serialize(value));
			jedisCluster.expire(tkey, expireTime);
			String valStr = "";
			if (value != null) {
				valStr = value.toString();
			}
			LOG.debug("-->[put] write redis success!key:" + key.toString()
					+ ",value:" + valStr + ",expire:" + expireTime);
		} catch (Exception e) {
			LOG.error("[put] redis cache error", e);
		} 
	}

	@Override
	public void remove(Object key) {
		byte[] tkey = SerializeUtil.serialize(key.toString());
		try {
			if (jedisCluster.exists(tkey)) {
				jedisCluster.expire(tkey, 0);
			}
			LOG.debug("-->[remove] write redis success!key:" + key.toString());
		} catch (Exception e) {
			LOG.error("[remove] redis cache error", e);
		} finally {
			// jedisCluster.close();
		}
	}

	@Override
	public List<Object> keys(String pattern) {
		List<Object> list = new ArrayList<Object>();
		TreeSet<String> keys = innerKeys(pattern);
		for (String str : keys) {
			Object obj = SerializeUtil.unserialize(ByteUtil.stringToByte(str));
			if (obj != null) {
				list.add(obj);
			}
		}
		return list;
	}

	@Override
	public int size() {
		int count = 0;
		try {
			count = innerKeys("*").size();
		} catch (Exception e) {
			LOG.error("redis cache error", e);
		} finally {
			// jedisPool.returnResource(jedis);
		}
		return count;
	}

	@Override
	public boolean isExists(Object key) {
		byte[] tkey = SerializeUtil.serialize(key.toString());
		try {
			if (jedisCluster.exists(tkey)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			LOG.error("[isExists] redis cache error", e);
			return false;
		}
	}

	public JedisClusterCustom getJedisCluster() {
		return jedisCluster;
	}

	public void setJedisCluster(JedisClusterCustom jedisCluster) {
		this.jedisCluster = jedisCluster;
	}
	
	
	private TreeSet<String> innerKeys(String pattern) {
		TreeSet<String> keys = new TreeSet<String>();
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
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

	@Override
	public Object get(Object key, cn.uncode.cache.framework.ICache.Level level) {
		return null;
	}

	@Override
	public void put(Object key, Object value, cn.uncode.cache.framework.ICache.Level level) {
		
	}

	@Override
	public void put(Object key, Object value, int expireTime, cn.uncode.cache.framework.ICache.Level level) {
		
	}


	@Override
	public void remove(Object key, cn.uncode.cache.framework.ICache.Level level) {
		
	}

	@Override
	public void removeAll() {
		
	}

	@Override
	public void removeAll(cn.uncode.cache.framework.ICache.Level level) {
		
	}

	@Override
	public int ttl(Object key) {
		return 0;
	}

	@Override
	public int ttl(Object key, cn.uncode.cache.framework.ICache.Level level) {
		return 0;
	}

	@Override
	public boolean isExists(Object key, cn.uncode.cache.framework.ICache.Level level) {
		return false;
	}

	@Override
	public Set<String> storeRegions() {
		return null;
	}

	@Override
	public Map<String, Set<String>> storeRegionKeys() {
		return null;
	}

	@Override
	public Object putIfAbsent(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object putIfAbsent(Object key, Object value, int expireTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
