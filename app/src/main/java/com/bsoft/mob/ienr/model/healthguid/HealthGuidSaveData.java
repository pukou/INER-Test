package com.bsoft.mob.ienr.model.healthguid;

import java.io.Serializable;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-09
 * Time: 14:58
 * Version:
 */

public class HealthGuidSaveData implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;

    //住院号
    public String ZYH;

    //病区id
    public String BQID;

    //机构id
    public String JGID;

    public HealthGuidData HealthGuidData;
}
