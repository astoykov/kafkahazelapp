package com.alesto.kafkahazel.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;

import java.util.Properties;

/**
 * <p>A factory method to create
 * {@link com.hazelcast.map.MapLoader MapLoader}
 * instances that take the table name as parameter to indicate
 * the data source to load into the map.
 * </p>
 */
@SuppressWarnings("rawtypes")
@Component
public class MyMapLoaderFactory implements MapStoreFactory<Integer, HazelcastJsonValue> {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * <p>Retrieve a Spring <u>prototype</u> map loader {@code @Bean}
     * for the specified map. It's a prototype so we can have one
     * loader bean per map, and pass the table name as a parameter.
     * </p>
     *
     * @param mapName    The table name to use to populate the map
     * @param properties Not used
     */
    @Override
    public MapLoader<Integer, HazelcastJsonValue> newMapStore(String mapName, Properties properties) {
        return this.applicationContext.getBean(MyJsonMapLoader.class, mapName);
    }

}
