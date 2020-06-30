/**   
 * @Title: QualityControl.java 
 * @Package com.bsoft.mob.ienr.model.risk 
 * @Description: 质控规则
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-10 下午1:37:23 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.risk;

/**
 * @ClassName: QualityControl
 * @Description: 质控规则
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-10 下午1:37:23
 * 
 */
public class QualityControl {

	// 质控描述
	public String ZKMS;

	// 分值上限
	public int FZSX;

	// 分值下限
	public int FZXX;

	// 措施标志  0:不需要提示填写措施  1:需要提示填写措施
	public String CSBZ;
}
