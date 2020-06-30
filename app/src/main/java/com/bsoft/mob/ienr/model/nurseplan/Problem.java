/**
 * @Title: Problem.java
 * @Package com.bsoft.mob.ienr.model.nurseform
 * @Description: 护理计划问题
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-30 下午3:22:28
 * @version V1.0
 */
package com.bsoft.mob.ienr.model.nurseplan;

import java.util.List;

import com.bsoft.mob.ienr.model.SelectResult;
import com.bsoft.mob.ienr.model.SyncRecord;

/**
 * @ClassName: Problem
 * @Description: 护理计划问题
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-11-30 下午3:22:28
 *
 */
public class Problem {
    public String JLWT;

    public String WTXH;

    public String GLXH;

    public String WTMS;

    public String WTLX;

    public String XGYS;

    public String YSWS;

    public String KSSJ;

    public List<RelevantFactor> XGYSList;

    public List<DiagnosticBasis> ZDYJ;

    public List<Goal> JHMB;

    public List<Measure> JHCS;

    public List<Measure> JHCSTemplate;

    public String PJZT;

    public boolean IsSync = false;

    public SelectResult SyncData;
}
