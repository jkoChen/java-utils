package cn.jko.apis.resolver;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

/**
 * api 类处理器
 * 用于解析 类  来生成相关的信息
 *
 * @author slsm258@126.com  create on 2018/10/29
 */
public interface ApiClassResolver {

    /**
     * api标题内容
     * @param classOrInterfaceDeclaration
     * @return
     */
    String apiTitle(ClassOrInterfaceDeclaration classOrInterfaceDeclaration);

    /**
     * api 分组
     * 可以给多个api分组 这样调用TxtApiPrinter 打印的时候 可以打印到同一个文件夹中 方便整理
     * @param classOrInterfaceDeclaration
     * @return
     */
    String apiIndex(ClassOrInterfaceDeclaration classOrInterfaceDeclaration);

    /**
     * api 的前置url
     * @param classOrInterfaceDeclaration
     * @return
     */
    String apiBaseUrl(ClassOrInterfaceDeclaration classOrInterfaceDeclaration);

}
