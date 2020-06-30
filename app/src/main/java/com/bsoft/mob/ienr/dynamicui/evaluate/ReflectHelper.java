package com.bsoft.mob.ienr.dynamicui.evaluate;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Classichu on 2017-7-6.
 */
public class ReflectHelper {

    /**
     * @param tClass
     * @param tag
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T setupData(Class<T> tClass, String tag) throws Exception {
        T t = tClass.newInstance();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType() == String.class) {
                //field.set(t, "T" + field.getName() + tag);
                field.set(t, "1");
            } else if (field.getType() == Short.class) {
                field.setShort(t, Short.valueOf("1"));
            } else if (field.getType() == Integer.class) {
                field.setInt(t, 1);
            } else if (field.getType() == Long.class) {
                field.setLong(t, 1L);
            } else if (field.getType() == Boolean.class) {
                field.setBoolean(t, true);
            } else if (field.getType() == Character.class) {
                field.setChar(t, 'A');
            } else if (field.getType() == Float.class) {
                field.setFloat(t, 1.0F);
            } else if (field.getType() == Double.class) {
                field.setDouble(t, 1.0D);
            } else if (field.getType() == Byte.class) {
                field.setByte(t, Byte.valueOf("1"));
            } else if (field.getType() == short.class) {
                field.setShort(t, Short.valueOf("1"));
            } else if (field.getType() == int.class) {
                field.setInt(t, 1);
            } else if (field.getType() == long.class) {
                field.setLong(t, 1L);
            } else if (field.getType() == boolean.class) {
                field.setBoolean(t, true);
            } else if (field.getType() == char.class) {
                field.setChar(t, 'A');
            } else if (field.getType() == float.class) {
                field.setFloat(t, 1.0F);
            } else if (field.getType() == double.class) {
                field.setDouble(t, 1.0D);
            } else if (field.getType() == byte.class) {
                field.setByte(t, Byte.valueOf("1"));
            }

        }
        return t;
    }

    /**
     * 内部的List 解析不出来
     *
     * @param map
     * @param tClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T map2Bean(Map map, Class<T> tClass) throws Exception {
        T t = tClass.newInstance();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase(entry.getKey().toString())) {
                    field.setAccessible(true);
                    field.set(t, entry.getValue());
                    break;
                }
            }
        }
        return t;
    }
}
