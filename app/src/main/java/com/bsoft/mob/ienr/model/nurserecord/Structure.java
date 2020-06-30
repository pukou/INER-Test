package com.bsoft.mob.ienr.model.nurserecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 护理记录模块一级结构体
 * 
 * @author hy
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Structure {

	/**
	 * 类别编号
	 */
	public String LBBH;// 类别编号

	/**
	 * 类别名称
	 */
	public String LBMC;// 类别名称

	/**
	 * 类别级别
	 */
	public String LBJB;// 类别级别

}
