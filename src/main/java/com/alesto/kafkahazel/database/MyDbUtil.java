package com.alesto.kafkahazel.database;

import com.hazelcast.core.HazelcastJsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * <p>Isolate look-up of database into this class, will make
 * easier for testing.
 * </p>
 */
public class MyDbUtil {
	private static final Logger log =
            LoggerFactory.getLogger(MyDbUtil.class);
    /**
     * <p>Scan the "{@code ID}" column on the provided table,
     * assuming this column exists and is an integer. Return
     * all values found.
     * </p>
     * <p>Trap any exceptions, but perhaps better just to let
     * it fail. You decide.
     * </P
     *
     * @param jdbcTemplate Spring {@code @Bean} to simplify access
     * @param tableName Must exist
     * @return Possibly empty
     */
    public static Collection<Integer> findIds(JdbcTemplate jdbcTemplate, String tableName) {
        String sql = "SELECT id FROM " + tableName;
        log.debug(sql);

        try {
            Set<String> ids = new TreeSet<>();

            List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> map : resultSet) {
                map.keySet().stream()
                   .filter(key -> key.equalsIgnoreCase("id"))
                   .map(key -> map.get(key).toString())
                   .forEach(ids::add);
            }

            System.out.println("[{} row{}]" +  resultSet.size());

            // Convert to integer list, allow NumberFormatException
            return ids
            .stream()
            .map(Integer::valueOf)
            .collect(Collectors.toCollection(TreeSet::new));

        } catch (Exception e) {
            log.error(sql, e);
            return Collections.emptyList();
        }
    }

    /**
     * <p>Find the column names and their values for one row in
     * the specified database, and turn it into JSON. One column
     * is one element, no need for nesting.
     * </p>
     *
     * @param jdbcTemplate Spring {@code @Bean} to simplify access
     * @param tableName Must exist
     * @param id Primary key to look for
     * @return Possibly empty
     */
    public static HazelcastJsonValue rowToJson(JdbcTemplate jdbcTemplate, String tableName, Integer id) {
        String sql = "SELECT * FROM " + tableName + " WHERE ID=" + id;
        log.debug(sql);

        try {
            List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(sql);

            if (resultSet == null || resultSet.size() != 1) {
                log.warn("Results {} for key {}",
                        (resultSet == null ? resultSet : resultSet.size()),
                        id);
            } else {
                Map<String, Object> columns = resultSet.get(0);

                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("{ ");

                Iterator<Map.Entry<String, Object>> iterator = columns.entrySet().iterator();
                int j = 0;
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (j > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append("\"" + entry.getKey() + "\" : \"" + entry.getValue() + "\"");
                    j++;
                }

                stringBuilder.append(" }");

                return new HazelcastJsonValue(stringBuilder.toString());
            }

        } catch (Exception e) {
            log.error(sql, e);
        }

        return null;
    }
    
}
