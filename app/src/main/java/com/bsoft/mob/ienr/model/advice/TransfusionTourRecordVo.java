package com.bsoft.mob.ienr.model.advice;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明 输液记录
 */
public class TransfusionTourRecordVo extends BaseVo {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 巡视时间
	 */
	public String XSSJ;
	/**
	 * 巡视工号
	 */
	public String XSGH;
	/**
	 * 输液滴速
	 */
	public String SYDS;
	/**
	 * 输液反应
	 */
	public String SYFY;

	/**
	 * 输液单号
	 */
	public String SYDH;

	// 机构ID
	public String JGID;

	// 巡视姓名
	public String XSXM;

	// 输液反应名称
	public String FYMC;

}
