package com.bsoft.mob.ienr.model.healthguid;

import java.util.ArrayList;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuidClassifyData {

    /*
    宣教项目归类序号
     */
    public String XH;

    /*
    宣教项目归类序号
     */
    public String GLXH = "0";

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
    宣教类别列表
     */
    public ArrayList<HealthGuidType> HealthGuidTypes;
}
