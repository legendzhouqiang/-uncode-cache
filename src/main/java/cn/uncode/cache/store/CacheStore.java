package cn.uncode.cache.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.uncode.cache.framework.ICache;
import cn.uncode.cache.store.local.CacheTemplate;
import cn.uncode.cache.store.redis.CacheData;

public class CacheStore implements ICache<Object, Object> {

    private String        storeRegion;
    private CacheTemplate cacheTemplate;

    public CacheStore(String storeRegion, CacheTemplate cacheTemplate) {
        this.storeRegion = storeRegion;
        this.cacheTemplate = cacheTemplate;
    }
    
    @Override
	public void put(Object key, Object value, int expireTime) {
    	cacheTemplate.set(storeRegion, String.valueOf(key), value, expireTime);
	}

	@Override
	public void remove(Object key) {
		this.cacheTemplate.del(this.storeRegion, String.valueOf(key));
	}

	@Override
	public List<Object> keys(String pattern) {
		List<Object> keylist = new ArrayList<Object>();
		Set<String> keys = cacheTemplate.keys(storeRegion, pattern);
		keylist.addAll(keys);
		return keylist;
	}

	@Override
	public int size() {
		return cacheTemplate.size(storeRegion);
	}

	@Override
	public boolean isExists(Object key) {
		Object val = get(key);
		if(null != val){
			return true;
		}
		return false;
	}

	@Override
	public Object get(Object key) {
		return this.cacheTemplate.get(this.storeRegion, String.valueOf(key));
	}

	@Override
	public void put(Object key, Object value) {
		this.cacheTemplate.set(this.storeRegion, String.valueOf(key), value);
	}

	@Override
	public void clear() {
		 this.cacheTemplate.rem(this.storeRegion);
	}
	
	public Object putIfAbsent(Object key, Object value) {
		return this.cacheTemplate.setIfAbsent(this.storeRegion, String.valueOf(key), value);
	}

	@Override
	public Object get(Object key, cn.uncode.cache.framework.ICache.Level level) {
		return cacheTemplate.get(storeRegion, String.valueOf(key), level);
	}

	@Override
	public void put(Object key, Object value, cn.uncode.cache.framework.ICache.Level level) {
		cacheTemplate.set(storeRegion, String.valueOf(key), value, level);
		
	}

	@Override
	public void put(Object key, Object value, int expireTime, cn.uncode.cache.framework.ICache.Level level) {
		cacheTemplate.set(storeRegion, String.valueOf(key), value, expireTime, level);
		
	}

	@Override
	public Object putIfAbsent(Object key, Object value, int expireTime) {
		return this.cacheTemplate.setIfAbsent(this.storeRegion, String.valueOf(key), value, expireTime);
	}

	@Override
	public void remove(Object key, cn.uncode.cache.framework.ICache.Level level) {
		cacheTemplate.del(storeRegion, String.valueOf(key), level);
	}

	@Override
	public void removeAll() {
		cacheTemplate.rem(storeRegion);
		
	}

	@Override
	public void removeAll(cn.uncode.cache.framework.ICache.Level level) {
		cacheTemplate.rem(storeRegion, level);
		
	}

	@Override
	public int ttl(Object key) {
		return cacheTemplate.ttl(storeRegion, String.valueOf(key));
	}

	@Override
	public int ttl(Object key, cn.uncode.cache.framework.ICache.Level level) {
		return cacheTemplate.ttl(storeRegion, String.valueOf(key), level);
	}

	@Override
	public boolean isExists(Object key, cn.uncode.cache.framework.ICache.Level level) {
		return cacheTemplate.isExists(storeRegion, String.valueOf(key), level);
	}

	@Override
	public Set<String> storeRegions() {
		return cacheTemplate.names();
	}

	@Override
	public Map<String, Set<String>> storeRegionKeys() {
		return cacheTemplate.stores();
	}

	
	 public List<CacheData> fetch(String key){
		 return cacheTemplate.fetch(storeRegion, key);
	 }
	
}


