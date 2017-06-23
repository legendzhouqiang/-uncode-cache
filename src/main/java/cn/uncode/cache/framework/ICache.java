/**
 * 
 */
package cn.uncode.cache.framework;

import java.util.List;

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

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, V value);

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * 
	 * @param key
	 * @param value
	 * @param expireTime
	 *            数据的有效时间（绝对时间），单位毫秒
	 */
	public void put(K key, V value, int expireTime);

	/**
	 * 删除key对应的数据
	 * 
	 * @param key
	 */
	public void remove(K key);
	
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
}
