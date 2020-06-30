package com.bsoft.mob.ienr.model.advice;

import java.util.List;

/**
 * Description: 输液单
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-19
 * Time: 17:23
 * Version:
 */
public class PlanAndTransfusion {

    /**
     * 输液单号
     */
    public String SYDH;


    /**
     * 输液状态
     * 0 未执行
     * 1 已经执行
     * 2 正在执行
     * 4 暂停
     */
    public String SYZT;

    public List<PlanInfo> planInfoList;

}
