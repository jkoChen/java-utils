package cn.jko.remote;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * ssh 登陆工具
 *
 * @author j.chen@91kge.com  create on 2018/11/1
 */
@Slf4j
public class SSHUtil {

    protected RemoteConf conf;

    protected Session session;

    //标识登陆端
    private UUID uuid;

    private boolean isConnect = false;

    public boolean isConnect() {
        return isConnect;
    }

    public SSHUtil(RemoteConf conf) {
        this.conf = conf;
    }

    /**
     * ssh登陆
     * <p>
     * 一般来说不直接使用 给子类使用
     */
    protected void connectSsh() {
        JSch jsch = new JSch();
        try {
            if (conf.getIdentity() != null) {

                jsch.addIdentity(conf.getIdentity());

            }
            session = jsch.getSession(conf.getUser(), conf.getRemoteIp(), conf.getLoginPort());
            if (conf.getIdentity() == null) {
                //密码登陆
                session.setPassword(conf.getPassword());
            }
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            uuid = UUID.randomUUID();
            log.info("login server {} success -- {} :server version {} ", conf.getRemoteIp(), uuid, session.getServerVersion());
            isConnect = true;
        } catch (JSchException e) {
            log.info("login server {} error. {}", conf.getRemoteIp(), e.getLocalizedMessage());
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (isConnect) {
            log.info("disconnect server {} -- {}", conf.getRemoteIp(), uuid);
            session.disconnect();
            session = null;
            isConnect = false;
        }
    }


}
