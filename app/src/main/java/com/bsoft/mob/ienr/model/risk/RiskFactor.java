/**   
* @Title: RiskFactor.java 
* @Package com.bsoft.mob.ienr.model.risk 
* @Description: 风险因子 
* @author 吕自聪  lvzc@bsoft.com.cn
* @date 2015-12-9 下午1:12:35 
* @version V1.0   
*/ 
package com.bsoft.mob.ienr.model.risk;

import java.util.List;

/** 
 * @ClassName: RiskFactor 
 * @Description: 风险因子
 * @author 吕自聪 lvzc@bsoft.com.cn 
 * @date 2015-12-9 下午1:12:35 
 *  
 */
public class RiskFactor {

    // 风险因子
	public String FXYZ;

    // 评估单号
    public String PGDH;

    // 因子描述
    public String YZMS;

    // 单选标识  0:多选  1:单选
    public String DXBZ;
    //必选标志
    public String BXBZ;

    // 因子评分
    public List<FactorGoal> YZPF;
}
