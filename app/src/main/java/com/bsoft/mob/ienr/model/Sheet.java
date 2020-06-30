package com.bsoft.mob.ienr.model;

import java.io.Serializable;

/**
 * Describtion:表单
 * Created: dragon
 * Date： 2017/1/3.
 */
public class Sheet implements Serializable{
    private static final long serialVersionUID = -8041671909945648383L;
    public Sheet(){}
    public Sheet(String bdid,String bdmc){
        this.bdid = bdid;
        this.bdmc = bdmc;
    }
    /**
     * 表单ID
     */
    public String bdid;
    /**
     * 表单名称
     */
    public String bdmc;
}
