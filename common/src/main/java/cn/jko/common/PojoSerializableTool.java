package cn.jko.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;

/**
 * Created by J.Chen on 2016/12/15.
 */
public class PojoSerializableTool {

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T decodeByte(byte[] arr,Class<T> clazz) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bi = new ByteArrayInputStream(arr);
        ObjectInputStreamWithClass ois = new ObjectInputStreamWithClass(bi,clazz);
        return (T) ois.readObject();
    }

    /**
     * encode 得到的字符串还原成原对象
     * @param value
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T extends Serializable> T decode(String value,Class<T> clazz) throws IOException, ClassNotFoundException {
        if (value == null) {
            return null;
        }
        byte[] arr = Base64.getDecoder().decode(value);
        if (arr == null) {
            return null;
        }
        return decodeByte(arr,clazz);
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String value = "rO0ABXNyACtjb20ucXEuYWN0MjAxNi5mbG93ZXIucG9qby5GbG93ZXJNb29uTm90ZVZPCy3zTG8VlLwCAAlJAAFkSQALZmxvd2VyQ291bnRJAAtwcmFpc2VDb3VudEkADHByYWlzZVN0YXR1c0oABnVzZXJJZEwAB2NvbnRlbnR0ABJMamF2YS9sYW5nL1N0cmluZztMAAhmcm9tTmlja3EAfgABTAAGdG9OaWNrcQB+AAFMAAR1dWlkcQB+AAF4cAAAAAAAAAk/AAAAZgAAAAEAAAAAAAEiJXQAEuiAgeWFrO+8jOS5iOS5iOWTknQABumYoemqqHQABumYoemZjHQAJDdkNDcyNThlLTcyNmQtNGFmNi05NjdmLWNhZTE1NzhiOWI0MQ==";
//        FlowerMoonNoteVO note = decode(value);
//        System.out.println(note.getUserId());
    }


}
