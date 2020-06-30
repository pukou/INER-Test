package com.bsoft.mob.ienr.model.clinicalevent;

import java.io.Serializable;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-08
 * Time: 16:58
 * Version:
 */

public class ClinicalEventSaveData implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;

    public String ZYH;

    public String YHID;

    public String BQID;

    public String JGID;

    public ClinicalEventType ClinicalEventType;

}
