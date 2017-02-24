package cn.uncode.cache.framework;

import static cn.uncode.cache.framework.listener.CacheOprator.GET;
import static cn.uncode.cache.framework.listener.CacheOprator.PUT;
import static cn.uncode.cache.framework.listener.CacheOprator.PUT_EXPIRE;
import static cn.uncode.cache.framework.listener.CacheOprator.REMOVE;
import static cn.uncode.cache.framework.listener.CacheOprator.REMOVE_ALL;

import java.util.List;

import cn.uncode.cache.framework.config.MethodConfig;
import cn.uncode.cache.framework.listener.CacheObservable;
import cn.uncode.cache.framework.listener.CacheOprateInfo;
import cn.uncode.cache.framework.util.CacheCodeUtil;

/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月24日
 */
public class CacheProxy<K, V> extends CacheObservable {

    private String storeRegion;
    private String key;

    /** 注入真正的cache实现 */
    private ICache<K, V> cache;

    private MethodConfig methodConfig;

    public CacheProxy(String storeRegion, String key,
            ICache<K, V> cache, MethodConfig methodConfig) {
        this.storeRegion = storeRegion;
        this.key = key;
        this.cache = cache;
        this.methodConfig = methodConfig;
    }

    public V get(K key, String ip) {
        if (!isUseCache)
            return null;

        CacheException cacheException = null;
        V v = null;

        long start = System.currentTimeMillis();
        try {
            v = cache.get(key);
        } catch (CacheException e) {
            cacheException = e;
        }

        long end = System.currentTimeMillis();

        if (v != null) // 命中，通知listener
            notifyListeners(GET, new CacheOprateInfo(key, end - start, true, methodConfig, cacheException, ip));

        return v;
    }

    public void put(K key, V value, String ip) {

        CacheException cacheException = null;

        long start = System.currentTimeMillis();
        try {
            cache.put(key, value);
        } catch (CacheException e) {
            cacheException = e;
        }

        long end = System.currentTimeMillis();

        // listener
        notifyListeners(PUT, new CacheOprateInfo(key, end - start, true, methodConfig, cacheException, ip));
    }

    public void put(K key, V value, int expireTime, String ip) {
        CacheException cacheException = null;

        long start = System.currentTimeMillis();
        try {
            cache.put(key, value, expireTime);
        } catch (CacheException e) {
            cacheException = e;
        }
        long end = System.currentTimeMillis();

        // listener
        notifyListeners(PUT_EXPIRE, new CacheOprateInfo(key, end - start, true, methodConfig, cacheException, ip));
    }

    public void remove(K key, String ip) {
        CacheException cacheException = null;

        long start = System.currentTimeMillis();
        try {
            cache.remove(key);
        } catch (CacheException e) {
            cacheException = e;
        }
        long end = System.currentTimeMillis();

        // listener
        notifyListeners(REMOVE, new CacheOprateInfo(key, end - start, true, methodConfig, cacheException, ip));
    }
    
    public void removeAll(String ip){
        CacheException cacheException = null;
        long start = System.currentTimeMillis();
        try {
            String pattern = CacheCodeUtil.getCacheAdapterKey(storeRegion, methodConfig);
            List<K> keys = cache.keys(pattern);
            for(K k:keys){
                cache.remove(k);
            }
        } catch (CacheException e) {
            cacheException = e;
        }
        long end = System.currentTimeMillis();
        // listener
        notifyListeners(REMOVE_ALL, new CacheOprateInfo(key, end - start, true, methodConfig, cacheException, ip));
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    /** 单个方法的缓存开关 */
    private boolean isUseCache = true;

    public void setIsUseCache(boolean isUseCache) {
        this.isUseCache = isUseCache;
    }

    public boolean isUseCache() {
        return isUseCache;
    }

    public void setUseCache(boolean isUseCache) {
        this.isUseCache = isUseCache;
    }

    public String getStoreRegion() {
        return storeRegion;
    }

    public String getKey() {
        return key;
    }

    public MethodConfig getMethodConfig() {
        return methodConfig;
    }

}
