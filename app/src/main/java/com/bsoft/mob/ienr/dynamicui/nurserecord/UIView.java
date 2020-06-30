package com.bsoft.mob.ienr.dynamicui.nurserecord;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 护理记录UI控件
 * 
 * @author hy
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIView {

	/**
	 * 类别名称
	 */
	public String LBMC;
	/**
	 * 类别号
	 */
	public String LBH;

	/**
	 * 组类型
	 */
	public String ZLX;

	/**
	 * 换页标志
	 */
	public String HYBZ;

	/**
	 * 控件列表 数组
	 */
	public List<PlugIn> NRControllist;

}
