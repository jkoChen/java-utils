package cn.jko.remote;

import java.util.ArrayList;
import java.util.List;

/**
 * ssh 正向代理配置
 * <p>
 * 有 A B C 三台主机
 * <p>
 * C 可以连接 B
 * <p>
 * B 可以连接 A
 * <p>
 * C 不可以连接 A
 * <p>
 * 这时候可以 C可以ssh连接B 把B上对应的A的ip的 某些端口 映射到本地的一些端口上
 * 这样 C 就可以 通过访问本地的端口 实现连接 A 主机的某些端口
 *
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class PortForwardingLConf extends RemoteConf {
    private List<Forwarding> forwardings = new ArrayList<>();


    public List<Forwarding> getForwardings() {
        return forwardings;
    }

    public PortForwardingLConf addForwarding(int lport, String rhost, int rport) {
        this.forwardings.add(new Forwarding(lport, rhost, rport));
        return this;
    }

    /**
     * 端口转发配置
     */
    class Forwarding {
        private int lport;//本地端口
        private String rhost;//远程主机的远程ip
        private int rport;//远程主机的端口

        public Forwarding(int lport, String rhost, int rport) {
            this.lport = lport;
            this.rhost = rhost;
            this.rport = rport;
        }

        public int getLport() {
            return lport;
        }

        public Forwarding setLport(int lport) {
            this.lport = lport;
            return this;
        }

        public String getRhost() {
            return rhost;
        }

        public Forwarding setRhost(String rhost) {
            this.rhost = rhost;
            return this;
        }

        public int getRport() {
            return rport;
        }

        public Forwarding setRport(int rport) {
            this.rport = rport;
            return this;
        }
    }
}
