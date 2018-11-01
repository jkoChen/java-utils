package cn.jko.db_utils.sql;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author j.chen@91kge.com  create on 2018/10/31
 */
@Slf4j
public abstract class ConnectionConf {
    private String url;
    private String user;
    private String password;

    public ConnectionConf() {
    }

    public ConnectionConf(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }


    public Connection getNewConnection() {
        try {

            Class.forName(getDriveName());
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            log.error("mysql connect fail .", e);
            throw new RuntimeException(e);
        }
    }

    abstract protected String getDriveName();
}
