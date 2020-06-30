package com.bsoft.mob.ienr.model.lifesymptom;

import java.io.Serializable;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-10-27
 * Time: 10:38
 * Version:
 */
public class LifeSignOptionItem implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;
    public String KJH;
    //选择号
    public String XZH;

    //选择内容
    public String XZNR;

    //特殊颜色
    public String TSYS;
}
