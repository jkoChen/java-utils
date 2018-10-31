package cn.jko.apis.visitor;

import cn.jko.apis.pojo.ApiInfo;
import cn.jko.apis.resolver.ApiClassResolver;
import cn.jko.common.StringUtils;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

/**
 * @author slsm258@126.com  create on 2018/10/27
 */
public class ApiClassVisitor extends GenericVisitorAdapter<ApiInfo, ApiClassResolver> {

    @Override
    public ApiInfo visit(ClassOrInterfaceDeclaration n, ApiClassResolver resolver) {
        if (n != null) {
            ApiInfo apiInfo = new ApiInfo();
            apiInfo.setName(n.getNameAsString());

            apiInfo.setTitle(resolver.apiTitle(n));
            apiInfo.setApiGroup(resolver.apiIndex(n));
            apiInfo.setApiPrefixUrl(resolver.apiBaseUrl(n));
            if (StringUtils.isEmpty(apiInfo.getTitle()) || StringUtils.isEmpty(apiInfo.getApiGroup())) {
                return null;
            }

            return apiInfo;
        }
        return null;
    }
}
