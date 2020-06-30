package com.bsoft.mob.ienr.model.nurserecord;

import java.io.Serializable;

/**
 * Describtion:护理记录签名数据
 * Created: dragon
 * Date： 2016/11/28.
 */
public class SignatureDataRequest implements Serializable{

    private static final long serialVersionUID = -8567320282612483192L;
    /**
     * 用户ID
     */
    public String YHID;

    /**
     * 记录编号
     */
    public String JLBH;

    /**
     * 机构ID
     */
    public String JGID;
}
