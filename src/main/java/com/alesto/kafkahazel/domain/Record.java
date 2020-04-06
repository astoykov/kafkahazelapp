package com.alesto.kafkahazel.domain;


public class Record {

	private RecordMetadata metadata;
	private RecordPayload payload;
	
	public Record()
	{
		
	}
	
	public Record(RecordMetadata metadata, RecordPayload payload) {
		this.metadata = metadata;
		this.payload = payload;
	}
	
	public RecordMetadata getMetadata() {
		return metadata;
	}
	public void setMetadata(RecordMetadata metadata) {
		this.metadata = metadata;
	}
	public RecordPayload getPayload() {
		return payload;
	}
	public void setPayload(RecordPayload payload) {
		this.payload = payload;
	}
}
