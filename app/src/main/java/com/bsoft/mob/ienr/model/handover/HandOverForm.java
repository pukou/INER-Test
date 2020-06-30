package com.bsoft.mob.ienr.model.handover;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 交接单样式 - 模板
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-14
 * Time: 15:29
 * Version:
 */
public class HandOverForm implements Serializable {

    /**
     * 样式序号
     */
    public String YSXH;

    /**
     * 样式类型
     * 1 转科交接
     * 2 手术交接
     * 3 外出检查
     */
    public String YSLX;

    /**
     * 样式名称
     */
    public String YSMC;

    /**
     * 排列顺序
     */
    public String PLSX;

    /**
     * 启用标志
     */
    public String QYBZ;

    /**
     * 机构id
     */
    public String JGID;

    /**
     * 分类列表
     */
    public List<HandOverClassify> HandOverClassifyList;

    /**
     * 当前模板下的记录列表
     */
    public List<HandOverRecord> HandOverRecordList;
}
