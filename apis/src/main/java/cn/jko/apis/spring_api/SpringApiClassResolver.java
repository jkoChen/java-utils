package cn.jko.apis.spring_api;

import cn.jko.apis.resolver.ApiClassResolver;
import cn.jko.common.StringUtils;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

/**
 * spring 文档处理器
 *
 * @author slsm258@126.com  create on 2018/10/29
 */
public class SpringApiClassResolver implements ApiClassResolver {
    @Override
    public String apiTitle(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        return classOrInterfaceDeclaration.getJavadoc().map(s -> s.getDescription().toText()).orElse(null);
    }

    @Override
    public String apiIndex(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        return apiTitle(classOrInterfaceDeclaration);
    }

    @Override
    public String apiBaseUrl(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        return classOrInterfaceDeclaration.getAnnotationByName("RequestMapping").map(s -> {
            if (s instanceof SingleMemberAnnotationExpr)
                return StringUtils.removeQuotations(((SingleMemberAnnotationExpr) s).getMemberValue().toString());
            return null;
        }).orElse(null);
    }
}
