package com.bsoft.mob.ienr.model.nurserecord;

import java.io.Serializable;

/**
 * Describtion: 组件下拉数据
 * Created: dragon
 * Date： 2016/10/24.
 */
public class DropItem implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;

    public DropItem(){

    }

    public DropItem(String value,String xznr){
        this.VALUE = value;
        this.XZNR = xznr;
    }
    /**
     * 选择内容
     */
    public String XZNR;

    /**
     * 特殊颜色
     */
    public String TSYS;
    /**
     * 选择号
     */
    public String XZH;

    public String VALUE;
    /**
     * 默认值
     */
    public boolean MRZ;

    /**
     * 是否选中
     */
    public Boolean ISCHECK = false;
}
