/**   
 * @Title: FactorGoal.java 
 * @Package com.bsoft.mob.ienr.model.risk 
 * @Description: 因子评分
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-9 下午1:13:52 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.risk;

/**
 * @ClassName: FactorGoal
 * @Description: 因子评分
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-9 下午1:13:52
 * 
 */
public class FactorGoal {

	// 明细序号 风险评估明细表主键
	public String MXXH;

	// 分值序号 风险评估因子表主键
	public int FZXH;

	// 风险因子
	public String FXYZ;

	// 输入标志 是选择还是手工输入类型  0:非输入  1:输入
	public String SRBZ;

	// 评分分值
	public String PFFZ;

	// 分值描述
	public String FZMS;

	// 分值上限
	public String FZSX;

	// 分值下限
	public String FZXX;

	// 是否选择
	public boolean SELECT;

	public String BZXX;

}
