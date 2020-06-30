package com.bsoft.mob.ienr.model.healthguid;

import java.util.ArrayList;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuidEvaluateData {

    /*
    记录序号
     */
    public String XH = "0";

    /*
    归类序号：样式序号/归类序号
     */
    public String GLXH = "0";

    /*
    数据类型 1：表单 2：分类
     */
    public String GLLX = "1";

    /*
    宣教类别列表
     */
    public ArrayList<HealthGuidType> HealthGuidTypes;

    /*
    宣教的默认操作（对象，方式，评价等信息）
     */
    public ArrayList<HealthGuidOper> HealthGuidDefaultOpers;
}
