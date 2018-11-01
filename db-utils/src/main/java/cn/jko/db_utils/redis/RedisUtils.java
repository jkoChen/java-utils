package cn.jko.db_utils.redis;

import cn.jko.common.AbstractProxyUtil;
import cn.jko.common.ExecuteLogProxyUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class RedisUtils {
    private final static Set<String> queryAllowMethod = new HashSet<>();

    static {
        queryAllowMethod.add("exists");
        queryAllowMethod.add("type");
        queryAllowMethod.add("keys");
        queryAllowMethod.add("randomKey");

        queryAllowMethod.add("get");
        queryAllowMethod.add("mget");

        queryAllowMethod.add("hget");
        queryAllowMethod.add("hmget");
        queryAllowMethod.add("hexists");
        queryAllowMethod.add("hlen");
        queryAllowMethod.add("hkeys");
        queryAllowMethod.add("hvals");
        queryAllowMethod.add("hgetAll");

        queryAllowMethod.add("llen");
        queryAllowMethod.add("lrange");
        queryAllowMethod.add("lindex");

        queryAllowMethod.add("smembers");
        queryAllowMethod.add("scard");
        queryAllowMethod.add("sismember");
        queryAllowMethod.add("srandmember");

        queryAllowMethod.add("zrange");
        queryAllowMethod.add("zrevrange");
        queryAllowMethod.add("zrank");
        queryAllowMethod.add("zrevrank");
        queryAllowMethod.add("zrangeWithScores");
        queryAllowMethod.add("zrevrangeWithScores");
        queryAllowMethod.add("zscore");
        queryAllowMethod.add("zcard");
        queryAllowMethod.add("zrangeByScore");
        queryAllowMethod.add("zrevrangeByScore");
        queryAllowMethod.add("zrevrangeByScoreWithScores");
        queryAllowMethod.add("zrangeByScoreWithScores");
        queryAllowMethod.add("zcount");

    }

    /**
     * 返回一个 只能执行上述穷举出来的方法的 jedis工具
     *
     * 为什么有这个工具
     * 因为 防止生产环境误操作啊
     *
     * @param conf
     * @return
     */
    public static Jedis createQueryJedis(RedisConf conf) {
        return JedisQuery.createProxy(createJedis(conf));
    }

    private static Jedis createJedis(RedisConf conf) {
        Jedis jedis = new Jedis(conf.getHost(), conf.getPort(), 3000);
        jedis.auth(conf.getAuth());
        jedis.select(conf.getDbIndex());
        return jedis;
    }

    /**
     * 返回一个带日志记录的jedis对象
     *
     * @param conf
     * @return
     */
    public static Jedis createModifyJedis(RedisConf conf) {
        return ExecuteLogProxyUtil.createProxy(createJedis(conf));
    }

    private static class JedisQuery extends ExecuteLogProxyUtil {
        protected JedisQuery(Object target) {
            super(target);
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            String methodName = method.getName();
            if (queryAllowMethod.contains(methodName)) {
                return super.intercept(o, method, objects, methodProxy);
            }
            throw new RuntimeException(getClass().getName() + " Method " + methodName + " is not allowed.");
        }
    }
}
