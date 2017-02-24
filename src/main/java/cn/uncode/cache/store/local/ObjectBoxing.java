package cn.uncode.cache.store.local;

import java.io.Serializable;

/**
 * 缓存对象包装类，支持expireTime
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月28日
 */
@SuppressWarnings("hiding")
public class ObjectBoxing<Object> implements Serializable {
    //
    private static final long serialVersionUID = 2186360043715004471L;

    private Long timestamp = new Long(System.currentTimeMillis() / 1000);

    /**
     * 失效时间（绝对时间），单位毫秒<br>
     * Null表示永不失效
     */
    private Integer expireTime;

    private Object value;

    public ObjectBoxing(Object value) {
        this(value, null);
    }

    public ObjectBoxing(Object value, Integer expireTime) {
        this.value = value;
        this.expireTime = expireTime;
    }

    public Object getObject() {
        // 已经失效
        if (expireTime != null && expireTime != 0) {
            long now = System.currentTimeMillis() / 1000;

            if (timestamp.longValue() > expireTime.longValue()) {// 相对时间
                if (now >= (expireTime.longValue() + timestamp.longValue()))
                    return null;
            } else {// 绝对时间
                if (now >= expireTime.longValue())
                    return null;
            }
        }

        return this.value;
    }
}