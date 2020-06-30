package com.bsoft.mob.ienr.model.blood;

import java.io.Serializable;

/**
 * 获取输血列表返回的业务类
 *
 * @author hy
 */
public class BloodTransfusionInfo implements Serializable {

    private static final long serialVersionUID = 4936248525807551738L;

    /**
     * 申请单号
     */
    public String SQDH;

    /**
     * 输血单号
     */
    public String SXDH;

    /**
     * 血液类型
     */
    public String XYLX;

    /**
     * 血液名称
     */
    public String MC;

    /**
     * 血袋号
     */
    public String XDH;

    /**
     * 血袋序号
     */
    public String XDXH;

    /**
     * 血型
     */
    public String ABO;

    /**
     * 阴型
     */
    public String RH;

    /**
     * 血量
     */
    public String XL;

    /**
     * 包装单位
     */
    public String BAOZHUANG;

    /**
     * 预约日期
     */
    public String YYRQ;

    /**
     * 到期日期
     */
    public String DQRQ;

    /**
     * 交差配血结果
     */
    public String PXFF;

    /**
     * 住院号码 BYH
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
     * 病人年龄
     */
    public String BRNL;

    /**
     * 病人床号
     */
    public String BRCH;

    /**
     * 输血人1 执行人
     */
    public String SXR1;

    /**
     * 输血人2 核对人
     */
    public String SXR2;

    /**
     * 输血时间
     */
    public String SXSJ;

    /**
     * 结束人
     */
    public String JSR;


    /**
     * 结束时间
     */
    public String JSSJ;

    /**
     * 结束时间
     */
    public String KSSJ;

    /**
     * 输血判别
     */
    public String SXPB;

    /**
     * 上交判别
     */
    public String SJPB;

    /**
     * 回收判别
     */
    public String HSPB;

}
