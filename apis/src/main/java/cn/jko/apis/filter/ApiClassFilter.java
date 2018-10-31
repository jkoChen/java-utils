package cn.jko.apis.filter;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;

/**
 * 判断一个类是不是 要生成文档的过滤器
 *
 * @author slsm258@126.com  create on 2018/10/27
 */
public interface ApiClassFilter {

    boolean isApiClass(File file, ClassOrInterfaceDeclaration classOrInterfaceDeclaration);


    static ApiClassFilter defaultApiClassFilter() {
        return (file, compilationUnit) -> false;
    }
}
