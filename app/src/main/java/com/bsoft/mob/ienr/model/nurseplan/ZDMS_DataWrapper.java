package com.bsoft.mob.ienr.model.nurseplan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Classichu on 2017/5/5.
 */
public class ZDMS_DataWrapper implements Serializable{

    private List<ZDMS_Bean> zdms_beanList;

    public ZDMS_DataWrapper(List<ZDMS_Bean> zdms_beanList) {
        this.zdms_beanList = zdms_beanList;
    }

    public List<ZDMS_Bean> getZdms_beanList() {
        return zdms_beanList;
    }

    public static class ZDMS_Bean implements Serializable{
        public  String ZDXH;
        public  String ZDMS;
    }
}
