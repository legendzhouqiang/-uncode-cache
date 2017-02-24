package cn.uncode.cache.framework.aop.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;

import cn.uncode.cache.CacheManager;
import cn.uncode.cache.framework.aop.advisor.CacheManagerAdvisor;
import cn.uncode.cache.framework.util.ConfigUtil;

/**
 * 缓存管理处理类
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月28日
 */
@SuppressWarnings("serial")
public class CacheManagerHandle extends AbstractAutoProxyCreator {

	private static final Log log = LogFactory.getLog(CacheManagerHandle.class);

	private CacheManager<?> cacheManager;

	public void setCacheManager(CacheManager<?> cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheManagerHandle() {
		this.setProxyTargetClass(true);
		this.setExposeProxy(true);// do call another advised method on itself
	}

	@SuppressWarnings("rawtypes")
	protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass,
			String beanName, TargetSource targetSource) throws BeansException {

		log.debug("CacheManagerHandle in:" + beanName);
		if (ConfigUtil.isBeanHaveCache(cacheManager.getCacheConfig(), beanName)) {
			log.warn("CacheManager start... ProxyBean:" + beanName);
			return new CacheManagerAdvisor[] { new CacheManagerAdvisor(cacheManager, beanName) };
		}

		return DO_NOT_PROXY;
	}

}
