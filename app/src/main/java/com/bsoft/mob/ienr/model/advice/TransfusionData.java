package com.bsoft.mob.ienr.model.advice;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 输液单传输数据，包含输液单和输液明细
 * User: 苏泽雄
 * Date: 17/1/9
 * Time: 16:08:42
 */
public class TransfusionData implements Serializable {

	private static final long serialVersionUID = 6548032481077053191L;

	// 输液单
	public List<TransfusionVo> SYD;
	// 输液明细
	public List<TransfusionInfoVo> SYMX;
	// 输液巡视
	public List<TransfusionTourRecordVo> SYXS;
	// 输液反应
	public List<TransfusionTourReactionVo> SYFY;
}
