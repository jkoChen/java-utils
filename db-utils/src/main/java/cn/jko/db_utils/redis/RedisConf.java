package cn.jko.db_utils.redis;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class RedisConf {

    private String host;
    private int port;
    private String auth;
    private int dbIndex = 0;

    public RedisConf(String host, int port, String auth) {
        this.host = host;
        this.port = port;
        this.auth = auth;
    }

    public RedisConf(String host, int port, String auth, int dbIndex) {
        this(host, port, auth);
        this.dbIndex = dbIndex;
    }

    public String getHost() {
        return host;
    }

    public RedisConf setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public RedisConf setPort(int port) {
        this.port = port;
        return this;
    }

    public String getAuth() {
        return auth;
    }

    public RedisConf setAuth(String auth) {
        this.auth = auth;
        return this;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public RedisConf setDbIndex(int dbIndex) {
        this.dbIndex = dbIndex;
        return this;
    }
}
