package com.alesto.kafkahazel.service;

import com.alesto.kafkahazel.database.MyMapListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.IMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by JavaDeveloperZone on 19-07-2017.
 */
@RestController
public class HazelcastController {

    @Autowired
    private HazelcastInstance instance;  // autowire hazel cast instance
    
    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/write")
   public String write(@RequestParam("id")String id, @RequestParam("json")String json) {
    	
    	HazelcastJsonValue value = new HazelcastJsonValue(json);
    	
    	IMap<Integer,HazelcastJsonValue> stringStringMap = instance.getMap("users");    // get map from hazelcast
        
    	stringStringMap.addEntryListener(this.applicationContext.getBean(MyMapListener.class), true);
    	
    	stringStringMap.put(Integer.parseInt(id),value);
        
    	return "Value has been write to Hazelcast";
    }
    
    @RequestMapping("/read")
    public String read(@RequestParam("id")String id) {
        
    	java.util.Map<Integer,HazelcastJsonValue> stringStringMap = instance.getMap("users");    // get map from hazel cast
        
    	
        return "Hazelcast values is :" +stringStringMap.get(Integer.parseInt(id));               // read value
    }
}