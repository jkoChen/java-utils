package cn.jko.apis.filter;

import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * 方法是不是要生成的请求文档过滤器
 *
 * @author slsm258@126.com  create on 2018/10/27
 */
public interface ApiMethodFilter {

    boolean isRequestMethod(MethodDeclaration methodDeclaration);

    static ApiMethodFilter defaultApiMethodFilter(){
        return methodDeclaration -> false;
    }

}
