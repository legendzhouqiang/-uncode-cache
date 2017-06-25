package cn.uncode.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import cn.uncode.cache.framework.CacheProxy;
import cn.uncode.cache.framework.ICache;
import cn.uncode.cache.framework.config.CacheConfig;
import cn.uncode.cache.framework.config.MethodConfig;
import cn.uncode.cache.framework.config.validate.CacheConfigVerify;
import cn.uncode.cache.framework.timer.CleanCacheTimerManager;
import cn.uncode.cache.framework.util.CacheCodeUtil;
import cn.uncode.cache.framework.util.ConfigUtil;
import cn.uncode.cache.log.LogListener;
import cn.uncode.cache.lru.ConcurrentLRUCacheMap;
import cn.uncode.cache.store.local.MapStore;



/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月24日
 */
public abstract class CacheManager<E extends ApplicationEvent> implements ApplicationContextAware,
        ApplicationListener<E> {

    private static final Logger LOG = LoggerFactory.getLogger(CacheManager.class);
    private static final String STORE_REGION_KEY = "uncode_cache_store_region";

    /**
     * 每一个method对应一个adapter实例
     */
    private final Map<String, CacheProxy<Object, Object>> cacheProxys = new ConcurrentHashMap<String, CacheProxy<Object, Object>>();
    
    protected static ApplicationContext applicationContext;

    private CleanCacheTimerManager timeTask = new CleanCacheTimerManager();

    private boolean useCache = true;

    protected static CacheConfig cacheConfig = new CacheConfig();
    
    private ICache<Object, Object> cache;

    /** 打印缓存命中日志 **/
    private boolean openCacheLog = false;
    /**
     * 缓存分区（可选）
     */
    private String storeRegion;

    /**
     * 指定本地缓存时LruMap的大小，默认是1024
     * 
     * @see StoreType.MAP
     * @see ConcurrentLRUCacheMap
     */
    private int localMapSize = ConcurrentLRUCacheMap.DEFAULT_INITIAL_CAPACITY;
    /**
     * 指定本地缓存分段的大小，默认是16
     * 
     * @see StoreType.MAP
     * @see ConcurrentLRUCacheMap
     */
    private int localMapSegmentSize = ConcurrentLRUCacheMap.DEFAULT_CONCURRENCY_LEVEL;

    public void init(){
        // 1. 加载/校验config
        cacheConfig = loadConfig();
        if(StringUtils.isNotBlank(storeRegion) && StringUtils.isBlank(cacheConfig.getStoreRegion())){
        	cacheConfig.setStoreRegion(storeRegion);
        }
        // 后面两个，见onApplicationEvent方法
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 放在onApplicationEvent里，原因是解决CacheManagerHandle里先执行代理，再applicationContext.getBean，否则代理不了

        if (event instanceof ContextRefreshedEvent) {

            // 2. 缓存配置合法性校验
            verifyCacheConfig(cacheConfig);

            // 3. 初始化缓存
            initCache();
        }
    }

    public abstract CacheConfig loadConfig();

    public void autoFillCacheConfig(CacheConfig cacheConfig) {
        ConfigUtil.autoFillCacheConfig(cacheConfig, applicationContext);
    }

    public void verifyCacheConfig(CacheConfig cacheConfig) {
        CacheConfigVerify.checkCacheConfig(cacheConfig, applicationContext);
    }

    /**
     * 初始化缓存
     */
    private void initCache() {
        Map<String, MethodConfig> cacheMethods = cacheConfig.getMethodConfigs();
        if (null != cacheMethods) {
            for (MethodConfig method : cacheMethods.values()) {
                initCacheAdapters(cacheConfig.getStoreRegion(), method, cacheConfig.getStoreMapCleanTime());
            }
        }
    }

    /**
     * 初始化Bean/Method对应的缓存，包括： <br>
     * 1. CacheProxy <br>
     * 2. 定时清理任务：storeMapCleanTime <br>
     * 3. 注册JMX <br>
     * 4. 注册Xray log <br>
     * 
     * @param region
     * @param cacheBean
     * @param storeMapCleanTime
     */
    private void initCacheAdapters(String region, MethodConfig cacheMethod, String storeMapCleanTime) {
        String key = CacheCodeUtil.getCacheAdapterKey(region, cacheMethod);
        if(null == cache){
            cache = new MapStore(localMapSize, localMapSegmentSize);
        }

        if (cache != null) {
            // 1. CacheProxy
            CacheProxy<Object, Object> cacheProxy = new CacheProxy<Object, Object>(
                    region, key, cache, cacheMethod);
            cacheProxys.put(key, cacheProxy);

            // 2. 定时清理任务：storeMapCleanTime
            if (StringUtils.isNotBlank(storeMapCleanTime)) {
                try {
                    timeTask.createCleanCacheTask(cacheConfig, cacheProxys);
                } catch (Exception e) {
                    LOG.error("[严重]设置Map定时清理任务失败!", e);
                }
            }

            // 3. 注册JMX
            //registerCacheMbean(key, cacheProxy, storeMapCleanTime, cacheMethod.getExpiredTime());

            // 4. 注册log
            if (openCacheLog){
                 cacheProxy.addListener(new LogListener(cacheMethod.getBeanName(), cacheMethod.getMethodName(),
                         cacheMethod.getParameterTypes()));
            }
             
            // 5. 预热
            if(cacheMethod.isPreload()){
                Class<?>[] parameterTypes = ConfigUtil.preloadMethodParameterTypes(cacheMethod, applicationContext);
                Assert.notNull(parameterTypes);
                Object bean = applicationContext.getBean(cacheMethod.getBeanName());
                ConfigUtil.invokeBeanMethod(bean, cacheMethod.getBeanName(), parameterTypes, cacheMethod.getPreloadParameters());
            }
            CacheUtils.setCache(cache);
        }
    }
    
    public CacheProxy<Object, Object> getCacheProxy(String key) {
        if (key == null || cacheProxys == null)
            return null;

        return cacheProxys.get(key);
    }
    
    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	CacheManager.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setLocalMapSize(int localMapSize) {
        this.localMapSize = localMapSize;
    }

    public void setLocalMapSegmentSize(int localMapSegmentSize) {
        this.localMapSegmentSize = localMapSegmentSize;
    }

    public void setOpenCacheLog(boolean openCacheLog) {
        this.openCacheLog = openCacheLog;
    }

    public static CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    public void setCache(ICache<Object, Object> cache) {
        this.cache = cache;
    }

	public String getStoreRegion() {
		return storeRegion;
	}

	public void setStoreRegion(String storeRegion) {
		this.storeRegion = storeRegion;
	}

    
    

}
