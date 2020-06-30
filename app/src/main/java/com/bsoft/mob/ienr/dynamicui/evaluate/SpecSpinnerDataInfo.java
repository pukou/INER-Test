/**   
 * @Title: SpinnerDataInfo.java
 * @Package com.bsoft.mob.ienr.dynamicui.evaluate 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-21 下午4:22:25 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.dynamicui.evaluate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @ClassName: SpinnerDataInfo 福建协和客户化
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-21 下午4:22:25
 * 
 */

public class SpecSpinnerDataInfo {
	@JsonProperty(value = "DropData")
	public List<DropData> datas;

	public int ID;

	public String Text;

	public String ParentID;

	public String Value;

	public String ValueType;

	public String NewLine;

	public String CtrlType;

	public String Font;

	public String IsScored;

	public String Score;

	public String GroupId;

	public int IsSelected;

	public String FrontId;

	public String PostpositionId;

	public String Jfgz;

	public String Xxdj;

	public String Dzlx;

	public String Dzbd;

	public String Dzxm;

	public String Dzbdlx;

	public String Btbz;
}
