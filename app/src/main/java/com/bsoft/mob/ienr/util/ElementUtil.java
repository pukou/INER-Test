package com.bsoft.mob.ienr.util;

import org.dom4j.Element;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-5 下午9:36:06
 * @类说明
 */
public class ElementUtil {

	public static String getValue(Element element, String key) {
		return element.attributeValue(key);
	}

	public static String getTypeName(Element element) {
		return element.getName();
	}

}
