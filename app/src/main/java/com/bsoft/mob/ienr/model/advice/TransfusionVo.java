package com.bsoft.mob.ienr.model.advice;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明 输液单
 */
public class TransfusionVo extends BaseVo {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 输液单号
	 */
	public String SYDH;
	/**
	 * 条码编号
	 */
	public String TMBH;
	/**
	 * 输液时间
	 */
	public String SYSJ;
	/**
	 * 开始时间
	 */
	public String KSSJ;
	/**
	 * 开始工号（人名）
	 */
	public String KSGH;
	/**
	 * 结束时间
	 */
	public String JSSJ;
	/**
	 * 结束工号（人名）
	 */
	public String JSGH;
	/**
	 * 输液状态
	 */
	public int SYZT;
	/**
	 * 平均低速
	 */
	public String PJDS;
	/**
	 *住院号
	 */
	public String ZYH;
	
}
