package cn.jko.apis.resolver;

import cn.jko.apis.result.ClassModel;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * 默认支持 根据方法返回类型返回返回模型
 * 如果不是根据 方法返回类型来生成文档 需要 自行添加自定义的处理器
 *
 * @author slsm258@126.com  create on 2018/10/30
 */
public interface ApiReturnResolver {

    /**
     * 是否使用改处理器
     *
     * @param methodDeclaration
     * @return
     */
    boolean isHandle(MethodDeclaration methodDeclaration);

    /**
     * 处理
     *
     * @param methodDeclaration
     * @return
     */
    ClassModel handle(MethodDeclaration methodDeclaration);
}
