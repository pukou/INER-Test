package com.bsoft.mob.ienr.model.handover;

import java.io.Serializable;

/**
 * Description: 交接单选项
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-14
 * Time: 15:32
 * Version:
 */
public class HandOverOption implements Serializable {

    /**
     * 选项标识
     */
    public String XXBS;

    /**
     * 项目标识
     */
    public String XMBS;

    /**
     * 样式序号
     */
    public String YSXH;

    /**
     * 排列顺序
     */
    public String PLXS;

    /**
     * 选项名称
     */
    public String XXMC;

    /**
     * 显示长度
     */
    public String XSCD;

    /**
     * 换行标志
     */
    public String HHBZ;

    /**
     * 下级分类
     */
    public String XJFL;

    /**
     * 作废标志
     */
    public String ZFBZ;

    /**
     * 是否选中
     */
    public boolean ISSELECT = false;

    /**
     * 是否修改
     */
    public boolean ISMODIFY = false;


    /**************记录数据****************/

    /**
     * 记录选项
     */
    public String JLXX;

    /**
     * 记录项目
     */
    public String JLXM;

    /**
     * 记录序号
     */
    public String JLXH;

    /**
     * 样式选项
     */
    public String YSXX;

    /**
     * 选项内容
     */
    public String XXNR;

    /**
     * 对照类型
     */
    public String DZLX;

    /**
     * 对照表单记录
     */
    public String DZBDJL;


}
