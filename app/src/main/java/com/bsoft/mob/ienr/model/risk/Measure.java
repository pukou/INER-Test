/**   
 * @Title: Measure.java 
 * @Package com.bsoft.mob.ienr.model.risk 
 * @Description: 风险措施项目 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-14 下午2:24:34 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.risk;

/**
 * @ClassName: Measure
 * @Description: 风险措施项目
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-14 下午2:24:34
 * 
 */
public class Measure {

	//记录项目
	public String JLXM;

	//措施序号
	public String CSXH;

	//自定义标志  0 否  1 是
	public String ZDYBZ;

	//项目内容
	public String XMNR;

	//组名称
	public String ZMC;

	//是否选择
	public boolean SELECT;

	//必填标志
	public boolean BTBZ;

	//项目组号
	public String XMZH;
}
