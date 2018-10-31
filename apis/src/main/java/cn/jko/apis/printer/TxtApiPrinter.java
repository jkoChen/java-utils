package cn.jko.apis.printer;

import cn.jko.apis.pojo.ApiInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author slsm258@126.com
 * create on 2017/9/1
 */
@Slf4j
public class TxtApiPrinter implements IApiPrinter {

    private static boolean out = false;
    private static String outDir = "/tmp";

    public static void setOut(boolean out) {
        TxtApiPrinter.out = out;
    }

    public static void setOutDir(String outDir) {
        TxtApiPrinter.outDir = outDir;
    }

    public void print(ApiInfo apiInfo, boolean isOverWrite) {
        if (!out) {
            return;
        }
        log.info(apiInfo.getTitle() + "start print.");
        String fileName = apiInfo.getTitle() + "接口.txt";
        File file = new File(outDir + "/" + apiInfo.getApiGroup() + "/" + fileName);
        if (!isOverWrite && file.exists()) {
            return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(file);
            fileWriter.append(new StringApiPrinter().print(apiInfo));


        } catch (IOException e) {
        } finally {
            log.info(apiInfo.getTitle() + "end print --  {}", file.getAbsolutePath());

            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
            }
        }
    }


}
