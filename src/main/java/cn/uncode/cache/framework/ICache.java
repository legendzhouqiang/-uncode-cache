/**
 * 
 */
package cn.uncode.cache.framework;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 * 
 * @param <K> key of cache
 * @param <V> value of cache
 * 
 * 2015年4月23日
 */
public interface ICache<K, V> {
	
    public static enum Level {
        Local, Remote
    }

    public static enum Operator {
        SET, GET, DEL, REM, CLS
    }
	
	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key);
	
	public V get(K key, Level level);

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, V value);
	
	public void put(K key, V value, Level level);

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * 
	 * @param key
	 * @param value
	 * @param expireTime
	 *            数据的有效时间（绝对时间），单位毫秒
	 */
	public void put(K key, V value, int expireTime);
	
	public void put(K key, V value, int expireTime, Level level);
	
	/**
     * 若缓存不存在则设置,若存在则返回原缓存值
     */
    public Object putIfAbsent(K key, Object value);
    /**
     * 若缓存不存在则设置缓存与过期时间,若存在则返回原缓存值
     */
    public Object putIfAbsent(K key, Object value, int expireTime);

	/**
	 * 删除key对应的数据
	 * 
	 * @param key
	 */
	public void remove(K key);
	
	public void remove(K key, Level level);
	
	
	/**
	 * 删除当前缓存分区上的所有缓存
	 */
	public void removeAll();
	
	public void removeAll(Level level);
	
	
	/**
     * 获取缓存过期剩余时间.单位为秒
     * 0,永久
     * -1,不存在
     */
    public int ttl(K key);
    
    public int ttl(K key, Level level);
	
	/**
     * 获取所有匹配的key
     * 
     * @param key
     */
    public List<K> keys(String pattern);

	/**
	 * 清除所有的数据
	 */
	public void clear();

	/**
	 * 获取缓存数据量
	 * 
	 * @return
	 */
	public int size();
	
	/**
	 * 判断key是否存在
	 * @param key
	 * @return {boolean}
	 */
	public boolean isExists(Object key);
	
	
	public boolean isExists(Object key, Level level);
	
	
	/**
	 * 获取所有缓存分区名称
	 * @return
	 */
	Set<String> storeRegions();
	
	
	/**
	 * 获取所有缓存分区和分区下的Keys
	 * @return
	 */
	Map<String, Set<String>> storeRegionKeys();

}
