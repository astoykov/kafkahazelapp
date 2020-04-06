package com.alesto.kafkahazel.serder;


import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import org.springframework.kafka.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.converter.Jackson2JavaTypeMapper;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.alesto.kafkahazel.domain.Record;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple custom {@link org.apache.kafka.common.serialization.Serializer Serializer} for sending
 * Record Java objects to Kafka as XML.
 *
 */
public class RecordXMLSerializer implements Serializer<Record> {


	protected final ObjectMapper objectMapper; // NOSONAR
	protected boolean addTypeInfo = true; // NOSONAR
	protected Jackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper(); // NOSONAR


	public RecordXMLSerializer() {
		this.objectMapper = new XmlMapper();
	}

	

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		
	}

	
	@Override
	@Nullable
	public byte[] serialize(String topic, @Nullable Record data) {
		if (data == null) {
			return null;
		}
		try {
			return this.objectMapper.writeValueAsBytes(data);
		}
		catch (IOException ex) {
			throw new SerializationException("Can't serialize data [" + data + "] for topic [" + topic + "]", ex);
		}
	}

	@Override
	public void close() {
		// No-op
	}

	
	

}