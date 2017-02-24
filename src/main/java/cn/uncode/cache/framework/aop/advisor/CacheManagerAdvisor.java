package cn.uncode.cache.framework.aop.advisor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;

import cn.uncode.cache.CacheManager;
import cn.uncode.cache.framework.aop.advice.CacheManagerRoundAdvice;


/**
 * 观察者
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月28日
 */
public class CacheManagerAdvisor implements Advisor {
	
	private CacheManagerRoundAdvice advice;

	public CacheManagerAdvisor(CacheManager<?> cacheManager, String beanName) {
		this.advice = new CacheManagerRoundAdvice(cacheManager, beanName);
	}

	public Advice getAdvice() {
		return advice;
	}

	public boolean isPerInstance() {
		return false;
	}
}
