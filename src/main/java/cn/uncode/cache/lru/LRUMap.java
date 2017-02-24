package cn.uncode.cache.lru;

import java.util.LinkedHashMap;

/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年4月24日
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {

    //
    private static final long serialVersionUID = 5063027273691566718L;

    /** LRU max size */
    private int maxSize;

    public LRUMap() {
        this(Integer.MAX_VALUE);
    }

    public LRUMap(int size) {
        super(size + 1, 1f, true);
        this.maxSize = size;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }

}
