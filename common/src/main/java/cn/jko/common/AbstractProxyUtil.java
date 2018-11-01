package cn.jko.common;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * 代理工具类
 * <p>
 * 只需要继承该方法 实现  intercept 即可
 * 可以通过 new AbstractProxyUtil(target).create();
 * 或者 通过 AbstractProxyUtil.createProxy(util); 生成代理对象
 * 子类还可以实现 createProxy(object); 来生成代理对象{@link ExecuteLogProxyUtil#createProxy(Object)}
 *
 *
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

    public <T> T create() {
        return createProxy(this);
    }

    public static <T, U extends AbstractProxyUtil> T createProxy(U util) {
        Class<?> tClass = util.getTarget().getClass();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(tClass);
        enhancer.setCallback(util);
        return (T) enhancer.create();
    }


}
