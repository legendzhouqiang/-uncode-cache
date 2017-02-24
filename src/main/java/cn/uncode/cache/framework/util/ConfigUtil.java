package cn.uncode.cache.framework.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import cn.uncode.cache.framework.config.CacheConfig;
import cn.uncode.cache.framework.config.ConfigException;
import cn.uncode.cache.framework.config.MethodConfig;

/**
 * 配置辅助类
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月28日
 */
public class ConfigUtil {

    
    /**
     * 是否bean在cache中配置
     * 
     * @param cacheConfig
     * @param beanName
     * @return
     */
    public static boolean isBeanHaveCache(CacheConfig cacheConfig, String beanName) {
        if (cacheConfig == null || beanName == null)
            return false;
        return cacheConfig.getBeans().contains(beanName);
    }
    

    /**
     * 获取对应的缓存MethodConfig配置
     * 
     * @param cacheConfig
     * @param beanName
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static MethodConfig getCacheMethod(CacheConfig cacheConfig,
            String beanName, String methodName, Class<?>[] parameterTypes) {
        if (cacheConfig == null || beanName == null || methodName == null)
            return null;
        String key = CacheCodeUtil.getCacheAdapterKey(cacheConfig.getStoreRegion(), beanName, methodName, parameterTypes);
        return cacheConfig.getMethodConfigs().get(key);
    }


    /**
     * 获取对应的缓存清理的MethodConfig配置列表
     * 
     * @param cacheConfig
     * @param beanName
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static List<MethodConfig> getCacheCleanMethods(
            CacheConfig cacheConfig, String beanName, String methodName, Class<?>[] parameterTypes) {

        /*List<CacheBean> cacheCleanBeans = cacheConfig.getCacheBeans();

        for (CacheBean bean : cacheCleanBeans) {
            if (!beanName.equals(bean.getBeanName()))
                continue;

            List<MethodConfig> methods = bean.getCacheMethods();
            for (CacheCleanMethod cacheCleanMethod : methods) {
                if (cacheCleanMethod.isMe(methodName, parameterTypes))
                    return cacheCleanMethod.getCleanMethods();
            }
        }*/

        return null;
    }

    /**
     * xml转换成config
     * 
     * @param inputStream
     * @return
     * @throws Exception
     */
   /* public static Config getConfigFromFile(InputStream inputStream) {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("config", Config.class);
        xStream.alias("bean", Bean.class);
        xStream.alias("method", Method.class);
        xStream.alias("cacheClean", CacheClean.class);

        if (inputStream != null) {
            Config config = (Config) xStream.fromXML(inputStream);
            return config;
        }

        throw new ConfigException("输入的配置信息为Null");
    }*/
    
    /**
     * 自动填充配置信息
     * 
     * @param cacheConfig
     * @param applicationContext
     */
    public static Class<?>[] preloadMethodParameterTypes(MethodConfig cacheMethod, ApplicationContext applicationContext) {
        Assert.notNull(applicationContext);
        Assert.notNull(cacheMethod);
        if(null != cacheMethod.getParameterTypes() && cacheMethod.getParameterTypes().length > 0){
            Assert.isTrue(null != cacheMethod.getPreloadParameters(), "预加载方法参数为空！Bean:"+cacheMethod.getBeanName()+",Method:"+cacheMethod.getMethodName());
            return cacheMethod.getParameterTypes();
        }else{
            int paramsLen = 0;
            if(null != cacheMethod.getPreloadParameters()){
                paramsLen = cacheMethod.getPreloadParameters().length;
            }
            List<java.lang.reflect.Method> methods = parsingMethods(cacheMethod.getBeanName(), applicationContext, cacheMethod.getMethodName());
            int couter = 0;
            for(java.lang.reflect.Method m : methods){
                int mLen = 0;
                if(null != m.getParameterTypes()){
                    mLen = m.getParameterTypes().length;
                }
                if(paramsLen == mLen){
                    //参数顺序必须一致
                    Class<?>[] pt = m.getParameterTypes();
                    Object[] pp = cacheMethod.getPreloadParameters();
                    for(int i=0;i<paramsLen;i++){
                        if(pt[i].equals(pp[i].getClass())){
                            couter++;
                        }
                    }
                    if(paramsLen == couter){
                        return pt;
                    }
                }
            }
            return null;
        }
        
    }
    

    /**
     * 自动填充配置信息
     * 
     * @param cacheConfig
     * @param applicationContext
     */
    public static void autoFillCacheConfig(CacheConfig cacheConfig, ApplicationContext applicationContext) {
        Assert.notNull(applicationContext);
        Assert.notNull(cacheConfig);
        Assert.isTrue(CollectionUtils.isEmpty(cacheConfig.getBeans())
                && CollectionUtils.isEmpty(cacheConfig.getMethodConfigs()),
                "配置中缓存和清理缓存不能同时为空！");

        // 1. 对method定义，如果没有parameterTypes，则自动寻找配对（有重名方法报错）
        // 1.1 包括：cacheBean.methodConfig
        if (cacheConfig.getMethodConfigs() != null) {
            for (MethodConfig methodConfig : cacheConfig.getMethodConfigs().values()) {
                if (methodConfig.getParameterTypes() != null)
                    continue;

                Class<?>[] parameterTypes = fillParameterTypes(methodConfig.getBeanName(), applicationContext,
                        methodConfig.getMethodName());
                methodConfig.setParameterTypes(parameterTypes);
            }
        }
        // 1.2 包括：cacheCleanBean.cacheCleanMethod
        /*if (cacheConfig.getCacheCleanBeans() != null) {
            for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
                for (CacheCleanMethod method : cleanBean.getMethods()) {
                    if (method.getParameterTypes() != null)
                        continue;

                    List<Class<?>> parameterTypes = fillParameterTypes(
                            cleanBean.getBeanName(), applicationContext,
                            method.getMethodName());
                    method.setParameterTypes(parameterTypes);
                }
            }
        }*/

        // 2. 填充缓存清理关联的方法参数：cacheCleanBean.methods.cleanMethods.parameterTypes
        /*if (cacheConfig.getCacheCleanBeans() != null) {
            for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
                for (CacheCleanMethod method : cleanBean.getMethods()) {
                    for (MethodConfig clearMethod : method.getCleanMethods()) {
                        clearMethod.setParameterTypes(method.getParameterTypes());// 继承
                    }
                }
            }
        }*/

    }
    
    /**
     * 调用缓存预加载
     * 
     * @param bean
     * @param methodName
     * @param paramTypes
     * @param params
     * @return
     */
    public static Object invokeBeanMethod(Object bean, String methodName,  Class<?>[] paramTypes, Object[] params){
        Object result = null;
        if(AopUtils.isAopProxy(bean)){
            java.lang.reflect.Method method = ReflectionUtils.findMethod(AopProxyUtils.ultimateTargetClass(bean), methodName, paramTypes);
            try {
                result = AopUtils.invokeJoinpointUsingReflection(getAopProxyTarget(bean), method, params);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }else{
            java.lang.reflect.Method method = ReflectionUtils.findMethod(bean.getClass(), methodName, paramTypes);
            if(null == method){
                method = ReflectionUtils.findMethod(bean.getClass(), methodName, paramTypes);
            }
            result = ReflectionUtils.invokeMethod(method, bean, params);
        }
        return result;
    }
    
    private static List<java.lang.reflect.Method> parsingMethods(String beanName, ApplicationContext applicationContext, String methodName) {
        // fill
        Object bean = applicationContext.getBean(beanName);
        Assert.notNull(bean, "找不到Bean:" + beanName);
        
        List<java.lang.reflect.Method> list = new ArrayList<java.lang.reflect.Method>();
        java.lang.reflect.Method[] methods = getAopProxyTarget(bean).getClass().getMethods();
        if(null != methods){
            for (java.lang.reflect.Method m : methods) {
                if (m.getName().equals(methodName)) {
                    list.add(m);
                }
            }
        }
        return list;
    }
    
    public static Object getAopProxyTarget(Object bean){
        Object current = bean;
        if(AopUtils.isAopProxy(bean)){
            while (current instanceof TargetClassAware) {
                Object nested = null;
                if (current instanceof Advised) {
                    TargetSource targetSource = ((Advised) current).getTargetSource();
                    if (targetSource instanceof SingletonTargetSource) {
                        nested = ((SingletonTargetSource) targetSource).getTarget();
                    }
                }
                current = nested;
            }
        }
        return current;
    }

    private static Class<?>[] fillParameterTypes(String beanName, ApplicationContext applicationContext, String methodName) {
        // fill
        Object bean = applicationContext.getBean(beanName);
        Assert.notNull(bean, "找不到Bean:" + beanName);
        
        java.lang.reflect.Method[] methods = bean.getClass().getMethods();
        int num = 0;
        java.lang.reflect.Method index = null;
        for (java.lang.reflect.Method m : methods) {
            if (m.getName().equals(methodName)) {
                num++;
                index = m;
            }
        }

        if (num > 1)
            throw new ConfigException("有重名方法但没有指定参数:" + beanName + "#" + methodName);

        return index.getParameterTypes();
    }

}
