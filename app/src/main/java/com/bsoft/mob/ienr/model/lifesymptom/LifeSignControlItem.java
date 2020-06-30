package com.bsoft.mob.ienr.model.lifesymptom;

import android.text.TextUtils;

import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-10-26
 * Time: 14:32
 * Version:
 */
public class LifeSignControlItem implements Serializable {
    private static final long serialVersionUID = 564751681705080572L;

    //控件号
    public String KJH;

    //输入项号
    public String SRXH;

    //控件类型(1：显示控件；2：输入控件；3：活动控件；4：下拉控件 5：特殊控件)
    public String KJLX;

    //控件长度-字符长度
    public String KJCD;

    //控件内容
    public String KJNR;

    //数字输入
    public String SZSR;

    //其它输入
    public String QTSR;

    //正常下线
    public String ZCXX;

    //正常上线
    public String ZCSX;

    //监控下线
    public String JKXX;

    //监控上线
    public String JKSX;

    //非法下线
    public String FFXX;

    //非法上线
    public String FFSX;

    //顺序号
    public String SXH;

    //显示类别
    public String XSLB;

    //体征项目号
    public String TZXM;

    //控件说明
    public String KJSM;

    //特殊标识
    public String TSBZ;

    //下拉选择项目
    public List<LifeSignOptionItem> LifeSignOptionItemList;

    //ADD 2018-4-24 10:54:51
    public String XMMC;
    public String XMDW;

    public boolean isMaxMinAble() {
        /*
        升级编号【56010050】============================================= start
        PB端上下限维护成空字符串时候，PDA端输入项输入后崩溃
        ================= Classichu 2017/11/13 14:23
        */
        return !EmptyTool.isBlank(ZCXX) || !EmptyTool.isBlank(ZCSX) || !EmptyTool.isBlank(FFXX) || !EmptyTool.isBlank(FFSX);
        /* =============================================================== end */
    }

    /**
     * 1-5
     *
     * @param input
     * @return
     */
    public int getMaxMinStatue(float input) {
           /*
        升级编号【56010050】============================================= start
        PB端维护上下限维护成空字符串时候，PDA端输入项输入后崩溃
        ================= Classichu 2017/11/13 14:23
        */
        float ffxx = !EmptyTool.isBlank(FFXX) ? Float.parseFloat(FFXX) : -1;
        float ffsx = !EmptyTool.isBlank(FFSX) ? Float.parseFloat(FFSX) : -1;
        float zcxx = !EmptyTool.isBlank(ZCXX) ? Float.parseFloat(ZCXX) : -1;
        float zcsx = !EmptyTool.isBlank(ZCSX) ? Float.parseFloat(ZCSX) : -1;
        /* =============================================================== end */
        //小于最小值
        if (ffxx != -1 && input < ffxx) {
            return 1;
        }
        //大于最大值
        if (ffsx != -1 && input > ffsx) {
            return 5;
        }
        // 在 最小值 和正常小值之间
        if (ffxx != -1 && zcxx != -1 && input > ffxx && input <= zcxx) {
            return 2;
        }
        // 在 最大值 和正常大值之间
        if (ffsx != -1 && zcsx != -1 && input <= ffsx && input > zcsx) {
            return 4;
        }
        return 3;
    }
}
