package cn.uncode.cache.framework.listener;

import cn.uncode.cache.framework.CacheException;
import cn.uncode.cache.framework.config.MethodConfig;


/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月23日
 */
public class CacheOprateInfo extends MethodConfig {

    //
    private static final long serialVersionUID = 7100282651039776916L;

    private Object key;
    private long methodTime;
    /** 是否命中（for GET） */
    private boolean isHitting;
    private CacheException cacheException;
    /** 调用来源：ip */
    private String ip;
    
    public CacheOprateInfo(Object key, long methodTime,
            boolean isHitting, MethodConfig methodConfig,
            CacheException exception, String ip) {
        this.key = key;
        this.methodTime = methodTime;
        this.isHitting = isHitting;
        this.cacheException = exception;
        this.ip = ip;
        if (methodConfig != null) {
            this.setBeanName(methodConfig.getBeanName());
            this.setMethodName(methodConfig.getMethodName());
            this.setParameterTypes(methodConfig.getParameterTypes());
        }
    }

    /**
     * 缓存操作是否成功
     * 
     * @return
     */
    public boolean isSuccess() {
        return cacheException == null;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isHitting() {
        return isHitting;
    }

    public long getMethodTime() {
        return methodTime;
    }

    public void setMethodTime(long methodTime) {
        this.methodTime = methodTime;
    }

    public CacheException getCacheException() {
        return cacheException;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


}
