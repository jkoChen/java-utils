import cn.jko.db_utils.sql.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class SqlUtilTest {


    @Test
    public void test1() throws InterruptedException {
        MysqlConnectConf connectConf = new MysqlConnectConf("192.168.1.53", 3306, "mmyz", "chenj", "123456");
        QueryUtil queryUtil = SqlUtils.createQueryUtil(connectConf);
        System.out.println(queryUtil.selectList("select * from cat_food"));
        ModifyUtil modifyUtil = SqlUtils.createModifyUtil(connectConf, true);
        String insert = "insert into cat_food values(?,?,?,?)";
        Thread t1 = new Thread(() -> {
            modifyUtil.batchModify(
                    new ModifyParam(insert, Arrays.asList(2, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(3, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(4, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(5, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(6, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(7, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(8, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(9, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(10, 1, 1, 1)),
                    new ModifyParam(insert, Arrays.asList(11, 1, 1, 1))
            );
        });
        Thread t2 = new Thread(() -> {
            System.out.println(modifyUtil.selectList("select * from cat_food"));
        });
        t1.start();
        Thread.sleep(80);
        t2.start();

        Thread.sleep(1000);


    }


}
