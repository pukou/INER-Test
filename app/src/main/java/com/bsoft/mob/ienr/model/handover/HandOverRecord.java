package com.bsoft.mob.ienr.model.handover;

import java.io.Serializable;

/**
 * Description: 交接单记录 - 记录
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-14
 * Time: 17:15
 * Version:
 */
public class HandOverRecord implements Serializable {

    /**
     * 记录序号
     */
    public String JLXH;

    /**
     * 住院号
     */
    public String ZYH;

    /**
     * 住院号码
     */
    public String ZYHM;

    /**
     * 病人姓名
     */
    public String BRXM;

    /**
     * 病人性别
     */
    public String BRXB;

    /**
     * 病人床号
     */
    public String BRCH;

    /**
     * 病人病区
     */
    public String BRBQ;

    /**
     * 当前诊断
     */
    public String DQZD;

    /**
     * 样式序号
     */
    public String YSXH;

    /**
     * 样式类型
     */
    public String YSLX;

    /**
     * 接受病区
     */
    public String JSBQ;

    /**
     * 填写时间
     */
    public String TXSJ;

    /**
     * 填写工号
     */
    public String TXGH;

    /**
     * 记录时间
     */
    public String JLSJ;

    /**
     * 记录工号
     */
    public String JLGH;

    /**
     * 接收时间
     */
    public String JSSJ;

    /**
     * 接收工号
     */
    public String JSGH;

    /**
     * 打印次数
     */
    public String DYCS;

    /**
     * 交接内容
     */
    public String JJNR;

    /**
     * 状态标志
     * 1: 发启科室填写完成
     * 2: 接收科室填写完成
     */
    public String ZTBZ;

    /**
     * 作废标志
     */
    public String ZFBZ;

    /**
     * 机构id
     */
    public String JGID;

    /**
     * 交接单模板
     */
    public HandOverForm HandOverFormTemplate;

    /**
     * 交接前-表单
     */
    public HandOverForm HandOverFormBefore;

    /**
     * 交接后-表单
     */
    public HandOverForm HandOverFormAfert;

    public String dbtype;
}
