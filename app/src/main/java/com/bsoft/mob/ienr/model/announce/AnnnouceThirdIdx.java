package com.bsoft.mob.ienr.model.announce;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 宣教表单index
 * 
 * @author hy
 * 
 */
public class AnnnouceThirdIdx {

	/**
	 * 一级宣教序号
	 */

	public String YSBS;

	/**
	 * 二级宣教序号
	 */
	@JsonIgnore
	public String LBBS;

	/**
	 * 三宣教序号
	 */
	public String XMBS;

	/**
	 * 宣教名称
	 */
	@JsonIgnore
	public String XMMC;

	/**
	 * 备注
	 */
	@JsonIgnore
	public String BZXX;

	/**
	 * 选择标识
	 */
	@JsonIgnore
	public boolean checked;
}
