package cn.uncode.cache.log;


import cn.uncode.cache.framework.listener.CacheOprateInfo;
import cn.uncode.cache.framework.listener.CacheOprateListener;
import cn.uncode.cache.framework.listener.CacheOprator;
import cn.uncode.cache.framework.util.CacheCodeUtil;
import cn.uncode.cache.log.asynlog.AsynWriter;

/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年5月5日
 */
public class LogListener implements CacheOprateListener {

    private AsynWriter<String> writer = new AsynWriter<String>();

    private String beanName;
    private String methodName;
    private Class<?>[] parameterTypes;

    /** 关键字 */
    private static final String XRAY_KEYWORD = "PAMIRS_CACHE_XRAY";
    private static final String SEPARATOR = ",";

    public LogListener(String beanName, String methodName, Class<?>[] parameterTypes) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public void oprate(CacheOprator oprator, CacheOprateInfo cacheInfo) {
        writer.write(getLog(oprator, cacheInfo.isHitting(),
                cacheInfo.getMethodTime(), cacheInfo.getIp(), cacheInfo.getKey()));
    }

    /**
     * 日志格式
     * 
     * @param type
     * @param isHit
     * @param useTime
     * @param ip
     * @param key
     * @return
     */
    private String getLog(CacheOprator type, boolean isHit, long useTime, String ip, Object key) {
        StringBuilder sb = new StringBuilder();
        sb.append(XRAY_KEYWORD);
        sb.append(SEPARATOR).append(ip);
        sb.append(SEPARATOR).append(beanName);
        sb.append(SEPARATOR).append(methodName);
        sb.append(SEPARATOR).append(CacheCodeUtil.parameterTypesToString(parameterTypes));
        sb.append(SEPARATOR).append(type.name());
        sb.append(SEPARATOR).append(isHit);
        sb.append(SEPARATOR).append(useTime);
        sb.append(SEPARATOR).append(key);

        return sb.toString();
    }

}