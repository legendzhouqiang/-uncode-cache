package cn.uncode.cache.store.redis.cluster;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import cn.uncode.cache.framework.util.PropertiesUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class JedisClusterFactory implements FactoryBean<JedisClusterCustom>, InitializingBean {

	private static Logger log = LoggerFactory.getLogger(JedisClusterFactory.class);

	public static final int POOL_MAX_IDLE = 100;
	public static final int POOL_MIN_IDLE = 8;
	public static final int POOL_MAX_WAIT_MILLIS = 1000;
	public static final int POOL_MAX_TOTAL = 600;
	public static final int TIME_OUT = 10000;
	public static final int MAX_REDIRECTIONS = 6;

	private Resource resource;
	// redis集群的urls。多个用英文分号分隔。例：192.168.1.13:6379;192.168.1.13:6389
//	private String clusterUrls;

	private JedisClusterCustom JedisCluster;

	private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

	@Override
	public JedisClusterCustom getObject() throws Exception {
		return JedisCluster;
	}

	@Override
	public Class<? extends JedisCluster> getObjectType() {
		return (this.JedisCluster != null ? this.JedisCluster.getClass() : JedisClusterCustom.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<HostAndPort> parseHostAndPort() throws Exception {
		try {
			Set<String> set = null;
			if (null != resource) {
				PropertiesUtil.loadPorperties(resource);
			}else{
				PropertiesUtil.loadProperties();
			}
			String urls = PropertiesUtil.getProperty("uncode.cache.redisClusterAddress");
			if(StringUtils.isBlank(urls)){
				throw new IllegalArgumentException("解析 jedis ip和prot失败");
			}
			set = new HashSet(Arrays.asList(urls.split(";")));
			Set<HostAndPort> haps = new HashSet<HostAndPort>();
			if (null != set) {
				for (String val : set) {
					boolean isIpPort = p.matcher(val).matches();
					if (!isIpPort) {
						throw new IllegalArgumentException("ip 或 port 不合法");
					}
					String[] ipAndPort = val.split(":");
					HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
					haps.add(hap);
				}
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
		 init();
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setJedisCluster(JedisClusterCustom jedisCluster) {
		JedisCluster = jedisCluster;
	}
	
	public void init() throws Exception {
		Set<HostAndPort> haps = this.parseHostAndPort();
		if (null == haps || haps.size() == 0) {
			throw new Exception("解析 jedis 配置文件失败");
		}
		if(JedisCluster == null){
			GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
			genericObjectPoolConfig.setMaxIdle(
					PropertiesUtil.getProperty4Int("uncode.cache.redisPoolMaxIdle", POOL_MAX_IDLE));
			genericObjectPoolConfig.setMinIdle(
					PropertiesUtil.getProperty4Int("uncode.cache.redisPoolMinIdle", POOL_MIN_IDLE));
			genericObjectPoolConfig.setMaxWaitMillis(PropertiesUtil
					.getProperty4Int("uncode.cache.redisPoolMaxWaitMillis", POOL_MAX_WAIT_MILLIS));
			genericObjectPoolConfig.setMaxTotal(PropertiesUtil
					.getProperty4Int("uncode.cache.redisPoolMaxTotal", POOL_MAX_TOTAL));
			JedisCluster = new JedisClusterCustom(haps,
					PropertiesUtil.getProperty4Int("uncode.cache.redisClusterTimeout", TIME_OUT),
					PropertiesUtil.getProperty4Int("uncode.cache.redisClusterMaxRedirections", MAX_REDIRECTIONS),
					genericObjectPoolConfig);
		}
	}

}
