package com.bsoft.mob.ienr.model.nursingeval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Classichu on 2017/11/29.
 */
public class NursingEvaluateItem {
    public String XMXH;

    public String BBH;

    public String YSXH;

    public String PLSX;

    public String GLXM;

    public String XMMC;

    public String PYDM;

    public String WBDM;

    public String QZWB;

    public String HZWB;

    public String XSCD;
    /**
     * MOB_XTPZ.DMLB=467
     * 1.分类标签
     * 2.项目标签
     * 3.基本信息项目
     * 4.签名项目
     * @deprecated 5.组合项目
     * 6.数据关联项目
     * 7.常规项目
     */
    public String XMLB;
    /**
     * MOB_XTPZ.DMLB=468
     * 1,单行输入
     * 2.多行输入
     * 3,单项选择
     * 4,多项选择
     * 5.下拉列表
     * 6.标签显示
     * 7.表格
     * 9.无
     */
    public String XJKJLX;
    /**
     * MOB_XTPZ.DMLB=421
     * 1.数字
     * 2.字符
     * 3.日期
     */
    public String SJLX;

    public String XMKZ;

    public String SJGS;

    public String SJXX;

    public String SJSX;

    public String XMZH;

    public String HHBZ;

    public String SJXM;
    /**
     * 0 分类标签
     * 1 分类标签下的第一级项目
     * 2 第一级项目下的下级项目
     * 3,4,5...以此类推
     */
    public String XMJB;

    public String XJZK;

    public String XJHC;

    public String XMBM;

    public String PDAXS;

    public String XGBZ;

    public String ZDXM;

    public String BTXM;

    public String ZXBZ;


    ////
    public String XMQZ;
    public Map<String,String> childValueMap=new HashMap<>();
    public boolean childViewIsGone;
    /**
     * MOB_XTPZ.DMLB=468
     * 1,单行输入
     * 2.多行输入
     * 3,单项选择
     * 4,多项选择
     * 5.下拉列表
     * 6.标签显示
     * 7.表格
     * 9.无
     */
    public String KJLX;
    public String SJXMMC;
    public String status;
    public List<KeyValue<String, String>> XMXXKeyValueList;
    public List<NursingEvaluateItem> childItems;
}

