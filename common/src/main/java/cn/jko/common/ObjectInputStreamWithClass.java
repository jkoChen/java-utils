package cn.jko.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * 反序列化的时候 可以自定义传入一个类
 * <p>
 * 需要类完全相同
 * serialVersionUID相同
 * <p>
 * Created by J.Chen on 2017/6/2.
 */
public class ObjectInputStreamWithClass extends ObjectInputStream {

    private Class clazz;

    public ObjectInputStreamWithClass(InputStream in, Class clazz) throws IOException {
        super(in);
        this.clazz = clazz;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        if (ObjectStreamClass.lookup(clazz).getSerialVersionUID() == desc.getSerialVersionUID()) {
            return super.resolveClass(ObjectStreamClass.lookup(clazz));
        }

        return super.resolveClass(desc);
    }


}
