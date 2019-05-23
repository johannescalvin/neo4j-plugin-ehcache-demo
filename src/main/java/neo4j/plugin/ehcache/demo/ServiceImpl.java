package neo4j.plugin.ehcache.demo;

import org.neo4j.graphdb.GraphDatabaseService;

import java.util.UUID;

public class ServiceImpl implements Service {

    public String doTask(GraphDatabaseService databaseService,long key) {
        // 模拟一个耗时的处理逻辑
        try{
            Thread.sleep(5000);
        }catch (InterruptedException ex){

        }
        return UUID.randomUUID().toString();
    }
}
