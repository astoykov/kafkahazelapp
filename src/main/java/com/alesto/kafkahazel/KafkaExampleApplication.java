package com.alesto.kafkahazel;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import com.alesto.kafkahazel.database.MyMapLoaderFactory;
import com.alesto.kafkahazel.domain.Record;
import com.alesto.kafkahazel.serder.RecordXMLDeserializer;
import com.alesto.kafkahazel.serder.RecordXMLSerializer;
import com.hazelcast.config.ClasspathYamlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;

import java.util.HashMap;
import java.util.Map;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
@EnableCaching
public class KafkaExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaExampleApplication.class, args);
    }

    @Autowired
    private KafkaProperties kafkaProperties;

    @Value("${kafkahazel.topic-name}")
    private String topicName;

    
    @Bean
    public Config config(MyMapLoaderFactory myMapLoaderFactory) {
        Config config = new ClasspathYamlConfig("application.yml");

        MapConfig defaultMapConfig = config.getMapConfig("default");

        MapStoreConfig myMapStoreConfig = new MapStoreConfig();
        myMapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        myMapStoreConfig.setFactoryImplementation(myMapLoaderFactory);
        myMapStoreConfig.setWriteCoalescing(false);
        myMapStoreConfig.setWriteBatchSize(1);
        

        defaultMapConfig.setMapStoreConfig(myMapStoreConfig);

        return config;
    }
    
    // Producer configuration
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                RecordXMLSerializer.class);
        
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic adviceTopic() {
        return new NewTopic(topicName, 1, (short) 1);
    }

    // Consumer configuration

    // If you only need one kind of deserialization, you only need to set the
    // Consumer configuration properties. Uncomment this and remove all others below.
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(
                kafkaProperties.buildConsumerProperties()
        );
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                RecordXMLDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "tpd-loggers");

        return props;
    }

    
    //Custom consumer 
    @Bean
    public ConsumerFactory<String, Record> consumerFactory() {
        final RecordXMLDeserializer xmlDeserializer = new RecordXMLDeserializer();
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(), new StringDeserializer(), xmlDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Record> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Record> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    //Database
    @Bean
    public Server server() throws IOException, AclFormatException {
        Server server = new Server();

        String myDataBase = "mydatabase";
        server.setDatabaseName(0, myDataBase);
        server.setDatabasePath(0, "file:" + myDataBase + "db");

        Properties properties = new Properties();
        properties.put("maxdatabases", "1");
        HsqlProperties hsqlProperties  = new HsqlProperties(properties);
        server.setProperties(hsqlProperties);

        server.start();

        return server;
    }

    /**
     * <p>Create an explicit database to stop Spring Boot from trying
     * to soon. Inject the {@link org.hsqldb.Server Server} {@code @Bean}
     * so this is created first.
     * </p>
     *
     * @param server {@code @Bean} created above
     * @param password From {@code application.yml}
     * @param url From {@code application.yml}
     * @param username From {@code application.yml}
     * @return A data-source onto the database above
     */
    @Bean
    public DataSource dataSource(Server server,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username
            ) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}
