package cn.uncode.cache.store.redis;

import cn.uncode.cache.store.redis.cluster.JedisClusterCustom;

public abstract class JedisExecutor<T> {

    abstract T doInJedis(JedisClusterCustom jedisClusterCustom);

}
