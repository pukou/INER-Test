package com.bsoft.mob.ienr.model.clinicalevent;

import java.util.List;

/**
 * Description: 临床事件类型
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-08
 * Time: 11:29
 * Version:
 */

public class ClinicalEventType {

    /*
    临床事件类型值
    0 1 2 3
     */
    public String TypeValue;

    /*
    临床事件类型名称
    自定义 分娩 死亡 手术
     */
    public String TypeName;

    /*
    数量
     */
    public String Count;

    /*
    临床事件
     */
    public List<ClinicalEventInfo> ClinicalEventInfoList;
}
