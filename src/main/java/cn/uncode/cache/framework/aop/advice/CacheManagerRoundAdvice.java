package cn.uncode.cache.framework.aop.advice;



import static cn.uncode.cache.framework.listener.CacheOprator.GET;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.uncode.cache.CacheManager;
import cn.uncode.cache.annotation.Cache;
import cn.uncode.cache.annotation.CacheClean;
import cn.uncode.cache.framework.CacheProxy;
import cn.uncode.cache.framework.config.CacheConfig;
import cn.uncode.cache.framework.config.MethodConfig;
import cn.uncode.cache.framework.listener.CacheOprateInfo;
import cn.uncode.cache.framework.util.CacheCodeUtil;



/**
 *  通知处理类
 *  
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月28日
 */
public class CacheManagerRoundAdvice implements MethodInterceptor, Advice {

    private static final Logger LOG = LoggerFactory.getLogger(CacheManagerRoundAdvice.class);

    private CacheManager<?> cacheManager;
    private String beanName;

    public CacheManagerRoundAdvice(CacheManager<?> cacheManager, String beanName) {
        this.cacheManager = cacheManager;
        this.beanName = beanName;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {

        MethodConfig cacheMethod = null;
        String storeRegion = "";

        Method method = invocation.getMethod();
        method.getAnnotation(Cache.class);
        String methodName = method.getName();
        CacheConfig cacheConfig = null;

        try {
            cacheConfig = cacheManager.getCacheConfig();
            Class<?>[] parameterTypes = method.getParameterTypes();
            cacheMethod = cacheConfig.getMethodConfig(beanName, methodName, parameterTypes);
        } catch (Exception e) {
            LOG.error("CacheManager:切面解析配置出错:" + beanName + "#" + invocation.getMethod().getName(), e);
            return invocation.proceed();
        }

        String fromHsfIp = "";// hsf consumer ip
        try {
            fromHsfIp = (String) invocation.getThis().getClass().getMethod("getCustomIp").invoke(invocation.getThis());
        } catch (NoSuchMethodException e) {
            LOG.debug("接口没有实现HSF的getCustomIp方法，取不到Consumer IP, beanName=" + beanName);
        }

        try {
        	
            // 1. cache
            if (cacheManager.isUseCache() && cacheMethod != null) {
                String adapterKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, cacheMethod);
                CacheProxy<Object, Object> cacheAdapter = cacheManager.getCacheProxy(adapterKey);

                String cacheCode = CacheCodeUtil.getCacheCode(storeRegion, cacheMethod, invocation.getArguments());

                return useCache(cacheAdapter, cacheCode, cacheMethod.getExpiredTime(), invocation, fromHsfIp);
            }

            // 2. do nothing
            return invocation.proceed();
        } catch (Exception e) {
            // LOG.error("CacheManager:出错:" + beanName + "#"
            // + invocation.getMethod().getName(), e);
            throw e;
        } finally {
            // 3. cache clean
            if (null != cacheMethod && null != cacheMethod.getCacheCleans() && cacheMethod.getCacheCleans().size() > 0) {
                cleanCache(cacheConfig, cacheMethod.getCacheCleans(), fromHsfIp);
            }
        }
    }

    /**
     * 缓存处理
     * 
     * @param cacheAdapter
     * @param cacheCode
     * @param expireTime
     * @param invocation
     * @return
     * @throws Throwable
     */
    private Object useCache(CacheProxy<Object, Object> cacheAdapter,
            String cacheCode, Integer expireTime, MethodInvocation invocation, String ip) throws Throwable {
        if (cacheAdapter == null)
            return invocation.proceed();

        long start = System.currentTimeMillis();
        Object response = cacheAdapter.get(cacheCode, ip);

        if (response == null) {
            response = invocation.proceed();

            long end = System.currentTimeMillis();
            // 缓存未命中，走原生方法，通知listener
            cacheAdapter.notifyListeners(GET, new CacheOprateInfo(cacheCode, end - start, false, cacheAdapter.getMethodConfig(), null, ip));

            if (response == null)// 如果原生方法结果为null，不put到缓存了
                return response;

            if (expireTime == null) {
                cacheAdapter.put(cacheCode, (Serializable) response, ip);
            } else {
                cacheAdapter.put(cacheCode, (Serializable) response, expireTime, ip);
            }
        }

        return response;
    }

    /**
     * 清除缓存处理
     * 
     * @param cacheCleanBean
     * @param invocation
     * @param storeRegion
     * @return
     * @throws Throwable
     */
    private void cleanCache(CacheConfig cacheConfig, List<CacheClean> cacheCleans, String ip) throws Throwable {
        if (cacheConfig == null || cacheCleans.isEmpty())
            return;
        for (CacheClean cacheClean : cacheCleans) {
            Set<String> keys = cacheConfig.getOverLoadingKeys(cacheClean.beanName(), cacheClean.methodName());
            for(String key : keys){
                CacheProxy<Object, Object> cacheAdapter = cacheManager.getCacheProxy(key);
                if (cacheAdapter != null) {
                    cacheAdapter.removeAll(ip);
                }
            }
        }
    }

}
