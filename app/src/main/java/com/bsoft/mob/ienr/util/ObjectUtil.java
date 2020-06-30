package com.bsoft.mob.ienr.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Classichu on 2017/5/10.
 */
public class ObjectUtil {
    public static <T> T deepCopy(T obj) throws Exception {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new Exception("对象中包含没有继承序列化的对象");
        } finally {
            try {
                baos.close();
                oos.close();
                bais.close();
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
