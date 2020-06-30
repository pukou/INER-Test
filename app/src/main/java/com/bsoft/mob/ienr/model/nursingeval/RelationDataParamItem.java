package com.bsoft.mob.ienr.model.nursingeval;

public class RelationDataParamItem {
    /**
     * 关联项目号（其他文书项目或类型号）：如：体征项目XMH ; 风险评估 YSLX
     */
    public String GLXMH ;
    /**
     * 评估项目：与其他文书项目所关联的评估项目号或此评估项目号对应的前端控件ID值，方便数据写入
     */
    public String PGXMH ;
    /**
     * 此评估项目的控件类型，方便数据写入(web 用)
     */
    public String XMKJLX;
    /**
     * 第三种关联数据策略(当前创建的记录)：通过记录获取此评估项目的值
     */
    public String DZBDJL;
}
