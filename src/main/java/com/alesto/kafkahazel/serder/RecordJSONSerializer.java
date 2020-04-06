package com.alesto.kafkahazel.serder;

import com.alesto.kafkahazel.domain.RecordPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class RecordJSONSerializer {

	private static ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	
	public static String convertToJSON(RecordPayload recordPayload) throws JsonProcessingException
	{
		return objectMapper.writeValueAsString(recordPayload);
	}
}
