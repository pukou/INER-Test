package com.bsoft.mob.ienr.model.advice;

import java.util.List;

/**
 * Description: 医嘱计划
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-19
 * Time: 16:36
 * Version:
 */
public class PlanInfo {

    /**
     * 计划号
     */
    public String JHH;

    /**
     * 计划号列表
     */
    public List<String> JHHList;

    /**
     * 关联计划号
     */
    public String GLJHH;

    /**
     * 医嘱序号
     */
    public String YZXH;

    /**
     * 医嘱组号
     */
    public String YZZH;

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
    public String GSLX;

    /**
     * 医生医嘱编号
     */
    public String YSYZBH;

    /**
     * 项目类型
     */
    public String XMLX;

    /**
     * 项目序号
     */
    public String XMXH;

    /**
     * 医嘱主项
     */
    public String YZZX;

    /**
     * 医嘱名称
     */
    public String YZMC;

    /**
     * 药品用法
     */
    public String YPYF;
    public String YPYFMC;
    /**
     * 使用频次
     */
    public String SYPC;

    /**
     * 一次剂量
     */
    public String YCJL;

    /**
     * 剂量单位
     */
    public String JLDW;

    /**
     * 一次数量
     */
    public String YCSL;

    /**
     * 数量单位
     */
    public String SLDW;

    /**
     * 临时医嘱
     */
    public String LSYZ;

    /**
     * 计划日期
     */
    public String JHRQ;

    /**
     * 计划时点
     */
    public String JHSD;

    /**
     * 周期日期
     */
    public String ZQRQ;

    /**
     * 产生时间
     */
    public String CSSJ;

    /**
     * 计划时间
     */
    public String JHSJ;

    /**
     * 时间编号
     */
    public String SJBH;

    /**
     * 时间名称
     */
    public String SJMC;

    /**
     * 确认标志
     */
    public String QRBZ;

    /**
     * 确认单号
     */
    public String QRDH;

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
    public String ZXZT;

    /**
     * 开始执行时间
     */
    public String KSSJ;

    /**
     * 开始执行工号
     */
    public String KSGH;

    /**
     * 结束执行时间
     */
    public String JSSJ;

    /**
     * 结束执行工号
     */
    public String JSGH;

    /**
     * 开始执行核对时间
     */
    public String KSHDSJ;

    /**
     * 开始执行核对工号
     */
    public String KSHDGH;

    /**
     * 结束执行核对时间
     */
    public String JSHDSJ;

    /**
     * 结束执行核对工号
     */
    public String JSHDGH;

    /**
     * 执行类型
     * 0 初始状态
     * 1 输液首瓶
     * 2 输液接瓶
     */
    public String ZXLX;

    /**
     * 执行终端
     * 新版本考虑添加：执行终端的唯一标识
     */
    public String ZXZD;

    /**
     * 终端类型
     * 1 PDA
     * 2 PC
     */
    public String ZDLX;

    /**
     * 住院号
     */
    public String ZYH;

    /**
     * 病人病区
     */
    public String BRBQ;

    /**
     * 类型号
     */
    public String LXH;

    /**
     * 双人核对标志
     */
    public String SRHDBZ;

    /**
     * 剂量信息
     */
    public String JLXX;

    /**
     * 数量信息
     */
    public String SLXX;

    /**
     * 作废标志 0未作废 1作废
     */
    public String ZFBZ;

    /**
     * 执行位置
     */
    public int ZXWZ;

    /**
     * 机构id
     */
    public String JGID;
    //路数标识
    public String LSBS;

    /**
     * 数据库类型
     */
    public String dbtype;
}
