# Part I. 框架设计目标

1. 缓存与业务代码完全隔离，缓存使用配置文件完成
2. 缓存中间层与存储分离，可以任务选择存储组件
3. 可以设置缓存定时自动更新时间
4. 缓存预热
5. 设置缓存消除的各种依赖关系
6. 命中率、读/写耗时等监控（部分实现）
7. 注解支持


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

## ProxyCache配置文件cache.xml

	<?xml version="1.0" encoding="utf-8"?>
	<config>
		<beans>
			<!-- 需要使用缓存的spring bean -->
			<bean>
				<!-- spring bean名称 -->
				<name>couriersService6</name>
				<methods>
					<!-- 需要使用缓存的方法 -->
					<method>
						<!-- 方法名称 -->
						<name>getCourierListById</name>
						<!-- 可选，方法参数类型，定义时必须按顺序定义，为空时自动配置 -->
						<parameterTypes>
							<java-class>java.lang.String</java-class>
							<java-class>java.lang.Long</java-class>
						</parameterTypes>
						<!-- 缓存过期时间，单位秒 -->
						<expiredTime>120</expiredTime>
						<!-- 可选，定时消除缓存时间 -->
						<storeMapCleanTime>0 0 10,14,18 * * ?</storeMapCleanTime>
						<!-- 可选，是否提前热加载 -->
						<preload>true</preload>
						<!-- 可选，preload为true并且方法有参时必配，热加载需要的方法参数 -->
						<preloadParameters>
							<java.lang.String>wj.ye</java.lang.String>
							<java.lang.Long>1L</java.lang.Long>
						</preloadParameters>
						<!-- 可选，定义缓存依赖（该方法执行时需要清除的方法缓存集 -->
						<cacheCleans>
							<cacheClean>
								<!-- spring bean名称 -->
								<beanName>apiAssignService</beanName>
								<!-- 方法名称 -->
								<methodName>findExpressList</methodName>
							</cacheClean>
						</cacheCleans>
					</method>
				</methods>
			</bean>
		</beans>
	</config>

## 注解

@Cache(expiredTime=60,cacheCleans={@CacheClean(beanName="couriersService6",methodName="updateCourierPwd")})
public Map<?,?> getCourierInfoById(Long courierid) throws Exception {	
	return courierDao.getCourierInfoById(courierid);
}

## 使用场景

1. 主要使用在service层
2。  所有查询类操作都应该加缓存，用户和交易模块除外。

------------------------------------------------------------------------