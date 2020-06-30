package com.bsoft.mob.ienr.model.trad;

import java.util.List;

/**
 * Created by Classichu on 2018/1/9.
 */

public class _SHFF {
    public String code;//ZZBH
    public String name;//ZZMC
    public List<SHFF_Check> shffCheckList;

    public String ZZBH;
    public String ZZMC;
    public String FAJL;
    public String ZZJL;


    public static class SHFF_Check {
        public String code;//JSBH
        public String name;//FFMC
        public String JSBH;
        public String FFMC;
        public String XMLB;
        public String BZXX;
        public String XGBZ;
        public String JCXMH;
        public String editable;
        public String status;
    }
}
