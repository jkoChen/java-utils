package cn.jko.apis.visitor;

import cn.jko.apis.pojo.RequestInfo;
import cn.jko.apis.resolver.ApiMethodResolver;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

/**
 * @author slsm258@126.com  create on 2018/10/27
 */
public class ApiMethodVisitor extends GenericVisitorAdapter<RequestInfo, ApiMethodResolver> {

    @Override
    public RequestInfo visit(MethodDeclaration n, ApiMethodResolver resolver) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setUrl(resolver.requestUrl(n));
        requestInfo.setMethod(resolver.requestMethod(n));
        requestInfo.setName(resolver.requestName(n));
        requestInfo.setDescription(resolver.requestDescription(n));
        requestInfo.setParams(resolver.requestParam(n));
        resolver.requestFailResults(n).forEach(requestInfo::addFailResult);
        return requestInfo;
    }
}
