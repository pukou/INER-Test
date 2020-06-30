package com.bsoft.mob.ienr.model.healthguid;

import java.util.ArrayList;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuidType {

    /*
    类型编号
     */
    public String LXBH;

    /*
    样式序号
    表单类型特有字段
     */
    public String YSXH;

    /*
    描述
     */
    public String MS;

    /*
    是否选中 默认不选中
     */
    public String ISCHECK = "0";

    /*
    宣教项目
     */
    public ArrayList<HealthGuidDetail> HealthGuidDetails;
}
