package com.alesto.kafkahazel;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.alesto.kafkahazel.domain.Record;
import com.alesto.kafkahazel.domain.RecordMetadata;
import com.alesto.kafkahazel.domain.User;
import com.alesto.kafkahazel.serder.RecordJSONSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class SerializerTest {

	private Record testRecord;
	
	private String testXml = "<Record><metadata><version>1.0</version><uid>1</uid><dateReceivedText>2020-04-06.15.43.22</dateReceivedText></metadata><payload><employeeId>101</employeeId><name>Andrew Price</name><companyId>B1</companyId><email>andrew@bank.inc</email><address>123 Road Str</address><company>Bank Inc</company><id>101</id></payload></Record>";
	
	@Before
	public void prepareUser()
	{
		
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
		
		testRecord = new Record(meta, testUser);
	}

	@Test
	public void whenSerializedToXmlStr_thenCorrect() throws JsonProcessingException {
	    XmlMapper xmlMapper = new XmlMapper();
	    String xml = xmlMapper.writeValueAsString(testRecord);
	    System.out.println(xml);
	    assertNotNull(xml);
	}
	
	@Test
	public void whenSerializedToJsonStr_thenCorrect() throws JsonProcessingException {
		String xml = RecordJSONSerializer.convertToJSON(testRecord.getPayload());
		
	    System.out.println(xml);
	    assertNotNull(xml);
	}
	
	@Test
	public void whenDeserializeFromXML_thenCorrect() throws IOException {
	    XmlMapper xmlMapper = (XmlMapper) new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    Record record
	      = xmlMapper.readValue(testXml, Record.class);
	    assertEquals("1", record.getMetadata().getUid());
	    assertEquals("101", ((User)record.getPayload()).getId());
	}
}
