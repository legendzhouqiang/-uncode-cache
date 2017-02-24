package cn.uncode.cache.framework.listener;

/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月23日
 */
public interface CacheOprateListener {

	/**
	 * 
	 * @param oprator
	 * @param cacheInfo
	 */
	void oprate(CacheOprator oprator, CacheOprateInfo cacheInfo);

}
