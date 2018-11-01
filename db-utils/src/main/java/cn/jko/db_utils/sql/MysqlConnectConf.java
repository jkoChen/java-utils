package cn.jko.db_utils.sql;

import lombok.extern.slf4j.Slf4j;

/**
 * mysql的配置
 *
 * @author j.chen@91kge.com  create on 2018/10/31
 */
@Slf4j
public class MysqlConnectConf extends ConnectionConf {


    public MysqlConnectConf(String url, String user, String password) {
        super(url, user, password);
    }

    public MysqlConnectConf(String host, int port, String db, String user, String password) {
        super(String.format("jdbc:mysql://%s:%d/%s?useSSL=false", host, port, db), user, password);
    }

    @Override
    protected String getDriveName() {
        return "com.mysql.jdbc.Driver";
    }
}
