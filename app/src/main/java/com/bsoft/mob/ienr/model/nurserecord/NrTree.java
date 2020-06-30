package com.bsoft.mob.ienr.model.nurserecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 护理记录历史项
 * 
 * @author hy
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NrTree {

	/**
	 * 记录编号
	 */
	public String JLBH;

	/**
	 * 结构编号
	 */
	public String JGBH;

	/**
	 * 类别编号
	 */
	public String LBBH;

	/**
	 * 显示内容
	 */
	public String XSNR;

	/**
	 * 记录时间，格式2014-04-17 15:07:00
	 */
	public String JLSJ;

	/**
	 * 完成标志 1标志已签名
	 */
	public boolean WCZT;

	/**
	 * 审核标志
	 */
	public boolean SHZT;

	/**
	 * 是否选中
	 */
	public boolean SFXZ;

	/**
	 * 是否一天中的第一条记录
	 */
	public boolean DOFR;
	//addd
	public String SXHS;
	/**
	 * 子记录列表(多层)
	 */
	public List<NrTree> ZML;

}
