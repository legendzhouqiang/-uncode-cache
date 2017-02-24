package cn.uncode.cache.framework.config;

import java.io.Serializable;
import java.util.List;

import cn.uncode.cache.annotation.CacheClean;
import cn.uncode.cache.framework.config.validate.Verfication;

/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月23日
 */
public class MethodConfig implements Serializable {

    //
    private static final long serialVersionUID = 1L;
    
    @Verfication(name = "Bean名称", notEmpty = true)
    private String beanName;

    @Verfication(name = "方法名称", notEmpty = true)
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    
    private boolean preload;
    
    private Object[] preloadParameters;
    
    private List<CacheClean> cacheCleans;
    
    private String cleanTimeExpressions;

    /**
     * 失效时间，单位：秒。<br>
     * 可以是相对时间，也可以是绝对时间(大于当前时间戳是绝对时间过期)。不传或0都是不过期 <br>
     * 【可选项】
     */
    private Integer expiredTime;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * null: 代表没有set,装载配置时需要重新赋值 <br>
     * 空: 代表无参方法
     * 
     * @return
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Integer getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Integer expiredTime) {
        this.expiredTime = expiredTime;
    }

    public boolean isMe(String method, Class<?>[] types) {
        if (!this.methodName.equals(method))
            return false;

        if (this.parameterTypes == null && types != null)
            return false;

        if (this.parameterTypes != null && types == null)
            return false;

        if (this.parameterTypes != null) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].getSimpleName()
                        .equals(types[i].getSimpleName()))
                    return false;
            }
        }

        return true;
    }
    
    public boolean isPreload() {
        return preload;
    }

    public void setPreload(boolean preload) {
        this.preload = preload;
    }
    
    public Object[] getPreloadParameters() {
        return preloadParameters;
    }

    public void setPreloadParameters(Object[] preloadParameters) {
        this.preloadParameters = preloadParameters;
    }
    
    public List<CacheClean> getCacheCleans() {
        return cacheCleans;
    }

    public void setCacheCleans(List<CacheClean> cacheCleans) {
        this.cacheCleans = cacheCleans;
    }
    
    public String getCleanTimeExpressions() {
        return cleanTimeExpressions;
    }

    public void setCleanTimeExpressions(String cleanTimeExpressions) {
        this.cleanTimeExpressions = cleanTimeExpressions;
    }

    public String toString(){
        return "{\"beanName\":" + beanName + "},{\"methodName\":" + methodName + "},{\"preload\":" + preload + "},{\"expiredTime\":" + expiredTime + "},{\"cleanTimeExpressions\":" + cleanTimeExpressions 
                + "},{\"parameterTypes\":"+parameterTypes.toString()+"}" + "},{\"preloadParameters\":" + preloadParameters.toString() + "},{\"cacheCleans\":" + cacheCleans.toString();
    }

}
