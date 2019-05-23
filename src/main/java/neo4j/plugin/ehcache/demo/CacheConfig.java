package neo4j.plugin.ehcache.demo;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.impl.internal.statistics.DefaultStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

public class CacheConfig {
    private static Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    public static Cache<Long,String> serviceCache;
    public static CacheManager cacheManager;
    public static StatisticsService statisticsService;

    static {
        init();
    }

    private static void init(){
        statisticsService = new DefaultStatisticsService();
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .using(statisticsService)
                .build(true);

        long ttl = getTTL("demo.cache.ttl");

        ResourcePools resourcePools = ResourcePoolsBuilder
                .newResourcePoolsBuilder()
                .heap(10000)
//                .offheap(10,MemoryUnit.MB)
                .build();

        CacheConfiguration<Long,String> configuration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Long.class, String.class,resourcePools)
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ttl)))
                .build();

        serviceCache = cacheManager.createCache("serviceCache",configuration);
    }

    private static long getTTL(String propKey){
        String ttl = System.getProperty(propKey);
        long seconds = 60 * 60 * 24 ;
        try{
            seconds = Long.parseLong(ttl);
        }catch (NumberFormatException ex){
            logger.info("demo.cache.ttl Parse failed! the default value will be used!");
        }
        return seconds;
    }
}
