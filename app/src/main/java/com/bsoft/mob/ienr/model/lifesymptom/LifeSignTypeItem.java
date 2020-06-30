package com.bsoft.mob.ienr.model.lifesymptom;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-10-26
 * Time: 14:11
 * Version:
 */
public class LifeSignTypeItem implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;

    //类别号
    public String LBH;

    //类别名称
    public String LBMC;

    public List<LifeSignInputItem> LifeSignInputItemList;

}
