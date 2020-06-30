package com.bsoft.mob.ienr.model.bloodglucose;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 血糖历史纪录传输数据
 * User: 苏泽雄
 * Date: 16/12/28
 * Time: 9:20:39
 */
public class BGHistoryData implements Serializable {

	private static final long serialVersionUID = -3975271312893593325L;

	public List<BloodGlucoseDetail> GLUCOSE;

	public List<BloodGlucoseDetail> INSULIN;
}
