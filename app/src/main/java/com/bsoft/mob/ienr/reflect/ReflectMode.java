package com.bsoft.mob.ienr.reflect;

import org.dom4j.Element;

import java.lang.reflect.Field;


/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-4 上午1:06:07
 * @类说明
 */
@SuppressWarnings("all")
public class ReflectMode {

	public static Object reflect(Class<?> className, Element element)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Object obj=className.newInstance();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String text = element.elementText(fields[i].getName());
			if (null != text && text.length() > 0) {
				if (fields[i].getType().getName()
						.equals(java.lang.String.class.getName())) {
					fields[i].set(obj, text);
				} else if (fields[i].getType().getName()
						.equals(java.lang.Integer.class.getName())
						|| fields[i].getType().getName().equals("int")) {
					fields[i].set(obj, Integer.valueOf(text));
				}
			}
		}
		return obj;
	}

}
