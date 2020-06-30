package com.bsoft.mob.ienr.model.inspection;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-25 上午11:30:57
 * @类说明 检验详情
 */
public class InspectionDetailVo extends BaseVo {

	/**
	 * 异常标志
	 */
	public int YCBZ;
	/**
	 * 项目ID
	 */
	public String XMID;
	/**
	 * 化验结果
	 */
	public String HYJG;
	/**
	 * 单位
	 */
	public String DW;
	/**
	 * 中文名称
	 */
	public String ZWMC;
	/**
	 * 样本号码
	 */
	public String YBHM;
	/**
	 * 下限
	 */
	public String CKXX;
	/**
	 * 上限
	 */
	public String CKSX;
	/**
	 * 结果提示
	 */
	public String JGTS;

}
