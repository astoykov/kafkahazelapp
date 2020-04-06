package com.alesto.kafkahazel.serder;


import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.alesto.kafkahazel.domain.Record;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple custom {@link org.apache.kafka.common.serialization.Deserializer Deserializer} for consuming
 * Record Java objects from Kafka as XML.
 *
 */
public class RecordXMLDeserializer implements Deserializer<Record> {

	  protected final ObjectMapper objectMapper; // NOSONAR
	
	  public RecordXMLDeserializer() {
			this.objectMapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	  }
	  
	  @Override public void close() {
		  //no-op
	  }

	  @Override public void configure(Map<String, ?> arg0, boolean arg1) {

	  }

	  @Override
	  public Record deserialize(String topic, byte[] data) {


	    Record record = null;

	    try {

	    	record = objectMapper.readValue(data, Record.class);

	    } catch (Exception e) {

	      e.printStackTrace();

	    }

	    return record;

	  }
	  
}
