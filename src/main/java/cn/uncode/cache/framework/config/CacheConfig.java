package cn.uncode.cache.framework.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.uncode.cache.framework.util.CacheCodeUtil;


/**
 * 缓存总配置
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月28日
 */
public class CacheConfig implements Serializable{

    //
    private static final long serialVersionUID = 8164876688008497503L;
    
    
    private Map<String, MethodConfig> methodConfigs = new HashMap<String, MethodConfig>(); 
    private Set<String> beans = new HashSet<String>();
    private Map<String, Set<String>> overLoadingMethods = new ConcurrentHashMap<String, Set<String>>();

    /**
     * Map自动清理表达式（可选）(just for map）
     * 
     * @see StoreType.MAP
     */
    private String storeMapCleanTime;

    /**
     * 缓存分区（可选）
     */
    private String storeRegion;
    

    public String getStoreMapCleanTime() {
        return storeMapCleanTime;
    }

    public void setStoreMapCleanTime(String storeMapCleanTime) {
        this.storeMapCleanTime = storeMapCleanTime;
    }

    public String getStoreRegion() {
        return storeRegion;
    }

    public void setStoreRegion(String storeRegion) {
        this.storeRegion = storeRegion;
    }
    
    public Map<String, MethodConfig> getMethodConfigs() {
        return methodConfigs;
    }

    public Set<String> getBeans() {
        return beans;
    }
    
    public void addMethodConfig(MethodConfig methodConfig){
        String key = CacheCodeUtil.getCacheAdapterKey(storeRegion, methodConfig);
        methodConfigs.put(key, methodConfig);
        beans.add(methodConfig.getBeanName());
        String overLoadingkey = CacheCodeUtil.getCacheAdapterKey(storeRegion, methodConfig.getBeanName(), methodConfig.getMethodName(), null);
        if(overLoadingMethods.containsKey(overLoadingkey)){
            overLoadingMethods.get(overLoadingkey).add(key);
        }else{
            Set<String> keys = new HashSet<String>();
            keys.add(key);
            overLoadingMethods.put(overLoadingkey, keys);
        }
    }
    
    public Set<String> getOverLoadingKeys(String beanName, String methodName){
        String overLoadingkey = CacheCodeUtil.getCacheAdapterKey(storeRegion, beanName, methodName, null);
        return overLoadingMethods.get(overLoadingkey);
    }
    
    public MethodConfig getMethodConfig(String beanName, String methodName, Class<?>[] parameterTypes){
        String key = CacheCodeUtil.getCacheAdapterKey(storeRegion, beanName, methodName, parameterTypes);
        if(methodConfigs.containsKey(key)){
            return methodConfigs.get(key);
        }
        key = CacheCodeUtil.getCacheAdapterKey(storeRegion, beanName, methodName, null);
        if(methodConfigs.containsKey(key)){
            return methodConfigs.get(key);
        }
        return null;
    }
    
    public String toString(){
        return "{\"methodConfig\":" + methodConfigs.toString() + "}";
    }


}
