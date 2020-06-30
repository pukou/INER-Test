package com.bsoft.mob.ienr.model.advice.execut;

/**
 * Description: 计划传入参数对象
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-20
 * Time: 17:17
 * Version:
 */
public class PlanArgInfo {

    /**
     * 计划号
     */
    public String JHH;
    public String YZZH;

    /**
     * 费用序号
     */
    public String FYXH;

    /**
     * 短语序号
     */
    public String DYXH;

    /**
     * 计划时间
     */
    public String JHSJ;

        /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
    /**
     * 归属类型
     * 0 空
     * 1 护理治疗
     * 2 标本采样
     * 3 口服用药
     * 4 静脉输液
     * 5 注射用药
     * 6 自理用药
     * 7 体征采集
     * 9 其它医嘱
     */
    public int GSLX;
    /**
     * 执行状态
     * -1 空
     * 0 未执行
     * 1 已执行
     * 2 执行中
     * 3 作废
     * 4 暂停中
     * 5 已拒绝
     */
    public int ZXZT;
    //6 静推
    public String  YPYF;
    /* =============================================================== end */
}
