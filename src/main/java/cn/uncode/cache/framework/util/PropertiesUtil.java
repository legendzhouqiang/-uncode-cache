package cn.uncode.cache.framework.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class PropertiesUtil{
	
	public static String FILE_PREFIX= "file:";
	
	public static final String COMMON_CONFIG_FILE_PATH= "file:/datum/data/conf/config.properties";
	
	public static final String SYSTEM_CONFIG_FILE_PATH_KEY = "systemConfigLocation";
	
	private static final Map<String, String> PROPERTIES_MAP = new HashMap<String, String>();
	
	private volatile static boolean systemPropertiesLoaded = false;
	
    public static void loadPorperties(Resource resource){
    	try {
    		if (null != resource) {
    			Properties prop = new Properties();
    			prop.load(resource.getInputStream());
    			for (Object key : prop.keySet()) {
    				PROPERTIES_MAP.put(key.toString(), String.valueOf(prop.get(key)));
    			}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}	
    
    public static void loadPorperties(Properties prop){
    	try {
    		if (null != prop) {
    			for (Object key : prop.keySet()) {
    				PROPERTIES_MAP.put(key.toString(), String.valueOf(prop.get(key)));
    			}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
    
    public static void loadPorperties(String path){
    	Properties prop = new Properties();
    	InputStream in = PropertiesUtil.class.getResourceAsStream(path);
    	try {
    		prop.load(in);
		} catch (Exception e) {
		}
    	for (Object key : prop.keySet()) {
			PROPERTIES_MAP.put(key.toString(), String.valueOf(prop.get(key)));
		}
	}
	
    public static void loadProperties(){
		if(!systemPropertiesLoaded){
			String filePath = System.getProperty(SYSTEM_CONFIG_FILE_PATH_KEY);
			if(StringUtils.isBlank(filePath)){
				filePath = COMMON_CONFIG_FILE_PATH;
			}
			if(StringUtils.isNotBlank(filePath)){
				Properties properties = new Properties();
				InputStream fis = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				try{
					UrlResource urlResource = new UrlResource(filePath);
					fis = urlResource.getInputStream();
					isr = new InputStreamReader(fis);
					br = new BufferedReader(isr);  
					properties.load(br);
					for (Object key : properties.keySet()) {
						PROPERTIES_MAP.put(key.toString(), String.valueOf(properties.get(key)));
					}
					systemPropertiesLoaded = true;
				}catch(Exception e){
					// TODO: handle exception
				} finally{
					try{
						if(fis!=null){
							fis.close();
						}
						if(isr!=null){
							isr.close();
						}
						if(br!=null){
							br.close();
						}
					}catch(Exception e){
					}
				}
			}
		}
	}
 
    public static String getProperty(String name) {
    	loadProperties();
    	String newName = name.replace("${", "");
    	newName = newName.replace("}", "");
        return PROPERTIES_MAP.get(newName.trim());
    }
    
    public static String getProperty(String name, String defaultValue) {
    	String newName = getProperty(name);
    	if(StringUtils.isNotBlank(newName)){
    		return newName;
    	}
        return defaultValue;
    }
    
    public static int getProperty4Int(String name, int defalutValue){
    	String val = getProperty(name);
    	if(StringUtils.isNotBlank(val)){
    		return Integer.parseInt(val);
    	}
    	return defalutValue;
    }
    
    public static boolean getProperty4Boolean(String name, boolean defalutValue){
    	String val = getProperty(name);
    	if(StringUtils.isNotBlank(val)){
    		return Boolean.parseBoolean(val);
    	}
    	return defalutValue;
    }
    
    public static long getProperty4Long(String name, long defalutValue){
    	String val = getProperty(name);
    	if(StringUtils.isNotBlank(val)){
    		return Long.parseLong(val);
    	}
    	return defalutValue;
    }
    
    

}
