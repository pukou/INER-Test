package com.bsoft.mob.ienr.model.advice;

import java.io.Serializable;

public class AdviceDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2838648118163185569L;

	/**
	 * 归属类型
	 */
	public String GSLX;

	/**
	 * 执行状态 0非 1已执行 2 running 4 暂停 5拒绝
	 */
	public int ZXZT;

	/**
	 * 计划时间
	 */
	public String JHSJ;

	/**
	 * 开始时间
	 */
	public String KSSJ;

	/**
	 * 开始工号
	 */
	public String KSGH;

	/**
	 * 结束工号
	 */
	public String JSGH;

	/**
	 * 结束时间
	 */
	public String JSSJ;

}
