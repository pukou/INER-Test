package com.bsoft.mob.ienr.model.outcontrol;

import java.io.Serializable;

/**
 * 外出登记保存数据
 * Created by king on 2016/11/17.
 */
public class OutControlSaveData implements Serializable {

    private static final long serialVersionUID = 9081259405051039729L;


    //住院号
    public String ZYH;

    //病人病区
    public String BRBQ;

    //外出时间
    public String WCSJ;

    //
    public String WCYY;


    //外出登记护士
    public String WCDJHS;

    //预计回床时间
    public String YJHCSJ;

    //批准医生
    public String PZYS;

    //陪同人员
    public int PTRY;

    //机构id
    public String JGID;

    public int sysType;


}