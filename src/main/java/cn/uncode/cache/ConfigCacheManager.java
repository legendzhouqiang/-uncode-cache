package cn.uncode.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationEvent;

import cn.uncode.cache.annotation.AnnotationScanUtil;
import cn.uncode.cache.annotation.Cache;
import cn.uncode.cache.framework.config.CacheConfig;
import cn.uncode.cache.framework.config.MethodConfig;


/**
 * 本地加载缓存配置服务
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月28日
 */
public class ConfigCacheManager extends CacheManager<ApplicationEvent> implements BeanDefinitionRegistryPostProcessor{

    private String scanPackage;
    
    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }


    /**
     * 加载加载缓存配置
     * 
     * @return
     */
    @Override
    public CacheConfig loadConfig() {
        return cacheConfig;
    }
    

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Map<String, String> methodMap = new HashMap<String, String>();
        List<MethodConfig> methods = new ArrayList<MethodConfig>();
        if(StringUtils.isNotEmpty(scanPackage)){
            List<String> list = Arrays.asList(scanPackage.split(","));
            Set<java.lang.reflect.Method> mds = AnnotationScanUtil.scanMethodByAnnotation(list, Cache.class);
            for(java.lang.reflect.Method md : mds){
                Cache cache = md.getAnnotation(Cache.class);
                MethodConfig method = new MethodConfig();
                method.setMethodName(md.getName());
                method.setParameterTypes(md.getParameterTypes());
                method.setExpiredTime(cache.expiredTime());
                method.setPreload(cache.preload());
                if(StringUtils.isNotEmpty(cache.cleanTimeExpressions())){
                    method.setCleanTimeExpressions(cache.cleanTimeExpressions());
                }
                if(null != cache.cacheCleans()){
                    method.setCacheCleans(Arrays.asList(cache.cacheCleans()));
                }
                methods.add(method);
                if(!methodMap.containsKey(md.getName())){
                    methodMap.put(md.getName(), md.getDeclaringClass().getCanonicalName().trim());
                }
            }
            if(methods.size() > 0){
                Map<String, String> classNames = new HashMap<String, String>();
                String[] names = registry.getBeanDefinitionNames();
                for(String name : names){
                    BeanDefinition beanDefinition = registry.getBeanDefinition(name);
                    if(methodMap.values().contains(beanDefinition.getBeanClassName().trim())){
                        classNames.put(beanDefinition.getBeanClassName(), name.trim());
                    }
                }
                for(MethodConfig mc : methods){
                    if(StringUtils.isEmpty(mc.getBeanName())){
                        if(methodMap.containsKey(mc.getMethodName())){
                            String clazz = methodMap.get(mc.getMethodName());
                            if(StringUtils.isNotEmpty(clazz) && classNames.containsKey(clazz)){
                                mc.setBeanName(classNames.get(clazz));
                            }
                        }
                    }
                    cacheConfig.addMethodConfig(mc);
                }
            }
        }

    }
    
   

    

}
