import cn.jko.remote.CommandConf;
import cn.jko.remote.CommandUtil;
import cn.jko.remote.PortForwardingLConf;
import org.junit.Test;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class CommandUtilTest {
    @Test
    public void test() {
        CommandConf conf = new CommandConf();
        conf.setRemoteIp("111.231.66.62").setLoginPort(22).setIdentity("D:\\Documents\\Jobs\\kge-conf\\keys\\kge\\ssh_key.qcloud").setUser("kge");
        CommandUtil commandUtil = new CommandUtil(conf);
        System.out.println(commandUtil.exec("ls /data"));
        System.out.println(commandUtil.exec("ls ~"));
    }
}
