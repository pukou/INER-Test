/**   
 * @Title: RiskOverview.java 
 * @Package com.bsoft.mob.ienr.model.risk 
 * @Description: 风险评估表单类型 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-8 下午3:59:30 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.risk;

import java.util.List;

/**
 * @ClassName: RiskOverview
 * @Description:风险评估表单类型
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-8 下午3:59:30
 * 
 */
public class RiskOverview {

	//评估单号
	public String PGDH;

	//评估单名称
	public String PGDMC;

	/**
	 * 评估描述：上次（）分，*度危险，填写频率，已是第几天
	 */
	public String PGMS;

	/**
	 * 填写计划：0：今日不许填写，1：今日可能要填写，2：今日必须填写
	 */
	public String TXJH;

	//评估类型
	public String PGLX;

	//评估记录
	public List<SimRiskRecord> PGJL;

	//提醒日期
	public String TXRQ;

}
