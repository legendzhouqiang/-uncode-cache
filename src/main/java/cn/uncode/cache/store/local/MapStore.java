/**
 * 
 */
package cn.uncode.cache.store.local;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.uncode.cache.framework.ICache;
import cn.uncode.cache.lru.ConcurrentLRUCacheMap;

/**
 * MapStore 使用本地 ConcurrentLRUCacheMap 作为 CacheManage 的缓存存储方案.
 * <p>
 * 
 * <pre>
 * 通过 Key-Value 的形式将对象存入 本地内存中.
 * 
 * 可以采用 PUT , PUT_EXPIRETIME , GET , REMOVE 这三种 Key 操作. 
 * 可以采用 CLEAR , CLEAN 这种范围清除操作.
 * 
 * 使用该 Store . 数据量较小. 0 ~ 1G 访问耗时极低. 
 * 适用于数据量小但变化较多的场合.
 * 
 * 例如基础型数据.
 * </pre>
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月28日
 */

public class MapStore implements ICache<Object, Object> {

	private final ConcurrentLRUCacheMap<Object, ObjectBoxing<Object>> datas;

	public MapStore() {
		datas = new ConcurrentLRUCacheMap<Object, ObjectBoxing<Object>>();
	}

	public MapStore(int size, int segmentSize) {
		datas = new ConcurrentLRUCacheMap<Object, ObjectBoxing<Object>>(size, segmentSize);
	}

	@Override
	public Object get(Object key) {
		ObjectBoxing<Object> storeObject = datas.get(key);
		if (storeObject == null) {
			datas.remove(key);
			return null;
		}

		Object v = storeObject.getObject();
		if (v == null)
			datas.remove(key);

		return v;
	}

	@Override
	public void put(Object key, Object value) {
		this.put(key, value, 0);
	}

	@Override
	public void put(Object key, Object value, int expireTime) {
		if (value == null)
			return;

		ObjectBoxing<Object> storeObject = new ObjectBoxing<Object>(value, expireTime);
		datas.put(key, storeObject);
	}

	@Override
	public void remove(Object key) {
		datas.remove(key);
	}

	@Override
	public void clear() {
		datas.clear();
	}

	@Override
	public int size() {
		return datas.size();
	}

    @Override
    public List<Object> keys(String pattern) {
        return datas.keys(pattern);
    }

	@Override
	public boolean isExists(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(Object key, cn.uncode.cache.framework.ICache.Level level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(Object key, Object value, cn.uncode.cache.framework.ICache.Level level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(Object key, Object value, int expireTime, cn.uncode.cache.framework.ICache.Level level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Object key, cn.uncode.cache.framework.ICache.Level level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll(cn.uncode.cache.framework.ICache.Level level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int ttl(Object key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ttl(Object key, cn.uncode.cache.framework.ICache.Level level) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isExists(Object key, cn.uncode.cache.framework.ICache.Level level) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> storeRegions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<String>> storeRegionKeys() {
		// TODO Auto-generated method stub
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
