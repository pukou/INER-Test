package com.bsoft.mob.ienr.model.lifesymptom;

import java.io.Serializable;

/**
 * Created by Classichu on 2017-6-6.
 * //用于接受web端   返回的LifeSignRealSaveDataItem
 */
public class LifeSignRealSaveDataItem implements Serializable{
    //采集号
    public String CJH;

    //采集组号
    public String CJZH;

    //项目号
    public String XMH;

    //计划标志
    public String JHBZ;

    //采集时间
    public String CJSJ;

    //住院号
    public String ZYH;

    //病人科室
    public String BRKS;

    //病人病区
    public String BRBQ;

    //病人床号
    public String BRCH;

    //体征内容
    public String TZNR;

    //项目下标
    public String XMXB;

    //复测标志
    public String FCBZ;

    //复测关联
    public String FCGL;

    //体温单显示
    public String TWDXS;

    //记录时间
    public String JLSJ;

    //记录人员
    public String JLGH;

    //作废标志
    public String ZFBZ;

    //异常标志
    public String YCBZ;

    //备注信息
    public String BZXX;

    //记录编号
    public String JLBH;

    //机构id
    public String JGID;
}
