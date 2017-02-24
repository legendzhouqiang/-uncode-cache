package cn.uncode.cache.store.redis.lock;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DistributeLockFactory{
	
	public static DistributeLock getLock(String key) {
		DistributeLock lock = new RedisDistributeLock(key);
        return lock;
	}
	
	
	
	 public static void main(String[] args) {
	        int threadNum = 10;
	        Executor executor = Executors.newFixedThreadPool(threadNum);
	        
	        for(int i = 0; i< threadNum; i++) {
	            executor.execute(new Runnable(){
	                @Override
	                public void run() {
	                	//必须全局唯一
	                	String key = "alsdkfe3223lksdja932er";
	                    DistributeLock lock = DistributeLockFactory.getLock(key);
	                    try {
	                        if (lock.lock()) {
	                            // 业务逻辑处理
	                            try {
	                                /*
	                                 * ***注意***
	                                 * thread * sleeptime < 获取锁的超市时间
	                                 * 也就是说业务逻辑决定了锁超时时间的设定
	                                 */
	                            	System.out.println("input");
	                                Thread.sleep(100L);
	                            } catch (InterruptedException e) {
	                                e.printStackTrace();
	                            }

	                        } else {

	                        }
	                    } finally {
	                        lock.unlock();
	                    }
	                }

	            });
	        }
	 }

}
