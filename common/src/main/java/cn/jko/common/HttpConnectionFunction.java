package cn.jko.common;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author slsm258@126.com  create on 2018/2/24
 */
@FunctionalInterface
public interface HttpConnectionFunction<R> {

    R apply(HttpURLConnection connection) throws IOException;
}
