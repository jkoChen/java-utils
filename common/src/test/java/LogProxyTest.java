import cn.jko.common.ExecuteLogProxyUtil;
import com.Demo;
import org.junit.Test;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public class LogProxyTest {

    @Test
    public void test1(){
        Demo demo = ExecuteLogProxyUtil.createProxy(new Demo());
        demo.add(1,1);
    }
}
