package cn.jko.common;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author j.chen@91kge.com  create on 2018/11/1
 */
public abstract class AbstractProxyUtil implements MethodInterceptor {

    protected Object target;

    protected AbstractProxyUtil(Object target) {
        this.target = target;
    }

    protected Object getTarget() {
        return target;
    }

    public Object create() {
        return createProxy(this);
    }


    protected static <T> T createProxy(AbstractProxyUtil util) {
        Class<?> tClass = util.getTarget().getClass();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(tClass);
        enhancer.setCallback(util);
        return (T) enhancer.create();
    }


}
