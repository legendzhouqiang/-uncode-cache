package cn.uncode.cache.framework.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ComnObjUtil {
	// 比较两个Long类型的对象是否值相等，两个都为空，返回true， 只有一个为空，返回false
	public static boolean equalLong(Long o1, Long o2){
		if(o1==null && o2==null){
			return true;
		}
		if((o1==null && o2 != null) || (o1  != null && o2 == null)){
			return false;
		}
		long long1= (long)o1;
		long long2= (long)o2;
		return long1== long2;
	}
	// 比较两个Long类型的对象是否值相等，两个都为空，返回true， 只有一个为空，返回false
	public static boolean equalInteger(Integer o1, Integer o2){
		if(o1==null && o2==null){
			return true;
		}
		if((o1==null && o2 != null) || (o1  != null && o2 == null)){
			return false;
		}
		int val1= (int)o1;
		int val2= (int)o2;
		return val1== val2;
	}
	

	public static void p(Object o) {
		System.out.print(o);
	}

	public static void pl(Object o) {
		System.out.println(o);
	}

	public static void pl() {
		System.out.println();
	}
	
	/**
	 * 如果data对象不为null则返回该对象的值，否则返回给定的默认值defaultValue
	 * 
	 * @param data
	 *            判断是否为null的对象
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author Wenlong.Zhou
	 * @date 2016年4月27日上午11:08:19
	 */
	public static byte valueFrom(Byte data, byte defaultValue) {
		if (data == null) {
			return defaultValue;
		}
		return data.byteValue();
	}
	
	/**
	 * 如果data对象不为null则返回该对象的值，否则返回给定的默认值defaultValue
	 * 
	 * @param data
	 *            判断是否为null的对象
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author Wenlong.Zhou
	 * @date 2016年4月27日上午11:08:19
	 */
	public static short valueFrom(Short data, short defaultValue) {
		if (data == null) {
			return defaultValue;
		}
		return data.shortValue();
	}
	
	/**
	 * 如果data对象不为null则返回该对象的值，否则返回给定的默认值defaultValue
	 * 
	 * @param data
	 *            判断是否为null的对象
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author Wenlong.Zhou
	 * @date 2016年4月27日上午11:08:19
	 */
	public static int valueFrom(Integer data, int defaultValue) {
		if (data == null) {
			return defaultValue;
		}
		return data.intValue();
	}
	
	/**
	 * 如果data对象不为null则返回该对象的值，否则返回给定的默认值defaultValue
	 * 
	 * @param data
	 *            判断是否为null的对象
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author Wenlong.Zhou
	 * @date 2016年4月27日上午11:08:19
	 */
	public static long valueFrom(Long data, long defaultValue) {
		if (data == null) {
			return defaultValue;
		}
		return data.longValue();
	}
	
	/**
	 * 如果data对象不为null则返回该对象的值，否则返回给定的默认值defaultValue
	 * 
	 * @param data
	 *            判断是否为null的对象
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author Wenlong.Zhou
	 * @date 2016年4月27日上午11:08:19
	 */
	public static double valueFrom(Double data, double defaultValue) {
		if (data == null) {
			return defaultValue;
		}
		return data.doubleValue();
	}
	
	/**
	 * 如果data对象不为null则返回该对象的值，否则返回给定的默认值defaultValue
	 * 
	 * @param data
	 *            判断是否为null的对象
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author Wenlong.Zhou
	 * @date 2016年4月27日上午11:08:19
	 */
	public static float valueFrom(Float data, float defaultValue) {
		if (data == null) {
			return defaultValue;
		}
		return data.floatValue();
	}
	
	
	/**
	 * 如果data对象不为null则返回该对象的值，否则返回给定的默认值defaultValue
	 * 
	 * @param data
	 *            判断是否为null的对象
	 * @param defaultValue
	 *            默认值
	 * @return
	 * @author Wenlong.Zhou
	 * @date 2016年4月27日上午11:08:19
	 */
	public static String valueFrom(String data, String defaultValue) {
		if (data == null) {
			return defaultValue;
		}
		return data;
	}
	

	
	public static Set<String> arr2SetNoBlank(String[] strs){
		if( strs==null || strs.length==0){
			return null;
		}
		Set<String> set=new HashSet<String>();
		for(String str:strs){
			if(StringUtils.isNotBlank(str)){
				set.add(str);
			}
		}
		
		return set;
	}
	private final static Logger log = LoggerFactory.getLogger(ComnObjUtil.class);
}
