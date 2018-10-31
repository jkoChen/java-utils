package cn.jko.apis.spring_api;

import cn.jko.apis.filter.ApiClassFilter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;

/**
 * Spring 文档 过滤器
 *
 * @author slsm258@126.com  create on 2018/10/29
 */
public class SpringApiClassFilter implements ApiClassFilter {
    @Override
    public boolean isApiClass(File file, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        return classOrInterfaceDeclaration != null && (classOrInterfaceDeclaration.getAnnotationByName("RestController").isPresent() || classOrInterfaceDeclaration.getAnnotationByName("Controller").isPresent());
    }
}
