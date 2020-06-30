package com.bsoft.mob.ienr.model.lifesymptom;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-10-26
 * Time: 14:18
 * Version:
 */
public class LifeSignInputItem implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;

    public String LBH;
    //输入项号
    public String SRXH;

    //输入项名
    public String SRXM;

    //输入顺序
    public String SRSX;

    //显示标志
    public String XSBZ;

    //换行标志
    public String HHBZ;

    public List<LifeSignControlItem> LifeSignControlItemList;

}
