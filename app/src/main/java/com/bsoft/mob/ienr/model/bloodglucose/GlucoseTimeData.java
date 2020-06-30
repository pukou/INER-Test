package com.bsoft.mob.ienr.model.bloodglucose;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 血糖时间点传输数据
 * User: 苏泽雄
 * Date: 16/12/28
 * Time: 10:14:50
 */
public class GlucoseTimeData implements Serializable {

	private static final long serialVersionUID = 8338076740219125623L;

	// 血糖时间点
	public List<GlucoseTime> GLUCOSETIME;

	// 胰岛素时间点
	public List<GlucoseTime> INSULINTIME;
}
