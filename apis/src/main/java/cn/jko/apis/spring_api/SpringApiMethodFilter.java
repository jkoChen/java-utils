package cn.jko.apis.spring_api;

import cn.jko.apis.filter.ApiMethodFilter;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * spring 请求方法过滤器
 *
 * @author slsm258@126.com  create on 2018/10/29
 */
public class SpringApiMethodFilter implements ApiMethodFilter {
    @Override
    public boolean isRequestMethod(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getAnnotations().stream().anyMatch(s -> {
            return s.getNameAsString().equals("GetMapping")
                    || s.getNameAsString().equals("PostMapping")
                    || s.getNameAsString().equals("RequestMapping");
        });
    }
}
