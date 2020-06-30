package com.bsoft.mob.ienr.model.handover;

import java.util.List;

/**
 * Description: 交接单记录 按照人进行分组
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-02-23
 * Time: 14:55
 * Version:
 */

public class BatchHandOverRecord {

    /**
     * 住院号
     */
    public String ZYH;

    /**
     * 病人姓名
     */
    public String BRXM;

    /**
     * 未核对数量
     */
    public int NotCheckCount;

    /**
     * 已核对数量
     */
    public int CheckCount;

    /**
     * 当前模板下的记录列表
     */
    public List<HandOverRecord> HandOverRecordList;

}
