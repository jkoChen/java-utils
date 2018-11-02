package cn.jko.remote;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 执行远程脚本
 *
 * @author j.chen@91kge.com  create on 2018/11/1
 */
@Slf4j
public class CommandUtil extends SSHUtil {

    private ChannelExec channel;

    public CommandUtil(CommandConf conf) {
        super(conf);
    }

    public List<String> exec(String cmd) {
        List<String> result = new ArrayList<>();
        if (!isConnect()) {
            connectSsh();
        }
        BufferedReader reader = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            channel.connect();
            InputStream in = channel.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            String buf = null;
            while ((buf = reader.readLine()) != null) {
                result.add(buf);
            }
        } catch (JSchException | IOException e) {
            log.error("exec cmd {} error . {}", cmd, e.getLocalizedMessage());
        } finally {
            //使用完毕关闭管道
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
            channel.disconnect();
            channel = null;
        }
        return result;
    }

    /**
     * 使用完成 调用close
     */
    public void close() {
        disconnect();
    }

}
