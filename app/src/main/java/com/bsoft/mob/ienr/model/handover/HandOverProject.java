package com.bsoft.mob.ienr.model.handover;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 交接单项目
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-14
 * Time: 15:30
 * Version:
 */
public class HandOverProject implements Serializable {

    /**
     * 项目标识
     */
    public String XMBS;

    /**
     * 样式分类
     */
    public String YSFL;

    /**
     * 项目名称
     */
    public String XMMC;

    /**
     * 分类序号
     */
    public String FLXH;

    /**
     * 样式序号
     */
    public String YSXH;

    /**
     * 使用范围
     * 1: 交接前
     * 2: 交接后
     * 3: 两者都要
     */
    public String SYFW;

    /**
     * 前置文本
     */
    public String QZWB;

    /**
     * 后置文本
     */
    public String HZWB;

    /**
     * 显示长度
     */
    public String XSCD;

    /**
     * 操作类型
     * 1,手工输入
     * 2,单项选择
     * 3,多项选择
     * 4.下拉选择
     */
    public String CZLX;

    /**
     * 下拉列表
     */
    public String XLLB;

    /**
     * 数据类型
     */
    public String SJLX;

    /**
     * 对照类型
     */
    public String DZLX;

    /**
     * 对照表单
     */
    public String DZBD;

    /**
     * 对照表单名称
     */
    public String DZBDMC;

    /**
     * 对照项目
     */
    public String DZXM;

    /**
     * 对照项目名称
     */
    public String DZXMMC;

    /**
     * 特殊项目
     */
    public String TSXM;

    /**
     * 换行标志
     */
    public String HHBZ;

    /**
     * 作废标志
     */
    public String ZFBZ;

    /**
     * 排列顺序
     */
    public String PLSX;

    /**
     * 选项列表
     */
    public List<HandOverOption> HandOverOptionList;

    /**
     * 是否选中
     */
    public boolean ISSELECT = false;

    /**
     * 是否修改
     */
    public boolean ISMODIFY = false;

    /**
     * 是否相同 - 核对方专用属性
     */
    public boolean ISDIFFERENT = false;


    /**************记录数据****************/

    /**
     * 记录项目
     */
    public String JLXM;

    /**
     * 记录序号
     */
    public String JLXH;

    /**
     * 样式项目
     */
    public String YSXM;

    /**
     * 样式分类
     */
    public String JJQH;

    /**
     * 项目汇总
     */
    public String XMHZ;

}
