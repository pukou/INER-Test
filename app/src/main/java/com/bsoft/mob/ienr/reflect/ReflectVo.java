package com.bsoft.mob.ienr.reflect;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-14 下午6:56:53
 * @类说明
 */
@SuppressWarnings("rawtypes")
public class ReflectVo {

	public Class className;
	public String tableName;

	public ReflectVo(Class className, String tableName) {
		this.className = className;
		this.tableName = tableName;
	}

}
