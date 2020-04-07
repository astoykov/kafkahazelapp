package com.alesto.kafkahazel.database;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.listener.EntryAddedListener;

@Component
@Scope("prototype")
public class MyMapListener implements EntryAddedListener<Integer, HazelcastJsonValue>{

	@Override
	public void entryAdded(EntryEvent<Integer, HazelcastJsonValue> event) {
		System.out.println("===== New:  " + event.getKey() + " : " + event.getValue());
		
	}

	
}
