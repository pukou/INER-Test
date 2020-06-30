package com.bsoft.mob.ienr.model.inspection;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank   E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-25 上午11:30:57
 * @类说明  检验
 */
public class InspectionVo extends BaseVo {
	/**
	 * 样本号码
	 */
	public String YBHM;
	/**
	 * 审核时间
	 */
	public String SHSJ;
	/**
	 * 项目名称
	 */
	public String XMMC;
	/**
	 * 异常标志 1异常 0 正常 异常此条记录为红色
	 */
	public int YCBZ;

}
