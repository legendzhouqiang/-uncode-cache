package cn.uncode.cache.store;

import java.util.List;
import java.util.Set;

import cn.uncode.cache.framework.ICache;
import cn.uncode.cache.store.local.CacheTemplate;

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
		Set<String> keys = cacheTemplate.keys(storeRegion);
		return null;
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
	
	public void putIfAbsent(Object key, Object value) {
		this.cacheTemplate.setIfAbsent(this.storeRegion, String.valueOf(key), value);
	}
		
	
}


