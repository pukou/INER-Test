package com.bsoft.mob.ienr.model.nurseplan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Classichu on 2017/5/5.
 */
public class CSMS_DataWrapper implements Serializable{


    public CSMS_DataWrapper(List<CSMS_Bean> csms_beanList) {
        this.csms_beanList = csms_beanList;
    }

    public List<CSMS_Bean> getCsms_beanList() {
        return csms_beanList;
    }

    private List<CSMS_Bean> csms_beanList;

    public static class CSMS_Bean implements Serializable{
        public String JLCS;
        public String JLWT;
        public  String CSXH;
        public  String CSMS;
        public String KSSJ;
        public String KSGH;
        public int ZDYBZ;
        public int XJBZ;
        public String JSSJ;
        public String JSGH;
        public String CSZH;
    }
}
