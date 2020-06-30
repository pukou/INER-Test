package com.bsoft.mob.ienr.model.nurseplan;

import java.util.List;

/**
 * Description: 问题评价保存时用参数对象
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-17
 * Time: 10:23
 * Version:
 */
public class ProblemEvaluateSaveData {
    private static final long serialVersionUID = 564751681705080572L;

    public List<Evaluate> evaluateList;//评价列表

    public String JLWT;//记录问题

    public String WTXH;//问题序号

    public String ZYH;//住院号

    public String YHID;//用户id

    public String BQID;//病区id

    public String JGID;//机构id

}
