/**   
 * @Title: MeasureRecord.java 
 * @Package com.bsoft.mob.ienr.model.risk 
 * @Description: 风险措施记录 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-14 下午2:26:04 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.risk;

import java.util.List;

/**
 * @ClassName: MeasureRecord
 * @Description: 风险措施记录
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-14 下午2:26:04
 * 
 */
public class MeasureRecord {

    //记录序号
	public String JLXH;

    //表单序号-措施单号
    public String BDXH;

    //评估序号
    public String PGXH;

    //措施时间
    public String CSSJ;

    //措施工号
    public String CSGH;

    //护士姓名
    public String HSXM;

    //护士长签字
    public String HSZQM;

    //护士长姓名
    public String HSZXM;

    //护士长签名时间
    public String HSZQMSJ;

    //措施评价
    public String CSPJ;

    //病人病区
    public String BRBQ;

    //措施项目
    public List<Measure> CSXM;

    //是否启用评价
    public String SFPJ;
    //add 2018-05-02 20:33:56
//    public String ZGQK;

    //是否手动同步 by ling
    public boolean CustomIsSync;
}
