package com.alesto.kafkahazel.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alesto.kafkahazel.domain.Record;
import com.alesto.kafkahazel.domain.RecordMetadata;
import com.alesto.kafkahazel.domain.User;
import com.alesto.kafkahazel.serder.RecordJSONSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hazelcast.core.HazelcastInstance;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@RestController
public class KafkaController {

    private static final Logger logger =
            LoggerFactory.getLogger(KafkaController.class);

    private final KafkaTemplate<String, Object> template;
    private final String topicName;


    public KafkaController(
            final KafkaTemplate<String, Object> template,
            @Value("${kafkahazel.topic-name}") final String topicName) {
        this.template = template;
        this.topicName = topicName;
    }

    
    
    @GetMapping("/test")
    public String hello() throws Exception {
        
        RecordMetadata meta= new RecordMetadata();
		meta.setDateReceived(new Date());
		meta.setUid("1");
		meta.setVersion("1.0");
		
		User testUser = new User();
		testUser.setName("Andrew Price");
		testUser.setCompany("Bank Inc");
		testUser.setAddress("123 Road Str");
		testUser.setCompanyId("B1");
		testUser.setEmail("andrew@bank.inc");
		testUser.setEmployeeId("101");
		
		Record testRecord = new Record(meta, testUser);
        
		this.template.send(topicName, meta.getUid(), testRecord);
        
        return "Test record added with id " + meta.getUid();
        
    }
    
    @KafkaListener(topics = "users-topic", clientIdPrefix = "record",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAsObject(ConsumerRecord<String, Record> cr,
                               @Payload Record record) {
        
    	logger.info("Received key {" + cr.key() + "}: Payload: {" + record.getPayload() + "} | Record: {" + cr.toString() + "}");
    	
    	//Convert into JSON
    	String json;
    	try {
			json = RecordJSONSerializer.convertToJSON(record.getPayload());
		
			//Extract the Id
	    	String employeeId = record.getMetadata().getUid();
	    	
	    	//Write to Hazelcast
	    	hazelcastController.write(employeeId, json);
	    	
    	} catch (JsonProcessingException e) {
    		logger.error("Error parsing to JSON " + record.getMetadata().getUid());
    		e.printStackTrace();
		}
    	
    	
    }

    @Autowired
    private HazelcastController hazelcastController;
}
