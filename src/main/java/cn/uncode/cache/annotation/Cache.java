package cn.uncode.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年5月12日
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache{
    
    /**
     * 判断是否需要预加载
     * 
     * @return
     */
    boolean preload() default false;
    
    String[] preloadParameters() default {};
    
    int expiredTime() default 0;
    
    String cleanTimeExpressions() default "";
    
    CacheClean[] cacheCleans() default {};

}
