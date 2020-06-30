package com.bsoft.mob.ienr.model.lifesymptom;

import java.util.List;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-02
 * Time: 10:14
 * Version:
 */
public class LifeSignSaveDataItem {
    private static final long serialVersionUID = 564751681705080572L;
    //体征项目
    public String TZXM;
    //要保存的数据
    public String Data;
    //异常标志的值：-2，-1，0，1，2
    public String YCBZ;

    public List<LifeSignSaveDataTerm> lifeSignSaveDataTermList;

}
