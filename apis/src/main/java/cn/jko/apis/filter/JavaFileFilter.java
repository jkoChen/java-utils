package cn.jko.apis.filter;

import java.io.File;

/**
 * 判断一个文件是不是java文件
 *
 * @author slsm258@126.com  create on 2018/10/27
 */
public interface JavaFileFilter {

    String javaFileSuffix = "java";

    boolean isJavaFile(File file);


    static JavaFileFilter defaultJavaFileFilter() {
        return file -> {
            if (file == null || !file.isFile()) return false;
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            return suffix.equalsIgnoreCase(javaFileSuffix);
        };
    }


}
