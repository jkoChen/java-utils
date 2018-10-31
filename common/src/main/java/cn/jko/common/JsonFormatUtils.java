package cn.jko.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonFormatUtils {

    /**
     * 删除注释
     *
     * @param value
     * @return
     */
    public static String removeAnnotation(String value) {
        String v = (value + "\n").replaceAll("(//.*?)([\\\"|\\[|\\{|\\]|\\}|\n])", "$2");
        return v.substring(0, v.length() - 1);
    }


    /**
     * 给注释增加三个\t
     *
     * @param value
     * @return
     */
    public static String formatAnnotation(String value) {
        String v = (zipJson(value) + "\n").replaceAll("(//.*?)([\\\"|\\[|\\{|\\]|\\}|\n])", "\t\t\t$1$2");
        return v.substring(0, v.length() - 1);
    }

    public static String zipJson(String jsonString) {
        return (jsonString + "").replaceAll("\\t|\\n|\\r", "");
    }

    /**
     * 格式化
     *
     * @param jsonStr
     * @return
     * @author lizhgb
     * @Date 2015-10-14 下午1:17:35
     */
    public static String formatJson(String jsonStr) {

        if (null == jsonStr || "".equals(jsonStr)) return "";
//        jsonStr = zipJson(jsonStr);
        String _json = removeAnnotation(jsonStr);
        try {
            JSONObject jsonObject = JSON.parseObject(_json);
            if (jsonSize(jsonObject) <= 1) {
                return formatAnnotation(jsonStr);
            }
        } catch (Exception e) {
        }

        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean flag = true;

        int x = 0;
        String value = null;
        int dot = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    if (value != null) {
                        sb.append(value);
                        value = null;
                    }
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    if (value != null) {
                        sb.append(value);
                        value = null;
                    }
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case '\"':
                    if (value != null) {
                        sb.append(value);
                        value = null;
                    }
                    sb.append(current);
                    flag = !flag;
                    break;

                case '\0': //单纯的数组 分割符
                    sb.append('\n');
                    addIndentBlank(sb, indent);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && flag) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '/':
                    if(flag){
                        char n = '\0';
                        if (i + 1 < jsonStr.length()) {
                            n = jsonStr.charAt(i + 1);
                        }
                        x = x + 1;
                        if (x % 2 == 1 && n == '/') {
                            if (sb.substring(sb.length() - 1, sb.length()).equals("\t")) {
                                value = sb.substring(sb.lastIndexOf("\n"), sb.length());
                                sb.delete(sb.lastIndexOf("\n"), sb.length());
                            }
                            sb.append("\t\t");
                        }
                    }

                    sb.append(current);
                    break;
                default:
                    sb.append(current);
            }
            last = current;
        }

        return sb.toString();
    }

    private static int jsonSize(JSONObject jsonObject) {
        int size = jsonObject.size();
        for (Object value : jsonObject.values()) {
            if (value instanceof JSONObject) {
                size += ((JSONObject) value).size();
            } else if (value instanceof JSONArray) {
                size += ((JSONArray) value).size();
            }
        }
        return size;
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    public static String compactJson(String content) {
        String regEx = "[\t\n]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        return m.replaceAll("").trim();
    }


}
