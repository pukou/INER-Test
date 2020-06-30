package com.bsoft.mob.ienr.model.blood;

import java.io.Serializable;

public class BloodReciveInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 444321233667153111L;

	/**
	 * 标本条码
	 */
	public String XMTM;

	/**
	 * 标本名称
	 */
	public String XMMC;

	/**
	 * 标本ID
	 */
	public String XMID;

	/**
	 * 病人床号
	 */
	public String BRCH;

	/**
	 * 病人姓名
	 */
	public String BRXM;

	/**
	 * 病人性别
	 */
	public String XBMC;

	/**
	 * 病人年龄
	 */
	public String BRNL;

	/**
	 * 采集时间
	 */
	public String CJSJ;

	/**
	 * 采集人工号
	 */
	public String CXGH;

	/**
	 * 采集人姓名
	 */
	public String CJXM;

	/**
	 * 标本状态
	 * 
	 * 0:正常 1:异常 其它待扩展
	 */
	public String Status;

	/**
	 * 签收姓名
	 */
	public String QSXM;

	/**
	 * 签收工号
	 */
	public String QSGH;

	/**
	 * 送血姓名
	 */
	public String SXXM;

	/**
	 * 送血工号
	 */
	public String SXGH;

	/**
	 * 送交时间
	 */
	public String SJSJ;

	/**
	 * 签收时间
	 */
	public String QSSJ;

	/**
	 * 是否选中
	 */
	public boolean isSelected = false;
}
