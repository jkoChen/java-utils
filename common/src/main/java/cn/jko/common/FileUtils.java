package cn.jko.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 *
 * @author slsm258@126.com
 * create on 2017/8/21
 */
public class FileUtils {
    private static void listFiles(File project, List<File> result, FilenameFilter filter) {
        File[] fileList = project.listFiles();
        for (File f : fileList) {
            if (f.getName().startsWith(".")) {
                continue;
            }
            if (f.isFile() && filter.accept(f, f.getName())) {
                result.add(f);
            } else if (f.isDirectory()) {
                listFiles(f, result, filter);
            }
        }
    }

    public static List<File> listFile(File project, FilenameFilter filter) {
        List<File> result = new ArrayList<>();
        listFiles(project, result, filter);
        return result;
    }


}
