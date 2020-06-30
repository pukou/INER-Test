package com.bsoft.mob.ienr.model.clinicalevent;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-12-08
 * Time: 11:30
 * Version:
 */

public class ClinicalEventInfo {

    /*
    事件序号
     */
    public String SJXH;

    /*
    事件归属 统一为3 护理
     */
    public String SJGS;

    /*
    就诊序号
     */
    public String JZXH;

    /*
    就诊号码
     */
    public String JZHM;

    /*
    事件分类
     */
    public String SJFL;

    /*
    发生时间
     */
    public String FSSJ;

    /*
    自定义事件描述
     */
    public String SJMS;

    /*
    记录时间
     */
    public String JLSJ;

    /*
    记录人 工号
     */
    public String JLGH;

    /*
    记录人 名称
     */
    public String JLR;

    /*
    系统标识
    0 非 1 是
     */
    public String XTBZ;

    /*
    机构id
     */
    public String JGID;

    /*
    是否修改
     */
    public boolean MODIFIED;

    /*
    数据库类型
     */
    public String dbtype;
}
