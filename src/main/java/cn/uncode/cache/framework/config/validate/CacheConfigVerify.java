package cn.uncode.cache.framework.config.validate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import cn.uncode.cache.framework.config.CacheConfig;
import cn.uncode.cache.framework.config.ConfigException;
import cn.uncode.cache.framework.config.MethodConfig;
import cn.uncode.cache.framework.util.CacheCodeUtil;



/**
 * 缓存配置合法性校验
 * 
 * <pre>
 *    缓存校验内容：
 *      1：缓存关键配置静态校验, @see {@link Verfication}
 *      2：缓存方法是否存在重复配置校验
 *      3：缓存清理方法是否存在重复配置校验
 *      4：缓存清理方法的关联方法是否存在重复配置校验
 *      5：缓存方法配置在Spring中的需要存在，并且合法
 *      6：缓存清理方法在Spring中需要存在，并且合法
 * </pre>
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月29日
 */
public class CacheConfigVerify {

    /**
     * 校验缓存配置
     * 
     * @param applicationContext
     * @param cacheConfig
     * @throws LoadConfigException
     */
    public static void checkCacheConfig(CacheConfig cacheConfig,
            ApplicationContext applicationContext) throws ConfigException {
        Assert.notNull(applicationContext);
        Assert.notNull(cacheConfig);
        Assert.isTrue(CollectionUtils.isEmpty(cacheConfig.getBeans())
                && CollectionUtils.isEmpty(cacheConfig.getMethodConfigs()),
                "配置中缓存和清理缓存不能同时为空！");

        // 1. 静态校验
        try {
            StaticCheck.check(cacheConfig);
            
            if (cacheConfig.getMethodConfigs() != null) {
                for (MethodConfig method : cacheConfig.getMethodConfigs().values())
                    StaticCheck.check(method);
            }
        } catch (Exception e) {
            throw new ConfigException(e.getMessage());
        }

        // 2. 动态Spring校验
        /*if (cacheConfig.getMethodConfigs() != null) {
            for (MethodConfig methodConfig : cacheConfig.getMethodConfigs().values()) {
                doValidSpringMethod(applicationContext,
                        methodConfig.getBeanName(),
                        methodConfig.getMethodName(),
                        methodConfig.getParameterTypes());
            }
        }*/

        // 3. 配置重复校验
        checkRepeatMethod(cacheConfig);
    }

    private static void checkRepeatMethod(CacheConfig cacheConfig) {
        // 3.1 缓存方法是否存在重复配置校验
        List<String> keys = new ArrayList<String>();
        if (cacheConfig.getMethodConfigs() != null) {
            for (MethodConfig methodConfig : cacheConfig.getMethodConfigs().values()) {
                String cacheAdapterKey = CacheCodeUtil.getCacheAdapterKey(cacheConfig.getStoreRegion(), methodConfig);
                if (keys.contains(cacheAdapterKey))
                    throw new ConfigException("缓存配置中方法重复了,Bean:" + methodConfig.getBeanName() + ",method="
                            + methodConfig.getMethodName());

                keys.add(cacheAdapterKey);
            }
        }

        // 3.2 缓存清理方法是否存在重复配置校验
        keys.clear();
    }

}