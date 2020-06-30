package com.bsoft.mob.ienr.model.healthguid;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuid implements Serializable {
    /*
        序号
    */
    public String XH;

    /*
    数量：单位是份
     */
    public String SL;

    /*
    描述
     */
    public String MS;

    /*
    数据类型 1：表单 2：分类
     */
    public String GLLX = "1";

    /*
    宣教项目
     */
    public ArrayList<HealthGuidItem> HealthGuidItems;
}
