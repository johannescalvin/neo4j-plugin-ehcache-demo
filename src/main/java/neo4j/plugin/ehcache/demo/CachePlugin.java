package neo4j.plugin.ehcache.demo;


import org.ehcache.Cache;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;


import java.util.stream.Stream;

public class CachePlugin {
    @Context
    public GraphDatabaseService databaseService;
    @Context
    public Log log;

    public static Cache<Long,String> cache = CacheConfig.serviceCache;
    public static Service service = new ServiceCachedImpl(cache,new ServiceImpl());

    @Procedure("demo.cache.read")
    public Stream<Output> read(@Name("key") long key){
        String value = service.doTask(databaseService,key);
        if (value == null){
            return Stream.empty();
        }

        return Stream.of(new Output(key,value));
    }

    public static class Output{
        public Output(long key, String value) {
            this.key = key;
            this.value = value;
        }

        public long key;
        public String value;
    }


    public static StatisticsService statisticsService = CacheConfig.statisticsService;

    @Procedure("demo.cache.stat")
    public Stream<Stat> test(){
        return Stream.of(
                new StatUtils(statisticsService.getCacheStatistics("serviceCache"))
                        .getStat()
        );
    }

    public class StatUtils{
        public CacheStatistics cacheStatistics;

        public StatUtils(CacheStatistics cacheStatistics) {
            this.cacheStatistics = cacheStatistics;
        }

        public Stat getStat() {
            Stat stat = new Stat();
            stat.hits = cacheStatistics.getCacheHits();
            stat.misses = cacheStatistics.getCacheMisses();
            stat.gets = cacheStatistics.getCacheGets();
            stat.puts = cacheStatistics.getCachePuts();
            stat.evictions = cacheStatistics.getCacheEvictions();
            stat.expirations = cacheStatistics.getCacheExpirations();
            stat.removals = cacheStatistics.getCacheRemovals();
            stat.missPercentage = String.format("%.2f",cacheStatistics.getCacheMissPercentage());
            stat.hitPercentage = String.format("%.2f",cacheStatistics.getCacheHitPercentage());
            stat.averageGetTime = cacheStatistics.getCacheAverageGetTime();

            return stat;

        }
    }

    public static class Stat{

        public long hits;
        public long misses;
        public long gets;
        public long puts;
        public long evictions;
        public long expirations;
        public long removals;
        public double averageGetTime;
        public String missPercentage;
        public String hitPercentage;

    }
}
