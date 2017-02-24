package cn.uncode.cache.store.redis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import cn.uncode.util.config.UncodePropertyPlaceholderConfigurer;
import redis.clients.jedis.HostAndPort;

public class JedisClusterFactory implements FactoryBean<JedisClusterCustom>,
		InitializingBean {

	private static Logger log = LoggerFactory.getLogger(JedisClusterFactory.class);
	
	private static final int POOL_MAX_IDLE = 100;
	private static final int POOL_MIN_IDLE = 8;
	private static final int POOL_MAX_WAIT_MILLIS = 1000;
	private static final int POOL_MAX_TOTAL = 600;
	private static final int TIME_OUT = 2000;
	private static final int SO_TIME_OUT = 2000;
	private static final int MAX_REDIRECTIONS = 6;
	
	private Resource addressConfig;
	// redis集群的urls。多个用英文分号分隔。例：192.168.1.13:6379;192.168.1.13:6389
	private String clusterUrls; 
	private String addressKeyPrefix;

	private JedisClusterCustom jedisClusterCustom;
	
	private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

	@Override
	public JedisClusterCustom getObject() throws Exception {
		return jedisClusterCustom;
	}

	@Override
	public Class<? extends JedisClusterCustom> getObjectType() {
		return (this.jedisClusterCustom != null ? this.jedisClusterCustom
				.getClass() : JedisClusterCustom.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<HostAndPort> parseHostAndPort() throws Exception {
		try {
			Set<String> set = null;
			clusterUrls = UncodePropertyPlaceholderConfigurer.getProperty("uncode_session_redis_cluster_address");
			log.info("parseHostAndPort clusterUrls="+clusterUrls);
			if(StringUtils.isBlank(clusterUrls)){
				Properties prop = new Properties();
				prop.load(this.addressConfig.getInputStream());
				set=new HashSet();
				for (Object key : prop.keySet()) {
					if (!((String) key).startsWith(addressKeyPrefix)) {
						continue;
					}
					String val = (String) prop.get(key);
					set.add(val);
				}
			}else{
				set = new HashSet(Arrays.asList(clusterUrls.split(";")));
			}
			Set<HostAndPort> haps = new HashSet<HostAndPort>();
			for (String val : set) {
				boolean isIpPort = p.matcher(val).matches();
				if (!isIpPort) {
					throw new IllegalArgumentException("ip 或 port 不合法");
				}
				String[] ipAndPort = val.split(":");
				HostAndPort hap = new HostAndPort(ipAndPort[0],
						Integer.parseInt(ipAndPort[1]));
				haps.add(hap);
			}
			return haps;
		} catch (IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new Exception("解析 jedis 配置文件失败", ex);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Set<HostAndPort> haps = this.parseHostAndPort();
		GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
		genericObjectPoolConfig.setMaxIdle(UncodePropertyPlaceholderConfigurer.getProperty4Int("uncode_cache_redis_pool_max_idle", POOL_MAX_IDLE));
		genericObjectPoolConfig.setMinIdle(UncodePropertyPlaceholderConfigurer.getProperty4Int("uncode_cache_redis_pool_min_idle", POOL_MIN_IDLE));
		genericObjectPoolConfig.setMaxWaitMillis(UncodePropertyPlaceholderConfigurer.getProperty4Int("uncode_cache_redis_pool_max_wait_millis", POOL_MAX_WAIT_MILLIS));
		genericObjectPoolConfig.setMaxTotal(UncodePropertyPlaceholderConfigurer.getProperty4Int("uncode_cache_redis_pool_max_total", POOL_MAX_TOTAL));
		jedisClusterCustom = new JedisClusterCustom(haps, UncodePropertyPlaceholderConfigurer.getProperty4Int("uncode_cache_redis_cluster_time_out", TIME_OUT),
				UncodePropertyPlaceholderConfigurer.getProperty4Int("uncode_cache_redis_cluster_so_time_out", SO_TIME_OUT),
				UncodePropertyPlaceholderConfigurer.getProperty4Int("uncode_cache_redis_cluster_max_redirections", MAX_REDIRECTIONS),
				UncodePropertyPlaceholderConfigurer.getProperty("uncode_cache_redis_cluster_password"), genericObjectPoolConfig);
	}

	public void setAddressConfig(Resource addressConfig) {
		this.addressConfig = addressConfig;
	}

	public void setAddressKeyPrefix(String addressKeyPrefix) {
		this.addressKeyPrefix = addressKeyPrefix;
	}

	public String getClusterUrls() {
		return clusterUrls;
	}

	public void setClusterUrls(String clusterUrls) {
		this.clusterUrls = clusterUrls;
	}

}
