package com.bsoft.mob.ienr.model.blood;

import java.io.Serializable;

/**
 * Description: 输血巡视相关信息
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-29
 * Time: 14:43
 * Version:
 */
public class BloodTransfusionTourInfo implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;

    //输血单号
    public String SXDH;

    //巡视工号
    public String XSGH;

    //巡视姓名
    public String XSXM;

    //巡视日期
    public String XSRQ;

    //输血速度
    public String SXSD;

    //不良反应 1有 0无
    public String BLFY;

    //不良反应名称
    public String FYMC;

    //备注信息
    public String BZ;

    //巡视次数
    public String XSCS;

    //巡视次数
    public String JGID;

    //操作类型 0：新增 1：修改  2：删除
    public String OperType;

    //数据库类型
    public String dbtype;
}
