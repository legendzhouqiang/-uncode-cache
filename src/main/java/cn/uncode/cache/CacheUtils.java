package cn.uncode.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.uncode.cache.framework.ICache;
import cn.uncode.cache.framework.util.CacheCodeUtil;
import cn.uncode.cache.store.redis.cluster.JedisClusterCustom;
import cn.uncode.cache.store.redis.cluster.RedisStore;

public class CacheUtils {

	private static final Logger LOG = LoggerFactory.getLogger(CacheUtils.class);

	private static ICache<Object, Object> cache;

	@SuppressWarnings("unchecked")
	public static ICache<Object, Object> getCache() {
		if (cache == null) {
			CacheUtils.cache = (ICache<Object, Object>) ConfigCacheManager
					.getApplicationContext().getBean(ICache.class);
		}
		return CacheUtils.cache;
	}

	public static Object get(Object key) {
		Object result = getCache().get(key);
		LOG.debug("[get] redis cache read,key:" + key + ",result:" + result);
		return result;
	}

	public static void put(Object key, Object value) {
		getCache().put(key, value);
		LOG.debug("[put] redis cache write,key:" + key + ",value:" + value);
	}

	public static void put(Object key, Object value, int expireTime) {
		getCache().put(key, value, expireTime);
		LOG.debug("[put] redis cache write,key:" + key + ",value:" + value
				+ ",expire:" + expireTime);
	}
	
	public static List<String> getKeys(String pattern){
		List<Object> list = getCache().keys(pattern);
		List<String> rt = new ArrayList<String>();
		if(list != null && list.size() > 0){
			for(Object obj:list){
				rt.add(String.valueOf(obj));
			}
		}
		return rt;
	}

	public static boolean isExists(Object key) {
		return getCache().isExists(key);
	}

	public static void remove(Object key) {
		getCache().remove(key);
		LOG.debug("[remove] redis cache write,key:" + key);
	}

	public static List<Object> keys(String pattern) {
		List<Object> keys = getCache().keys(pattern);
		LOG.debug("[keys] redis cache read, pattern:" + pattern + ",keys:" + keys);
		return keys;
	}

	public static int size() {
		int size = getCache().size();
		LOG.debug("[size] redis cache read, size:" + size);
		return size;
	}

	public static void setCache(ICache<Object, Object> cache) {
		CacheUtils.cache = cache;
	}

	public static JedisClusterCustom getRedisCache() {
		ICache<Object, Object> cache = getCache();
		if (cache instanceof RedisStore) {
			RedisStore redisStore = (RedisStore) getCache();
			return redisStore.getJedisCluster();
		}
		LOG.debug("Redis instance is null.");
		return null;
	}

	/**
	 * 向list添加数据
	 * 
	 * @param listKey
	 *            list的key
	 * @param value
	 *            值
	 */
	public static void listAdd(String listKey, String value) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			jedisCluster.rpush(listKey, value);
			LOG.debug("[listAdd] redis cache write,key:", listKey + ",value:"
					+ value);
		} catch (Exception e) {
			LOG.error("[listAdd] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
	}

	/**
	 * 从list中取出指定范围的值
	 * 
	 * @param listKey
	 *            list的key
	 * @param start
	 *            起始位置
	 * @param end
	 *            结束位置
	 * @return 指定范围的值
	 */
	public static List<String> listGet(String listKey, int start, int end) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			List<String> values = jedisCluster.lrange(listKey, start, end);
			LOG.debug("[listGet] redis cache read,start:" + start + ",end:"
					+ end + ",key:", listKey + ",value:" + values);
			return values;
		} catch (Exception e) {
			LOG.error("[listGet] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 从list中取出所有值
	 * 
	 * @param listKey
	 *            list的key
	 * @return 所有值
	 */
	public static List<String> listGet(String listKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			List<String> values = jedisCluster.lrange(listKey, 0, -1);
			LOG.debug("[listGet] redis cache read,key:", listKey + ",value:"
					+ values);
			return values;
		} catch (Exception e) {
			LOG.error("[listGet] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 取list的大小
	 * 
	 * @param listKey
	 *            list的key
	 * @return 大小
	 */
	public static long listSize(String listKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			long size = jedisCluster.llen(listKey);
			LOG.debug("[listSize] redis cache read,key:", listKey + ",size:"
					+ size);
			return size;
		} catch (Exception e) {
			LOG.error("[listSize] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return 0;
	}

	/**
	 * 对list进行排序
	 * 
	 * @param listKey
	 *            list的key
	 * @return 排序后的结果
	 */
	public static List<String> listSort(String listKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			List<String> result = jedisCluster.sort(listKey);
			LOG.debug("[listSort] redis cache read,key:", listKey + ",result:"
					+ result);
			return result;
		} catch (Exception e) {
			LOG.error("[listSort] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 修改list中单个下标的值
	 * 
	 * @param listKey
	 *            list的key
	 * @param index
	 *            下标
	 * @param value
	 *            值
	 * @return 排序后的结果
	 */
	public static List<String> listSet(String listKey, int index, String value) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			jedisCluster.lset(listKey, index, value);
			LOG.debug("[listSet] redis cache write,key:", listKey + ".index:"
					+ index + ",result:" + value);
		} catch (Exception e) {
			LOG.error("[listSet] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 获取指定下标的值
	 * 
	 * @param listKey
	 *            list的key
	 * @param index
	 *            下标
	 * @return 指定下标的值
	 */
	public static String listIndex(String listKey, int index) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			String result = jedisCluster.lindex(listKey, index);
			LOG.debug("[listIndex] redis cache read,key:", listKey + ".index:"
					+ index + ",result:" + result);
			return result;
		} catch (Exception e) {
			LOG.error("[listIndex] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * list出栈
	 * 
	 * @param listKey
	 * @return 值
	 */
	public static String listPop(String listKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			String result = jedisCluster.lpop(listKey);
			LOG.debug("[listPop] redis cache read,key:", listKey + ",result:"
					+ result);
			return result;
		} catch (Exception e) {
			LOG.error("[listPop] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 删除指定下标的值
	 * 
	 * @param listKey
	 * @return 排序后的结果
	 */
	public static long listRemove(String listKey, int count, String value) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			listKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, listKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			LOG.debug("[listRemove] redis cache write,key:", listKey
					+ ",count:" + count + ",value:" + value);
			return jedisCluster.lrem(listKey, count, value);
		} catch (Exception e) {
			LOG.error("[listRemove] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return 0;
	}

	/**
	 * 向set添加数据
	 * 
	 * @param setKey
	 *            set的key
	 * @param value
	 *            值
	 */
	public static void setAdd(String setKey, String value) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			setKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, setKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			jedisCluster.sadd(setKey, value);
			LOG.debug("[setAdd] redis cache write,key:", setKey + ",value:"
					+ value);
		} catch (Exception e) {
			LOG.error("[setAdd] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
	}

	/**
	 * 从set中取出所有值
	 * 
	 * @param setKey
	 *            set的key
	 * @return 所有值
	 */
	public static Set<String> setGet(String setKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			setKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, setKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			Set<String> result = jedisCluster.smembers(setKey);
			LOG.debug("[setGet] redis cache read,key:", setKey + ",value:"
					+ result);
			return result;
		} catch (Exception e) {
			LOG.error("[setGet] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 取set的大小
	 * 
	 * @param setKey
	 *            set的key
	 * @return 大小
	 */
	public static long setSize(String setKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			setKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, setKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.scard(setKey);
		} catch (Exception e) {
			LOG.error("[setSize] redis cache error", e);
		} finally {
			/* pool.returnResource(jedisCluster); */
		}

		return 0;
	}

	/**
	 * setKey是否存在
	 * 
	 * @param setKey
	 *            set的key
	 * @return 结果
	 */
	public static boolean setExists(String setKey, String value) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			setKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, setKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.sismember(setKey, value);
		} catch (Exception e) {
			LOG.error("[setExists] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return false;
	}

	/**
	 * set出栈
	 * 
	 * @param setKey
	 * @return 值
	 */
	public static String setPop(String setKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			setKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, setKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.spop(setKey);
		} catch (Exception e) {
			LOG.error("[setPop] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 删除指定的值
	 * 
	 * @param setKey
	 * @return
	 */
	public static long setRemove(String setKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			setKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, setKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.srem(setKey);
		} catch (Exception e) {
			LOG.error("[setRemove] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return 0;
	}

	/**
	 * 两个set的交集
	 * 
	 * @param setKeys
	 *            key名称集
	 * @return
	 */
	/*
	 * public static Set<String> setInner(String... setKeys) {
	 * 
	 * try { JedisClusterCustom jedisCluster = CacheUtils.getRedisCache(); if
	 * (jedisCluster == null) { throw new Exception("jedisCluster is null."); }
	 * return jedisCluster.sinter(setKeys);
	 * 
	 * } catch (Exception e) { LOG.error("[setInner] redis cache error", e); }
	 * finally { //pool.returnResource(jedisCluster); }
	 * 
	 * return null; }
	 */

	/**
	 * 两个set的并集
	 * 
	 * @param setKeys
	 *            key名称集
	 * @return
	 */
	public static Set<String> setUnion(String... setKeys) {
		/*
		 * try { JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		 * if (jedisCluster == null) { throw new
		 * Exception("jedisCluster is null."); } return
		 * jedisCluster.sunion(setKeys); } catch (Exception e) {
		 * LOG.error("[setUnion] redis cache error", e); } finally { //
		 * pool.returnResource(jedisCluster); }
		 */
		return null;
	}

	/**
	 * 两个set差集
	 * 
	 * @param setKeys
	 *            key名称集
	 * @return
	 */
	/*
	 * public static Set<String> setDiff(String... setKeys) {
	 * 
	 * try { JedisClusterCustom jedisCluster = CacheUtils.getRedisCache(); if
	 * (jedisCluster == null) { throw new Exception("jedisCluster is null."); }
	 * return jedisCluster.sdiff(setKeys); } catch (Exception e) {
	 * LOG.error("[setDiff] redis cache error", e); } finally {
	 * pool.returnResource(jedisCluster); }
	 * 
	 * return null; }
	 */

	/**
	 * 保存map
	 * 
	 * @param mapKey
	 *            map的key
	 * @param map
	 *            值
	 */
	public static void mapSave(String mapKey, Map<String, String> map) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			jedisCluster.hmset(mapKey, map);
		} catch (Exception e) {
			LOG.error("[mapSave] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
	}

	/**
	 * map存数据
	 * 
	 * @param mapKey
	 *            map的key
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void mapPut(String mapKey, String key, String value) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			jedisCluster.hset(mapKey, key, value);
		} catch (Exception e) {
			LOG.error("[mapPut] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
	}

	/**
	 * map的key是否存在
	 * 
	 * @param mapKey
	 *            map的key
	 * @param key
	 *            键
	 */
	public static void mapExists(String mapKey, String key) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			jedisCluster.hexists(mapKey, key);
		} catch (Exception e) {
			LOG.error("[mapExists] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
	}

	/**
	 * 获取map指定key的值
	 * 
	 * @param mapKey
	 *            map的key
	 * @param key
	 *            键
	 */
	public static String mapGet(String mapKey, String key) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.hget(mapKey, key);
		} catch (Exception e) {
			LOG.error("[mapGet] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 获取map所有keys
	 * 
	 * @param mapKey
	 *            map的key
	 */
	public static Set<String> mapKeys(String mapKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.hkeys(mapKey);
		} catch (Exception e) {
			LOG.error("[mapKeys] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 获取map所有values
	 * 
	 * @param mapKey
	 *            map的key
	 */
	public static List<String> mapValues(String mapKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.hvals(mapKey);
		} catch (Exception e) {
			LOG.error("[mapValues] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 获取map的个数
	 * 
	 * @param mapKey
	 *            map的key
	 */
	public static Long mapSize(String mapKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.hlen(mapKey);
		} catch (Exception e) {
			LOG.error("[mapSize] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 获取整个map的值
	 * 
	 * @param mapKey
	 *            map的key
	 */
	public static Map<String, String> mapGetAll(String mapKey) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {
			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.hgetAll(mapKey);
		} catch (Exception e) {
			LOG.error("[mapGetAll] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return null;
	}

	/**
	 * 删除map中某个键的值
	 * 
	 * @param mapKey
	 *            map的key
	 */
	public static Long mapRemove(String mapKey, String... keys) {
		String storeRegion = ConfigCacheManager.getCacheConfig().getStoreRegion();
		if(StringUtils.isNotBlank(storeRegion)){
			mapKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, mapKey);
		}
		JedisClusterCustom jedisCluster = CacheUtils.getRedisCache();
		try {

			if (jedisCluster == null) {
				throw new Exception("jedisCluster is null.");
			}
			return jedisCluster.hdel(mapKey, keys);
		} catch (Exception e) {
			LOG.error("[mapRemove] redis cache error", e);
		} finally {
			//jedisCluster.close();
		}
		return 0L;
	}

}
