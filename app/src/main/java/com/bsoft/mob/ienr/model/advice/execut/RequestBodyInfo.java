package com.bsoft.mob.ienr.model.advice.execut;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 医嘱执行请求对象
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-27
 * Time: 15:51
 * Version:
 */
public class RequestBodyInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 住院号
     */
    public String ZYH;

    /**
     * 用户id
     */
    public String YHID;

    /**
     * 输液并行
     */
    public boolean SYBX;

    /**
     * 是否校验时间
     */
    public boolean JYSJ;

    /**
     * 是否强制结束
     */
    public boolean QZJS;

    /**
     * 计划数据
     */
    public List<PlanArgInfo> PlanArgInfoList;

    /**
     * 条码内容
     */
    public String TMNR;

    /**
     * 条码前缀
     */
    public String TMQZ;

    /**
     * 确认单号
     */
    public String QRDH;

    /**
     * 需要接瓶的确认单号
     */
    public String JPQRDH;

    /**
     * 是否校验时间
     */
    public String JGID;

    /*
        升级编号【56010053】============================================= start
        多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
        ================= Classichu 2017/11/14 16:25

        */
    public String transfuse_sp_sydh;
    /* =============================================================== end */
    public InArgument inArgument;
    //扩展
    public String core;
}
