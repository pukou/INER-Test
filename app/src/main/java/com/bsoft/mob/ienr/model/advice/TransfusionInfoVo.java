package com.bsoft.mob.ienr.model.advice;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明 输液明细
 */
public class TransfusionInfoVo extends BaseVo {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 记录序号（医嘱序号）
	 */
	public String YZXH;
	/**
	 * 输液单号
	 */
	public String SYDH;
	/**
	 * 医嘱名称
	 */
	public String YZMC;
	/**
	 * 剂量信息
	 */
	public String JLXX;
	/**
	 * 数量信息
	 */
	public String SLXX;
}
