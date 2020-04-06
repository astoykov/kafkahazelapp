package com.alesto.kafkahazel.domain;

import com.alesto.kafkahazel.serder.CustomDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CustomDeserializer.class)
public class RecordPayload {
	
	
}
