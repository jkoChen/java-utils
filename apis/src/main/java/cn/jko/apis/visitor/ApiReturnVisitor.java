package cn.jko.apis.visitor;

import cn.jko.apis.resolver.ApiReturnResolver;
import cn.jko.apis.result.ClassModel;
import cn.jko.apis.utils.ParseUtils;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回模型
 *
 * @author slsm258@126.com  create on 2018/10/27
 */
@Slf4j
public class ApiReturnVisitor extends GenericVisitorAdapter<ClassModel, ClassTypeParseVisitorParam> {


    /**
     * 其他的处理器
     */
    private List<ApiReturnResolver> returnResolvers;

    public ApiReturnVisitor() {
        this.returnResolvers = new ArrayList<>();
    }

    public ApiReturnVisitor(List<ApiReturnResolver> returnResolvers) {
        this.returnResolvers = returnResolvers;
    }

    public void addReturnResolver(ApiReturnResolver returnResolver) {
        returnResolvers.add(returnResolver);
    }

    @Override
    public ClassModel visit(MethodDeclaration methodDeclaration, ClassTypeParseVisitorParam param) {

        //判断返回类型
        //如果存在某些条件 直接 生成返回模型
        if (this.returnResolvers != null && returnResolvers.size() > 0) {
            for (ApiReturnResolver r : returnResolvers) {
                if (r.isHandle(methodDeclaration)) {
                    return r.handle(methodDeclaration);
                }
            }
        }
        //否则 根据 返回的具体类型 递归生成 返回模型
        return ParseUtils.parseClassModel(methodDeclaration.getType(), param);
    }


}
