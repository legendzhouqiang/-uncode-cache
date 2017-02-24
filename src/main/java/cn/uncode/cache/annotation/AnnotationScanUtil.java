package cn.uncode.cache.annotation;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * 
 * @author juny.ye
 * @email  juny.ye@ksudi.com
 *
 * 2015年5月12日
 */
public class AnnotationScanUtil {
    
    


    /**
     * 扫描指定包下所有包含指定注解的类
     *
     * @param packageNames
     *            上下文环境context
     * @param annotation
     *            注解类
     * @return 包含注解的类的集合
     */

    public static Set<Class<?>> scanByAnnotation(List<String> packageNames, Class<? extends Annotation> annotation) {
        Reflections reflections = getReflections(packageNames);
        return reflections.getTypesAnnotatedWith(annotation);
    }



    /**
     * 
     * 获取Reflections
     *
     * @param packageNames
     *            包名
     * @return Reflections
     */

    private static Reflections getReflections(List<String> packageNames) {

        ConfigurationBuilder config = new ConfigurationBuilder();
        for (String packageName : packageNames) {
            config.addUrls(ClasspathHelper.forPackage(packageName));
        }
        // 添加扫描器
        config.setScanners(new MethodAnnotationsScanner(), new ResourcesScanner(), new TypeAnnotationsScanner(),
                new FieldAnnotationsScanner(), new MethodParameterScanner(), new SubTypesScanner(),
                new TypeElementsScanner());
        return new Reflections(config);

    }

    /**
     * 扫描指定包下指定注解的方法
     *
     * @param packageNames
     *            包名
     * @param annotation
     *            注解
     * @return 方法列表
     */

    public static Set<Method> scanMethodByAnnotation(List<String> packageNames, Class<? extends Annotation> annotation) {
        Reflections reflections = getReflections(packageNames);
        return reflections.getMethodsAnnotatedWith(annotation);

    }

}
