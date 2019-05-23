package neo4j.plugin.ehcache.demo;

import org.ehcache.Cache;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceCachedImpl implements Service {
    private static final String NO_RESULT = "";

    private static Logger logger = LoggerFactory.getLogger(ServiceCachedImpl.class);
    private Cache<Long,String> cache;
    private Service service;

    public ServiceCachedImpl(Cache<Long, String> cache, Service service) {
        this.cache = cache;
        this.service = service;
    }

    public String doTask(GraphDatabaseService databaseService, long key) {

        // 尝试从缓存获取，若存在，则直接返回
        String value = cache.get(key);
        if (value != null){
            logger.debug("key " + key + "hits!");
            return value;
        }

        logger.debug("key " + key + "miss!");
        // 缓存不命中，则需要重新集散，并写缓存
        value = service.doTask(databaseService,key);
        // 即使结果为null, 也缓存起来; 以防止DDoS攻击
        // 不过不能直接缓存null值,不然，按照现在的写法,和不缓存没啥两样
        value = getDefaultValueIfNull(value);
        cache.put(key,value);

        return value;
    }

    private String getDefaultValueIfNull(String value){
        if (value == null){
            return NO_RESULT;
        }

        return value;
    }
}
