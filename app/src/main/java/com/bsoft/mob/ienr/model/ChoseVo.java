package com.bsoft.mob.ienr.model;

import com.bsoft.mob.ienr.Constant;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-13 下午4:53:30
 * @类说明 选择控件，数据对象
 */
public class ChoseVo {


    public String name;
    public int index;
    public String lxh;
    public final static String LXH_DEFAULT = "DEFAULT";
//    public final static String LXH_OTHERS = "OTHERS";
//    public final static String LXH_zhiLiao = "25";//福建协和 25 是治疗
    public final static String LXH_zhiLiao = Constant.DEFAULT_STRING_NEGATIVE;//业务里没有负数，变相不启用该功能

    public ChoseVo(String name, int index) {
        this.name = name;
        this.index = index;
        this.lxh = LXH_DEFAULT;
    }

    public ChoseVo(String name, int index, String lxh) {
        this.name = name;
        this.index = index;
        this.lxh = lxh;
    }
}
