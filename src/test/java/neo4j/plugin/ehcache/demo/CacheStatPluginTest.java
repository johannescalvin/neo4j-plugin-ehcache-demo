package neo4j.plugin.ehcache.demo;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.junit.Neo4jRule;
import static org.junit.Assert.*;

public class CacheStatPluginTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withProcedure(CachePlugin.class);

    @Test
    public void testStat(){
        CacheConfig.statisticsService.getCacheStatistics("serviceCache").clear();
        // This is in a try-block, to make sure we close the driver after the test
        try(Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build()
                .withEncryptionLevel( Config.EncryptionLevel.NONE ).toConfig() );
            Session session = driver.session() ){

            final int REPEAT_COUNT = 1000;
            final int KEY_SIZE = 10;
            for (int i = 0; i < REPEAT_COUNT;  i++){
                for (long key = 0; key < KEY_SIZE; key++){
                    session.run("CALL demo.cache.read("+key+")");
                }
            }

            Record record = session.run("CALL demo.cache.stat")
                    .single();
            for (String key : record.keys()){
                System.out.println(record.get(key));
            }

            assertEquals(REPEAT_COUNT * KEY_SIZE,record.get("gets").asInt());
            assertEquals(REPEAT_COUNT * KEY_SIZE - KEY_SIZE,record.get("hits").asInt());
            assertEquals(KEY_SIZE,record.get("misses").asInt());
            assertEquals(KEY_SIZE,record.get("puts").asInt());

        }
    }

}
