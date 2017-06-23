package cn.uncode.cache.store.redis;

import java.io.Serializable;

import cn.uncode.cache.framework.ICache.Level;
import cn.uncode.cache.store.local.CacheTemplate;


/**
 * CacheData
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 
 * @author YRain
 */
public class CacheData implements Serializable {

    private static final long serialVersionUID = 7275208849829911463L;

    /**
     * ID
     */
    private String            id;
    /**
     * 所属服务
     */
    private String            host;

    /**
     * 缓存名
     */
    private String            name;
    /**
     * 缓存名
     */
    private String            key;

    /**
     * 缓存值
     */
    private Object            value;

    /**
     * 缓存tti
     */
    private int               tti;

    /**
     * 缓存ttl
     */
    private int               ttl;

    /**
     * 缓存层级
     */
    private Level             level;

    public CacheData() {
    }

    public CacheData(String name, String key, Object value, int tti, int ttl, Level level) {
        super();
        this.id = CacheTemplate.ID;
        this.host = CacheTemplate.HOST;
        this.name = name;
        this.key = key;
        this.value = value;
        this.tti = tti;
        this.ttl = ttl;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getTti() {
        return tti;
    }

    public void setTti(int tti) {
        this.tti = tti;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "CacheData [id=" + id + ", host=" + host + ", name=" + name + ", key=" + key + ", value=" + value + ", tti=" + tti + ", ttl=" + ttl + ", level=" + level + "]";
    }

}