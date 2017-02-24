package cn.uncode.cache.store.redis.lock;


/**
 * 基于redis的分布式锁
 * @author juny.ye
 *
 */
public interface DistributeLock {
	
	public boolean lock();

    public boolean unlock();

}
