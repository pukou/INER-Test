package com.bsoft.mob.ienr.model.advice;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 输液单获取的医嘱计划返回值，包括医嘱计划和拒绝理由
 * User: 苏泽雄
 * Date: 16/12/19
 * Time: 11:07:37
 */
public class AdvicePlanData implements Serializable {

	private static final long serialVersionUID = -2214564482603813219L;

	// 医嘱计划
	public List<AdvicePlanVo> PlanInfoList;

	// 拒绝理由
	public List<AdviceRefuseReasonVo> PhraseModelList;
	//
}
