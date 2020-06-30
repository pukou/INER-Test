package com.bsoft.mob.ienr.util;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayUtil {
	
	// 支持基础类型，结果需要转换类型
	public final static Object copyOf(Object $source, int $newLength) {
		Class<?> __type = $source.getClass().getComponentType();
		int __oldLength = Array.getLength($source);
		Object __target = Array.newInstance(__type, $newLength);
		int __preserveLength = Math.min(__oldLength, $newLength);
		System.arraycopy($source, 0, __target, 0, __preserveLength);
		return __target;
	}

	// 支持泛型，但不支持基础类型数组，例如要处理byte[]需要使用上面的方法。
	public final static <T> T[] copyOf(T[] $source, int $newLength) {
		Class<?> __type = $source.getClass().getComponentType();
		int __oldLength = Array.getLength($source);
		@SuppressWarnings("unchecked")
		T[] __target = (T[]) Array.newInstance(__type, $newLength);
		int __preserveLength = Math.min(__oldLength, $newLength);
		System.arraycopy($source, 0, __target, 0, __preserveLength);
		return __target;
	}

	public static <T> boolean inList(List<T> list, T item){
		if(null == list || list.size() == 0) return false;
		for(T node : list){
			if(node.equals(item)) return true;
		}

		return false;
	}
}
