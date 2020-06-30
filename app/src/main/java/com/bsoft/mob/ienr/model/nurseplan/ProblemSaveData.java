package com.bsoft.mob.ienr.model.nurseplan;

import java.io.Serializable;

/**
 * Description: 护理计划保存数据对象
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-15
 * Time: 13:45
 * Version:
 */
public class ProblemSaveData implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;

    public Problem Problem;//护理计划问题

    public String ZYH;//住院号

    public String YHID;//用户id

    public String GLLX;//归类类型

    public String BQID;//病区id

    public String JGID;//机构id

    // add
    public String GLJL;//GLJL
    public String JLGLLX;//JLGLLX

}
