import cn.jko.db_utils.redis.RedisConf;
import cn.jko.db_utils.redis.RedisUtils;
import cn.jko.db_utils.sql.MysqlConnectConf;
import cn.jko.db_utils.sql.QueryUtil;
import cn.jko.db_utils.sql.SqlUtils;
import cn.jko.remote.PortForwardingLConf;
import cn.jko.remote.PortForwardingLUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class PortForwardingLTest {
    @Test
    public void test() {
        PortForwardingLConf conf = new PortForwardingLConf();
        conf.setRemoteIp("111.231.66.62").setLoginPort(22).setIdentity("D:\\Documents\\Jobs\\kge-conf\\keys\\kge\\ssh_key.qcloud").setUser("kge");
        conf.addForwarding(15565, "10.66.117.128", 3306);
        conf.addForwarding(15566, "10.66.88.104", 6379);
        PortForwardingLUtil util = new PortForwardingLUtil(conf);
        util.start();
        QueryUtil queryUtil = SqlUtils.createQueryUtil(new MysqlConnectConf("127.0.0.1", 15565, "ktv_jykg", "kge", "kge123"));
        System.out.println(queryUtil.selectOne("select * from user_info__0 limit 1"));
        Jedis jedis = RedisUtils.createQueryJedis(new RedisConf("127.0.0.1", 15566, "d7e540e0-c418-4c56-ad64-6444d4c65987:kge123@91kge"));
        System.out.println(jedis.hgetAll("muser_info:1088"));
        util.stop();

    }

}
