package com.alesto.kafkahazel.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping("/write")
   public String write(@RequestParam("id")String id, @RequestParam("json")String json) {
        
    	json = "{\"employeeId\":\"101\",\"name\":\"Andrew Price\",\"companyId\":\"B1\",\"email\":\"andrew@bank.inc\",\"address\":\"123 Road Str\",\"company\":\"Bank Inc\",\"id\":\"101\"}";
    	
    	System.out.println("WRITE : " + id + " : " + json);
    	
    	HazelcastJsonValue value = new HazelcastJsonValue(json);
    	
    	java.util.Map<Integer,HazelcastJsonValue> stringStringMap = instance.getMap("users");    // get map from hazelcast
        
    	stringStringMap.put(Integer.parseInt(id),value);
        
    	return "Value has been write to Hazelcast";
    }
    
    @RequestMapping("/read")
    public String read(@RequestParam("id")String id) {
        
    	java.util.Map<Integer,HazelcastJsonValue> stringStringMap = instance.getMap("users");    // get map from hazel cast
        
        return "Hazelcast values is :" +stringStringMap.get(Integer.parseInt(id));               // read value
    }
}