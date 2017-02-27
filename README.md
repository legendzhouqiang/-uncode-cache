# Part I. 框架设计目标

1. 可以设置缓存定时自动更新时间
2. 缓存预热
3. 设置缓存消除的各种依赖关系
4. 命中率、读/写耗时等监控（部分实现）
5. 注解支持


------------------------------------------------------------------------

# Part II. 用户指向

## spring配置

	<bean id="propertyConfigurer" class="cn.uncode.util.config.UncodePropertyPlaceholderConfigurer"> 
		<property name="ignoreResourceNotFound" value="true" /> 
		<property name="locations"> 
			<list> 
		   		<value>classpath:config.properties</value> 
		   	</list> 
		</property>
	</bean> 

	<!-- 设置代理 -->
	<aop:aspectj-autoproxy proxy-target-class="true"/>
	
	<!-- 配置缓存池 -->
	<bean id="jedisCluster" class="com.ksudi.proxycache.store.redis.JedisClusterFactory" />
	<bean id="cache" class="com.ksudi.proxycache.store.redis.RedisStore">
		<property name="jedisCluster" ref="jedisCluster" />
	</bean>
	
	<!-- 配置缓存代理管理器 -->
	<bean id="proxyCacheManager" class="com.ksudi.proxycache.ConfigCacheManager"
		init-method="init">
		<!-- 注解的扫描路径 -->
		<property name="scanPackage" value="com.ksudi.star.service"></property>
		<property name="cache" ref="cache" />
	</bean>

	<!-- 配置aop缓存处理器 -->
	<bean class="com.ksudi.proxycache.framework.aop.handle.CacheManagerHandle">
		<property name="cacheManager" ref="proxyCacheManager" />
	</bean>


## 注解

@Cache(expiredTime=60,cacheCleans={@CacheClean(beanName="couriersService6",methodName="updateCourierPwd")})
public Map<?,?> getCourierInfoById(Long courierid) throws Exception {	
	return courierDao.getCourierInfoById(courierid);
}

## 使用场景

1. 主要使用在service层。
2. 缓存工具类。

------------------------------------------------------------------------