package com.bsoft.mob.ienr.model.handover;

import java.util.List;

/**
 * Description: 交接单分类
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-14
 * Time: 15:29
 * Version:
 */
public class HandOverClassify {

    /**
     * 样式分类
     */
    public String YSFL;

    /**
     * 样式序号
     */
    public String YSXH;

    /**
     * 分类名称
     */
    public String FLMC;

    /**
     * 分类级别
     * 1 一级分类
     * 2 二级分类（二级分类用于选项关联使用）
     */
    public String FLJB;

    /**
     * 排列顺序
     */
    public String PLSX;

    /**
     * 显示标志
     */
    public String XSBZ;

    /**
     * 作废标志
     */
    public String ZFBZ;

    /**
     * 项目列表
     */
    public List<HandOverProject> HandOverProjectList;

}
