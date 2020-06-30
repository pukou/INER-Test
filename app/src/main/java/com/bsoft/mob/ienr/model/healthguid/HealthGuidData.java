package com.bsoft.mob.ienr.model.healthguid;

import com.bsoft.mob.ienr.model.SelectResult;
import com.bsoft.mob.ienr.model.SyncRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuidData implements Serializable {

    /*
    记录序号
     */
    public String XH = "0";

    /*
    归类序号：样式序号/归类序号
     */
    public String GLXH = "0";

    /*
    操作的数据类型（预留字段） 1：添加 2：修改 9：其他模块引用健康宣教模块
     */
    public String OperType = "1";

    /*
    数据类型 1：表单 2：分类
     */
    public String GLLX = "1";

    /*
    记录时间
     */
    public String JLSJ;

    /*
    记录工号
     */
    public String JLGH;

    /*
    签名工号
     */
    public String QMGH;

    /*
    用户参数：宣教独立评价
     */
    public String XJDLPJ;

    //项目内容
    public String XMNR;

    /*
    宣教类别列表
     */
    public ArrayList<HealthGuidType> HealthGuidTypes;

    /*
    宣教的默认操作（对象，方式，评价等信息）
     */
    public ArrayList<HealthGuidOper> HealthGuidDefaultOpers;

    public boolean IsSync = false;

    public SelectResult SyncData;
}
