package cn.jko.apis.resolver;

import cn.jko.apis.pojo.ParamInfo;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;

/**
 * 请求方法 处理类
 *
 *
 * @author slsm258@126.com  create on 2018/10/29
 */
public interface ApiMethodResolver {

    /**
     * 获取 请求的url
     * @param methodDeclaration
     * @return
     */
    String requestUrl(MethodDeclaration methodDeclaration);

    /**
     * 获取请求的方法
     * @param methodDeclaration
     * @return
     */
    String requestMethod(MethodDeclaration methodDeclaration);

    /**
     * 获取 请求的名称
     * @param methodDeclaration
     * @return
     */
    String requestName(MethodDeclaration methodDeclaration);

    /**
     * 获取请求的详细描述
     * @param methodDeclaration
     * @return
     */
    String requestDescription(MethodDeclaration methodDeclaration);

    /**
     * 获取请求的参数
     * @param methodDeclaration
     * @return
     */
    List<ParamInfo> requestParam(MethodDeclaration methodDeclaration);

    /**
     * 获取请求失败示例
     * @param methodDeclaration
     * @return
     */
    List<String> requestFailResults(MethodDeclaration methodDeclaration);



}
