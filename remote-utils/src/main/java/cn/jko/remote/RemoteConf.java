package cn.jko.remote;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class RemoteConf {
    //远程地址
    private String remoteIp;
    //ssh 登陆ip
    private int loginPort;
    //用户名
    private String user;
    //密码
    private String password;
    //私钥地址
    private String identity;


    public String getRemoteIp() {
        return remoteIp;
    }

    public RemoteConf setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
        return this;
    }

    public int getLoginPort() {
        return loginPort;
    }

    public RemoteConf setLoginPort(int loginPort) {
        this.loginPort = loginPort;
        return this;
    }

    public String getUser() {
        return user;
    }

    public RemoteConf setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RemoteConf setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getIdentity() {
        return identity;
    }

    public RemoteConf setIdentity(String identity) {
        this.identity = identity;
        return this;
    }
}
