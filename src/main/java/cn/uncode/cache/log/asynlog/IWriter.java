package cn.uncode.cache.log.asynlog;


/**
 * 异步Log接口
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年5月5日
 */
public interface IWriter<T> {

    /**
     * 单个写
     * 
     * @param content
     */
    public void write(T content);

}
