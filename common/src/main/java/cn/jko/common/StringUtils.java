package cn.jko.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String 工具类
 *
 * @author slsm258@126.com
 * create on 2017/8/21
 */
public class StringUtils {

    /**
     * 判断文字是否是空串
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        if (value == null) return true;
        return value.trim().isEmpty();
    }

    public static String removeQuotations(String rawUrl) {
        if (rawUrl == null) return rawUrl;
        return rawUrl.replace("\"", "").trim();
    }


    public static String getMatchStr(String source, String reg) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static String joinArrayString(String[] array, String separator){
        if(array == null || array.length == 0){
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0, len = array.length ; i != len ; i++){
            builder.append(array[i]);
            if(i != len -1){
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    /**
     * 获取最外层的括号中/外的内容
     *
     * @param contents
     * @param type     1 [] 2 {} 3 ()
     * @param out      是否输出括号外的内容
     * @return
     */
    private static String getBracket(String contents, int type, boolean out) {
        int x = 0;

        StringBuilder sb = new StringBuilder();
        StringBuilder sr = new StringBuilder();
        char start = '[';
        char end = ']';
        switch (type) {
            case 1:
                break;
            case 2:
                start = '{';
                end = '}';
                break;
            case 3:
                start = '(';
                end = ')';
                break;
        }

        char current = '\0';
        for (int i = 0; i < contents.length(); i++) {
            current = contents.charAt(i);
            if (current == start) {
                if (x > 0) {
                    sb.append(current);
                }
                x++;
            } else if (current == end) {
                x--;
                if (x > 0) {
                    sb.append(current);
                }
            } else {
                if (x == 0) {
                    sr.append(current);
                } else {
                    sb.append(current);
                }
            }


        }
        return out ? sr.toString() : sb.toString();
    }

    /**
     * 获取最外层的括号中的内容
     *
     * @param contents
     * @param type     1 [] 2 {} 3 ()
     * @return
     */
    public static String getBracketContents(String contents, int type) {
        return getBracket(contents, type, false);
    }

    /**
     * 获取最外层的括号之外的内容
     * <p>
     * e.x. input {content} desc return desc
     *
     * @param contents
     * @param type     1 [] 2 {} 3 ()
     * @return
     */
    public static String getBracketDesc(String contents, int type) {
        return getBracket(contents, type, true);
    }

    public static List<String> getChildrenContents(String childrenContents) {
        List<String> list = new ArrayList<>();
        int _x = 0;//(
        int _y = 0;//[
        int _z = 0;//{
        StringBuilder sb = new StringBuilder();
        char current = '\0';
        for (int i = 0; i < childrenContents.length(); i++) {
            current = childrenContents.charAt(i);
            switch (current) {
                case '{':
                    _z++;
                    sb.append(current);
                    break;
                case '[':
                    _y++;
                    sb.append(current);

                    break;
                case '(':
                    _x++;
                    sb.append(current);

                    break;

                case '}':
                    _z--;
                    sb.append(current);

                    break;
                case ']':
                    _y--;
                    sb.append(current);

                    break;
                case ')':
                    _x--;
                    sb.append(current);
                    break;
                case ',':
                    if (_x == 0 && _y == 0 && _z == 0) {
                        list.add(sb.toString());
                        sb = new StringBuilder();
                    } else {
                        sb.append(current);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }
        list.add(sb.toString());
        return list;
    }

}
