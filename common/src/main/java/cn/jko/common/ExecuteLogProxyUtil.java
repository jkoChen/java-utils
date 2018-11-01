package cn.jko.common;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 返回
 *
 * @author j.chen@91kge.com  create on 2018/1/25
 */
public class ExecuteLogProxyUtil extends AbstractProxyUtil {
    Logger log;

    protected ExecuteLogProxyUtil(Object target) {
        super(target);
        log = LoggerFactory.getLogger(target.getClass());
    }

    public static <T> T createProxy(T t) {
        return createProxy(new ExecuteLogProxyUtil(t));
    }


    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        try {
            log.info("execute {}: {}", method.getName(), Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(",")));
            //执行目标对象的方法
            return methodProxy.invoke(target, args);
        } catch (Exception e) {
            log.info("execute error {}: {}", method.getName(), Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(",")));
            throw e;
        }
    }
}
