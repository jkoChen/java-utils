package cn.jko.db_utils.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * sql 操作的工具类
 *
 * @author j.chen@91kge.com  create on 2018/10/31
 */
public abstract class SqlUtil {

    /**
     * 永久存在
     * 直到调用close
     */
    protected Connection connection;

    public void close() {
        try {
            if (connection != null) {
                this.connection.close();
                this.connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
