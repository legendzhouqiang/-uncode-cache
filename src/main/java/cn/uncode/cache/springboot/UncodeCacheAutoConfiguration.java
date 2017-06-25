package cn.uncode.cache.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.uncode.cache.ConfigCacheManager;
import cn.uncode.cache.framework.ICache;
import cn.uncode.cache.framework.aop.handle.CacheManagerHandle;
import cn.uncode.cache.framework.util.PropertiesUtil;
import cn.uncode.cache.store.CacheStore;
import cn.uncode.cache.store.local.CacheTemplate;
import cn.uncode.cache.store.redis.JedisTemplate;
import cn.uncode.cache.store.redis.cluster.JedisClusterCustom;
import cn.uncode.cache.store.redis.cluster.JedisClusterFactory;
import cn.uncode.cache.store.redis.cluster.RedisStore;

/**
 * Created by KevinBlandy on 2017/2/28 14:11
 */
@Configuration
public class UncodeCacheAutoConfiguration {
	
	private final Logger LOGGER = LoggerFactory.getLogger(UncodeCacheAutoConfiguration.class);
	
	@Bean(name = "jedisCluster")
	public JedisClusterCustom jedisClusterCustom(){
		JedisClusterFactory jedisClusterFactory = new JedisClusterFactory();
		PropertiesUtil.loadPorperties("/application.properties");
		try {
			jedisClusterFactory.init();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JedisClusterCustom jedisClusterCustom = null;
		try {
			jedisClusterCustom = (JedisClusterCustom)jedisClusterFactory.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LOGGER.info("=====>UncodeCache inited..");
		return jedisClusterCustom;
	}
	
	@Bean(name = "cache")
	public ICache<Object, Object> cache(JedisClusterCustom jedisClusterCustom){
		boolean useLocal = PropertiesUtil.getProperty4Boolean("uncode.cache.useLocal", false);
		if(useLocal){
        	JedisTemplate jedisTemplate = new JedisTemplate(jedisClusterCustom);
        	CacheTemplate cacheTemplate = new CacheTemplate(jedisTemplate);
        	CacheStore cache = new CacheStore(PropertiesUtil.getProperty("uncode.cache.storeRegion"), cacheTemplate);
			return cache;
		}
		RedisStore redisStore = new RedisStore();
		redisStore.setJedisCluster(jedisClusterCustom);
		LOGGER.info("=====>Redis Cache inited..");
		return redisStore;
	}
	
	
	@Bean(name = "configCacheManager")
	public ConfigCacheManager configCacheManager(ICache<Object, Object> cache){
		ConfigCacheManager configCacheManager = new ConfigCacheManager();
		configCacheManager.setScanPackage(PropertiesUtil.getProperty("uncode.cache.scanPackage"));
		configCacheManager.setCache(cache);
		configCacheManager.setStoreRegion(PropertiesUtil.getProperty("uncode.cache.storeRegion"));
		configCacheManager.init();
		LOGGER.info("=====>CacheManager inited..");
		return configCacheManager;
	}
	
	@Bean(name = "cacheManagerHandle")
	public CacheManagerHandle cacheManagerHandle(ConfigCacheManager configCacheManager){
		CacheManagerHandle cacheManagerHandle = new CacheManagerHandle();
		cacheManagerHandle.setCacheManager(configCacheManager);
		LOGGER.info("=====>CacheManagerHandle inited..");
		return cacheManagerHandle;
	}
	
	
}
