package cn.uncode.cache.store.redis.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.uncode.cache.CacheUtils;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisException;

public class RedisDistributeLock implements DistributeLock {

	private static final Log LOG = LogFactory.getLog(RedisDistributeLock.class);

	/**
	 * 锁的key标识，根据业务逻辑调整
	 */
	private final String key;

	/**
	 * 超时时间，无限等待锁会造成系统崩溃
	 */
	private long timeout = 2 * 1000L;

	public RedisDistributeLock(String key) {
		this.key = key;
	}

	public boolean lock() {
		// RedisLock非单例，Jedis切勿声明为static,会锁全DB
		boolean rt = false;
		JedisCluster jedisCluster = CacheUtils.getRedisCache();
		if (jedisCluster == null) {
			throw new JedisException("jedisCluster is null.");
		}
		try {
			long tryLocktime = System.currentTimeMillis();
			while (System.currentTimeMillis() - tryLocktime < timeout) {
				// value可随意设置，存储时间类信息便于问题发生后的时间定位
				String value = String.valueOf(System.currentTimeMillis());

				// 尝试写入key(上锁)
				long result = jedisCluster.setnx(key, value);
				if (result == 1) {
					// 获取成功，设置有效时间，单是秒
					jedisCluster.expire(key, 30);
					LOG.info("{" + Thread.currentThread().getName() + "} begin");
					rt = true;
					break;
				}

				// 等待一段时间后尝试重获取key
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			/* pool.returnResource(jedis); */
		}
		return rt;
	}

	public boolean unlock() {
		// RedisLock非单例，Jedis切勿声明为static,会锁全DB
		JedisCluster jedisCluster = CacheUtils.getRedisCache();
		if (jedisCluster == null) {
			throw new JedisException("jedisCluster is null.");
		}
		try {
			long result = jedisCluster.del(key);
			if (result != 1) {
				LOG.warn("unlock fail ,key -> " + key);
			}
			LOG.info("{" + Thread.currentThread().getName() + "} end");
			return result == 1;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			/* pool.returnResource(jedis); */
		}
		return false;

	}

}
