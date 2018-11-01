import cn.jko.db_utils.redis.RedisConf;
import cn.jko.db_utils.redis.RedisUtils;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class RedisTest {

    @Test
    public void test1() {
        RedisConf conf = new RedisConf("192.168.1.206", 1101, "kge123");

        Jedis jedis = RedisUtils.createModifyJedis(conf);
        System.out.println(jedis.hset("redisTestKey", "k", "v"));


    }

}
