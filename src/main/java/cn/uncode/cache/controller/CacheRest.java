package cn.uncode.cache.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.uncode.cache.framework.ICache;
import cn.uncode.cache.framework.ICache.Level;
import cn.uncode.cache.store.CacheStore;
import cn.uncode.cache.store.redis.CacheData;


@Controller
@RequestMapping("cache")
public class CacheRest {
	
    @Autowired
    private ICache<Object, Object> cache;

    @RequestMapping("/keys/{key}")
    @ResponseBody
    public List<Object> keys(@PathVariable("key") String key) {
    	if("@all@".equals(key)){
    		return cache.keys(null);
    	}
    	return cache.keys(key);
    }

    @RequestMapping(path = "/add/{val}", method = RequestMethod.GET)
    @ResponseBody
    public String add(@PathVariable("val") String val) {
    	if(StringUtils.isNotBlank(val)){
    		String[] vals = val.split("-");
    		if(vals.length > 1){
    			cache.put(vals[0], vals[1]);
    		}
    	}
    	return "ok";
    }
    
    
    @RequestMapping("/key/{key}")
    @ResponseBody
    public Object key(@PathVariable("key") String key) {
    	Map<String, Object> keyRt = new HashMap<String, Object>();
    	keyRt.put("local", cache.get(key, Level.Local));
    	keyRt.put("remote", cache.get(key, Level.Remote));
     	return keyRt;
    }
    
    @RequestMapping("/key/{key}/remote")
    @ResponseBody
    public Object keyRemote(@PathVariable("key") String key) {
    	return cache.get(key, Level.Remote);
    }
    
    @RequestMapping("/key/{key}/local")
    @ResponseBody
    public Object keyLocal(@PathVariable("key") String key) {
    	return cache.get(key, Level.Local);
    }


    @RequestMapping(path = "/remove/{key}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void remove(@PathVariable("key") String key) {
    	if(StringUtils.isNotEmpty(key)){
    		cache.remove(key);
    	}
    }
    
    @RequestMapping(path = "/remove", method = {RequestMethod.DELETE})
    @ResponseBody
    public void removeall() {
    	cache.removeAll();
    }
    
    
    @RequestMapping("/fetch/{key}")
    @ResponseBody
    public List<CacheData> fetch(@PathVariable("key") String key) {
    	if(cache instanceof CacheStore){
    		CacheStore cacheStore = (CacheStore)cache;
    		if("@all@".equals(key)){
    			key = null;
    		}
    		return cacheStore.fetch(key);
    	}else{
    		return null;
    	}
    }


    @RequestMapping("")
    public String index() {
        return "/static/cache/admin.html";
    }
}
