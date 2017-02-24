package cn.uncode.cache.framework.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.uncode.cache.framework.CacheProxy;
import cn.uncode.cache.framework.config.CacheConfig;
import cn.uncode.cache.framework.config.MethodConfig;
import cn.uncode.cache.framework.util.CacheCodeUtil;


/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月24日
 */
public class CleanCacheTimerManager {

    private static final Logger LOG = LoggerFactory.getLogger(CleanCacheTimerManager.class);
    
    private static final long INTERVAL_TIME = 5000;
    
    private Timer timer;

    public CleanCacheTimerManager() {
        timer = new Timer("CleanCacheTimerManager", false);// 守护进程
    }

    public void createCleanCacheTask(final CacheConfig cacheConfig, 
            final Map<String, CacheProxy<Object, Object>> cacheProxys) throws Exception {
        Map<String, List<CacheProxy<Object, Object>>> crontabMap = new HashMap<String, List<CacheProxy<Object, Object>>>();
        for(MethodConfig methodConfig:cacheConfig.getMethodConfigs().values()){
            if(StringUtils.isNotEmpty(methodConfig.getCleanTimeExpressions())){
                if(crontabMap.containsKey(methodConfig.getCleanTimeExpressions())){
                    String key = CacheCodeUtil.getCacheAdapterKey(cacheConfig.getStoreRegion(), methodConfig);
                    crontabMap.get(methodConfig.getCleanTimeExpressions()).add(cacheProxys.get(key));
                }else{
                    List<CacheProxy<Object, Object>> list = new ArrayList<CacheProxy<Object, Object>>();
                    String key = CacheCodeUtil.getCacheAdapterKey(cacheConfig.getStoreRegion(), methodConfig);
                    list.add(cacheProxys.get(key));
                    crontabMap.put(key, list);
                }
            }
        }

        this.timer.schedule(new CleanCacheTask(crontabMap), 0, INTERVAL_TIME);
    }

    /**
     * 清理Task
     */
    class CleanCacheTask extends TimerTask {
        
        private Map<String, List<CacheProxy<Object, Object>>> crontabMap;

        public CleanCacheTask(Map<String, List<CacheProxy<Object, Object>>> crontabMap) {
            this.crontabMap = crontabMap;
        }

        @Override
        public void run() {
            try {
                for(String key:crontabMap.keySet()){
                  long newTime = System.currentTimeMillis();
                  CronExpression cexp = new CronExpression(key);
                  Date nextTime = cexp.getNextValidTimeAfter(new Date(newTime));
                  long timeDifference = nextTime.getTime() - newTime;
                  if(timeDifference < INTERVAL_TIME){
                      CacheProxy<Object, Object> cacheProxy = (CacheProxy<Object, Object>) crontabMap.get(key);
                      cacheProxy.removeAll("");
                  }
                }
            } catch (Exception e) {
                LOG.error("清理Map失败", e);
            }
        }

    }
}
