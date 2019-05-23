package neo4j.plugin.ehcache.demo;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.junit.Neo4jRule;
import static org.junit.Assert.*;
public class CachePluginTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withProcedure(CachePlugin.class);

    @Test
    public void test(){
        // This is in a try-block, to make sure we close the driver after the test
        try(Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build()
                .withEncryptionLevel( Config.EncryptionLevel.NONE ).toConfig() );
            Session session = driver.session() ){
            long start = System.currentTimeMillis();
            String first = session.run("CALL demo.cache.read(1)")
                    .single()
                    .get("value").toString();
            long end = System.currentTimeMillis();

            assertTrue((end - start) > 5000);

            start = end;
            String firstAgin = session.run("CALL demo.cache.read(1)")
                    .single()
                    .get("value").toString();

            assertTrue((end - start) < 1000);

            assertTrue(first.equals(firstAgin));


        }

    }
}
