package neo4j.plugin.ehcache.demo;

import org.neo4j.graphdb.GraphDatabaseService;

public interface Service {

    String doTask(GraphDatabaseService databaseService,long key);
}
