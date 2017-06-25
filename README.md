uncode-cache
===========

基于redis和ehcache的两级缓存组件，支持spring-boot,使用方便，有管理页面。


## 功能概述
1. 统一注解支持
2. 可以设置缓存定时自动更新时间
3. 可以缓存预热
4. 可以设置缓存依赖消除
5. 可以添加监听，使用实现命中率、读/写耗时等



## spring boot

### 1. application.peroperties
	uncode.cache.redisClusterAddress=127.0.0.1:7000;127.0.0.1:7001;127.0.0.1:7002;127.0.0.1:7003;127.0.0.1:7004;127.0.0.1:7005
	uncode.cache.redisPoolMaxIdle=
	uncode.cache.redisPoolMinIdle =
	uncode.cache.redisPoolMaxTotal=
	uncode.cache.redisPoolMaxWaitMillis=
	uncode.cache.redisClusterTimeout=
	uncode.cache.redisClusterMaxRedirections=
	uncode.cache.redisClusterPassword
	uncode.cache.scanPackage=cn.uncode.cache
	uncode.cache.useLocal=true
	uncode.cache.openCacheLog=false
	uncode.cache.storeRegion=uncode-cache-demo
	
### 2. spring boot启动类
	@SpringBootApplication
	@EnableAspectJAutoProxy
	public class Application {
		public static void main(String[] agrs){
			SpringApplication.run(Application.class,agrs);
		}
	}

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
	



## 核心注解

	@Cache(
		preload = true,//是否需要缓存预热，会在系统启动时自已加载，支持特殊场景
		preloadParameters = {"param1", "param2"},//加载方法参数，目前只支持String
		expiredTime = 60,//缓存有效时间，单位秒
		cleanTimeExpressions = "0 15 10 ? * *",//缓存定时清除时间表达式,如：每天10点15分触发
		cacheCleans={//依赖清除bean定义，当该方法被调用时清除当前缓存，可定义多个
			@CacheClean(
				beanName="couriersService6",//bean名称
				methodName="updateCourierPwd"//方法名称
			)
		}
	)
	public User getUserById(){...}



## 工具类

可以直接使用cn.uncode.cache.CacheUtils工具类。

## 管理页面

![输入图片说明](https://git.oschina.net/uploads/images/2017/0625/163508_6824afd3_277761.png "在这里输入图片标题")

## 关于

作者：冶卫军（ywj_316@qq.com,微信:yeweijun）

技术支持QQ群：47306892

Copyright 2018 www.uncode.cn

## 特别说明

本项目使用了部分开源项目代码，保留了原作者的名称和所有内容，同时向作者致敬。

