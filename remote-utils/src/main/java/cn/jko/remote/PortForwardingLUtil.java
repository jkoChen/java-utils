package cn.jko.remote;

import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;

/**
 * ssh 正向代理工具
 *
 * @author j.chen@91kge.com  create on 2018/11/1
 */
@Slf4j
public class PortForwardingLUtil extends SSHUtil {

    public PortForwardingLUtil(PortForwardingLConf conf) {
        super(conf);
    }


    public void start() {
        if (!isConnect()) {
            connectSsh();
        }
        for (PortForwardingLConf.Forwarding forwarding : ((PortForwardingLConf) conf).getForwardings()) {
            int mysql_assinged_port = 0;//端口映射 转发
            try {
                mysql_assinged_port = session.setPortForwardingL(forwarding.getLport(), forwarding.getRhost(), forwarding.getRport());
                log.info("start port forwarding : {}_{}:{} -> localhost:{}", conf.getRemoteIp(), forwarding.getRhost(), forwarding.getRport(), mysql_assinged_port);
            } catch (JSchException e) {
                log.error("start port forwarding  error.", e);
            }
        }

    }

    public void stop() {
        for (PortForwardingLConf.Forwarding forwarding : ((PortForwardingLConf) conf).getForwardings()) {
            log.info("stop port forwarding : {}_{}:{} -> localhost:{}", conf.getRemoteIp(), forwarding.getRhost(), forwarding.getRport(), forwarding.getLport());
        }
        disconnect();
    }
}
