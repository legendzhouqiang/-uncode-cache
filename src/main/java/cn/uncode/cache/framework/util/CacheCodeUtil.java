package cn.uncode.cache.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import cn.uncode.cache.framework.config.MethodConfig;

/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月27日
 */
public class CacheCodeUtil {

    /**
     * Key的主分隔符<br>
     * 格式：regionbeanName#methodName#{String}
     */
    public static final String KEY_SPLITE_SIGN = "#";
    /**
     * key中方法参数的分隔符<br>
     * 格式：{String|Long}
     */
    public static final String KEY_PARAMS_SPLITE_SIGN = "|";

    /** region分隔符 */
    public static final String REGION_SPLITE_SIGN = "@";

    /**
     * 取得最终的缓存Code中参数值分隔符<br>
     * 格式：regionbeanName#methodName#{String,Long}abc@@123
     */
    public static final String CODE_PARAM_VALUES_SPLITE_SIGN = "@@";

    /**
     * 取得最终的缓存Code<br>
     * 格式：region@beanName#methodName#{String|Long}abc@@123
     * 
     * @param region
     * @param beanName
     * @param methodConfig
     * @param parameters
     *            数组长度会以methodConfig.getParameterTypes()优先，多余的会丢失
     * @return
     */
    public static String getCacheCode(String region, MethodConfig methodConfig, Object[] parameters) {
        // 最终的缓存code
        StringBuilder code = new StringBuilder();

        // 1. region
        // 2. bean + method + parameter
        code.append(getCacheAdapterKey(region, methodConfig));

        // 3. value
        if (parameters != null) {
            StringBuilder valus = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                if (valus.length() != 0) {
                    valus.append(CODE_PARAM_VALUES_SPLITE_SIGN);
                }

                valus.append(parameters[i] == null ? "null" : parameters[i].toString());
            }
            code.append(valus.toString());
        }

        return code.toString();
    }

    /**
     * 缓存适配器的key<br>
     * 格式：region@beanName#methodName#{String|Long}
     * 
     * @param region
     * @param beanName
     * @param methodConfig
     * @return
     */
    public static String getCacheAdapterKey(String region, MethodConfig methodConfig) {
        Assert.notNull(methodConfig);
        return getCacheAdapterKey(region, methodConfig.getBeanName(), methodConfig.getMethodName(), methodConfig.getParameterTypes());

    }
    
    /**
     * 缓存适配器的key<br>
     * 格式：region@beanName#methodName#{String|Long}
     * 
     * @param region
     * @param beanName
     * @param methodConfig
     * @return
     */
    public static String getCacheAdapterKey(String region, String beanName, String methodName, Class<?>[] parameterTypes) {
        Assert.notNull(beanName);
        Assert.notNull(methodName);

        // 最终的key
        StringBuilder key = new StringBuilder();

        // 1. region
        if (StringUtils.isNotBlank(region))
            key.append(region).append(REGION_SPLITE_SIGN);

        // 2. bean + method + parameter
        key.append(beanName).append(KEY_SPLITE_SIGN);
        key.append(methodName).append(KEY_SPLITE_SIGN);
        if(null != parameterTypes && parameterTypes.length > 0){
            key.append(parameterTypesToString(parameterTypes));
        }

        return key.toString();

    }
    
    public static String getCacheAdapterKey(String region, Object key) {
        Assert.notNull(key);
        // 最终的key
        StringBuilder kkey = new StringBuilder();

        // 1. region
        if (StringUtils.isNotBlank(region))
        	kkey.append(region).append(REGION_SPLITE_SIGN);

        // 2. key
        kkey.append(String.valueOf(key));

        return kkey.toString();
    }

    /**
     * 参数toString，格式{String|int}
     * 
     * @param parameterTypes
     * @return
     */
    public static String parameterTypesToString(Class<?>[] parameterTypes) {
        StringBuilder parameter = new StringBuilder("{");
        if (parameterTypes != null) {
            for (Class<?> clazz : parameterTypes) {
                if (parameter.length() != 1) {
                    parameter.append(KEY_PARAMS_SPLITE_SIGN);
                }

                parameter.append(clazz.getSimpleName());
            }
        }
        parameter.append("}");
        return parameter.toString();
    }

}
