package com.alesto.kafkahazel.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordMetadata {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss");
	
	private String dateReceived;
	private String version;
	private String uid;
	
	public String getDateReceivedText() {
		return dateReceived;
	}
	public void setDateReceivedText(String datereceived) {
		this.dateReceived = datereceived;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@JsonProperty
	public void setDateReceived(Date date) {
		this.dateReceived =  sdf.format(date);
	}

	@JsonIgnore
	public Date getDateReceived() throws ParseException {
		return sdf.parse(this.dateReceived);
	}
}
