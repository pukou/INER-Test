package com.bsoft.mob.ienr.model.inspection;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank   E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-25 上午11:30:57
 * @类说明  检查
 */
public class ExamineVo extends BaseVo {
	/// YBHM 样本号码
    /// MZHM 门诊号码
    /// ZYHM 住院号码
    /// JCLX 检查类型 1 放射 0 超声
    /// JCMC 检查名称
    /// BWMS 部位描述
    /// DJGH 登记工号
    /// DJSJ 登记时间
    /// JCYS 检查医生
    /// JCSJ 检查时间
    /// BGYS 报告医生
    /// BGSJ 报告时间
    /// SHYS 审核医生
    /// SHSJ 审核时间
    /// JCYSXM 检查医生姓名
	/**
	 * 样本号码
	 */
	public String YBHM;
	/**
	 * 门诊号码
	 */
	public String MZHM;
	/**
	 * 住院号码
	 */
	public String ZYHM;
	/**
	 * 检查类型 1 放射 0 超声
	 */
	public String JCLX;
	/**
	 * 检查名称
	 */
	public String JCMC;
	/**
	 * 部位描述
	 */
	public String BWMS;
	/**
	 * 登记工号
	 */
	public String DJGH;
	/**
	 * 登记时间
	 */
	public String DJSJ;
	/**
	 * 检查医生
	 */
	public String JCYS;
	/**
	 * 检查时间
	 */
	public String JCSJ;
	/**
	 * 报告医生
	 */
	public String BGYS;
	/**
	 * 报告时间
	 */
	public String BGSJ;
	/**
	 * 审核医生
	 */
	public String SHYS;
	/**
	 * 审核时间
	 */
	public String SHSJ;
	/**
	 * 报告医生姓名
	 */
	public String BGYSXM;
}
