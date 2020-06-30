package com.bsoft.mob.ienr.model.nurserecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 护理记录模板
 * 
 * @author hy
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Template {

	/**
	 * 结构编号
	 */
	public String JGBH;

	/**
	 * 结构名称
	 */
	public String JGMC;

	/**
	 * 所属科室
	 */
	public String SSKS;

	/**
	 * 病历类别
	 */
	public String BLLB;

	/**
	 * 病历类别名称
	 */
	public String BLLBMC;

	/**
	 * 模板类别
	 */
	public String MBLB;

	/**
	 * 模板类别名称
	 */
	public String MBLBMC;

	/**
	 * 是否独立页码
	 */
	public String DLYM;

	/**
	 * 备注信息
	 */
	public String BZXX;

}
