package com.bsoft.mob.ienr.model.nursingeval;

import java.util.ArrayList;
import java.util.List;

/*
* description:重新设计护理评估单：
* 1.支持无限级项目
* 2.支持自伸缩
* 业务关联数据请求入参
* create by: dragon xinghl@bsoft.com.cn
* create time:2017/11/20 11:19
* since:5.6 update1
*/
public class RelationDataParam {
    /**
     * 业务类别:对应评估单项目扩展 2|8 中前半部分信息
     * 2：风险评估 3：宣教 5：生命体征
     */
    public String YWLB ;
    /**
     * 评估样式序号
     */
    public String YSXH ;
    /**
     * 评估样式类型
     */
    public String YSLX ;
    /**
     * 填写评估单时间 yyyy-mm-dd hh:mi:ss 格式
     */
    public String TXSJ ;
    /**
     * 住院号
     */
    public String ZYH;
    /**
     * 机构ID
     */
    public String JGID;
    /**
     * 针对生命体征项目，第三种策略时，返回CJZH
     */
    public String CJZH;
    /**
     * 业务类别关联的明细项目
     */
    public List<RelationDataParamItem> YWLBMX = new ArrayList<>();

}
