package cn.jko.common;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 日志代理类
 * <p>
 * 代理类 使得调用类的所有方法时 记录日志
 *
 * @author j.chen@91kge.com  create on 2018/1/25
 */
@Slf4j
public class ExecuteLogProxyUtil extends AbstractProxyUtil {

    protected ExecuteLogProxyUtil(Object target) {
        super(target);
    }

    public static <T> T createProxy(T t) {
        return createProxy(new ExecuteLogProxyUtil(t));
    }


    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        try {
            log.info("{} execute {}: {}", target.getClass(), method.getName(), Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(",")));
            //执行目标对象的方法
            return methodProxy.invoke(target, args);
        } catch (Exception e) {
            log.info("{} execute error {}: {}", target.getClass(), method.getName(), Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(",")));
            throw e;
        }
    }
}
