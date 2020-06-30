package com.bsoft.mob.ienr.model.lifesymptom;

import java.util.List;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-02
 * Time: 10:14
 * Version:
 */
public class LifeSignSaveData {
    private static final long serialVersionUID = 564751681705080572L;

    //用户id
    public String URID;

    //病区id
    public String BQID;

    //住院号
    public String ZYH;

    //是否临时数据 0：非临时 1：临时
    public String IsTemp;

    //时间
    public String TempTime;

    //采集组号
    public String CJZH;

    //记录编号
    public String JLBH;

    //记录编号
    public String JGID;

    //要保存的数据对象列表
    public List<LifeSignSaveDataItem> lifeSignSaveDataItemList;

    public boolean customIsSync;
}
