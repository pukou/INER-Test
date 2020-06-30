package com.bsoft.mob.ienr.model.bloodglucose;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 血糖记录
 * User: 苏泽雄
 * Date: 16/12/23
 * Time: 16:00:35
 */
public class BloodGlucoseRecord implements Serializable {

	private static final long serialVersionUID = 1825742004127275081L;



	// 待执行记录
	public List<BloodGlucoseDetail> DETAILS;

	// 已执行记录
	public List<BloodGlucoseDetail> HISTORYS;
}
