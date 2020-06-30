package com.bsoft.mob.ienr.model.advice.execut;

import com.bsoft.mob.ienr.model.SelectResult;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 医嘱执行返回结果
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-27
 * Time: 15:53
 * Version:
 */
public class ResponseBodyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public String TableName;
    public String Message;
    public List<REModel> REModelList;
    public List<SJModel> SJModelList;
    public List<KFModel> KFModelList;
    public List<ZSModel> ZSModelList;
    public List<SYZTModel> SYZTModelList;
    public List<SYModel> SYModelList;
    public List<SQModel> SQModelList;
	public Boolean IsSync = false;
	public SelectResult SyncData;
	//add 2018-4-18 20:30:40
    public InArgument inArgument;

}
