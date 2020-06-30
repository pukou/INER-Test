package com.bsoft.mob.ienr.dynamicui.nurserecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * 下拉选择项
 * 
 * @author hy
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PouponItem {

	/**
	 * 选择内容
	 */
	public String XZNR;

	/**
	 * 特殊颜色
	 */
	public String TSYS;
	/**
	 * 选择号
	 */
	public String XZH;

	public String VALUE;

	/**
	 * 福建协和客户化：前置文本
	 */
	public String QZWB;
	/**
	 * 默认值
	 */
	public boolean MRZ;

	/**
	 * 是否选中
	 */
	public Boolean ISCHECK = false;

}
