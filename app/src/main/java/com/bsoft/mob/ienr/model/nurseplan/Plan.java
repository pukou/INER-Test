/**   
 * @Title: Plan.java 
 * @Package com.bsoft.mob.ienr.model.nurseform 
 * @Description: 护理计划
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-19 上午11:21:24 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.nurseplan;

import java.util.ArrayList;

/**
 * @ClassName: Plan
 * @Description: 护理计划
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-11-19 上午11:21:24
 * 
 */
public class Plan {

	public String XH;//序号

	public String LXBH;

	public String GLLX;

	public String MS;
	
	public String WTLX;//1计划2焦点  问题类型

	public ArrayList<SimpleRecord> SimpleRecord;
}
