package cn.jko.common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author slsm258@126.com  create on 2018/2/24
 */
public class HttpUtils {
    public static String get(String url, Map<String, String> param) {
        return httpConn(url,"GET",param);
    }

    public static String post(String url, Map<String, String> param) {
        return httpConn(url,"POST",param);
    }
    public static String httpConn(String url,String method, Map<String, String> param) {
        return conn(url, method, param, null, connection -> {
            StringBuilder result = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
        });
    }

    public static void download(String url, File dirFile, Map<String, String> param) {
        conn(url, "GET", param, null, conn -> {
            InputStream inputStream = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            String fileName = conn.getHeaderField("Content-Disposition");
            fileName = fileName.split("filename=", 2)[1].replaceAll("\"", "");
            fileName = URLDecoder.decode(fileName, "UTF-8");
            File file = new File(dirFile, fileName == null ? String.valueOf(System.currentTimeMillis()) : fileName);
            FileOutputStream fileOut = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fileOut);

            byte[] buf = new byte[4096];
            int length = bis.read(buf);
            //保存文件
            while (length != -1) {
                bos.write(buf, 0, length);
                length = bis.read(buf);
            }
            bos.close();
            bis.close();
            return null;
        });
    }

    private static <T> T conn(String url, String method, Map<String, String> param, Map<String, String> headerMap, HttpConnectionFunction<T> function) {
        BufferedReader in = null;
        HttpURLConnection connection = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);

            connection = (HttpURLConnection) realUrl.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");

            if (headerMap != null) {
                for (String s : headerMap.keySet()) {
                    connection.addRequestProperty(s, headerMap.get(s));
                }
            }
            connection.setUseCaches(false);

            connection.setRequestMethod(method);
            if (param != null && !param.isEmpty()) {
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(param.entrySet().stream().map(s -> s.getKey() + "=" + s.getValue()).collect(Collectors.joining("&")).getBytes());
            }
            connection.connect();

            return function.apply(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                System.out.println(e2.getLocalizedMessage());
            }
        }

    }
}
