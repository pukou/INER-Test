/**   
 * @Title: RiskMeasure.java 
 * @Package com.bsoft.mob.ienr.model.risk 
 * @Description: 风险措施 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-15 上午8:58:12 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.risk;

import com.bsoft.mob.ienr.model.SelectResult;

import java.util.List;

/**
 * @ClassName: RiskMeasure
 * @Description: 风险措施
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-15 上午8:58:12
 * 
 */
public class RiskMeasure {

	//风险措施
	public MeasureRecord RECORD;

	//措施评价
	public List<RiskEvaluate> EVALUATE;

	//判断同步结果，是否需要处理
	public boolean IsSync = false;

	//同步数据
	public SelectResult SyncData;
}
