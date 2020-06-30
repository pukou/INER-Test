package com.bsoft.mob.ienr.model.nurseplan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Classichu on 2017/5/5.
 */
public class FXPG_InfoDataWrapper implements Serializable{
    private List<FXPG_InfoData> FXPG_InfoDataList;

    public FXPG_InfoDataWrapper(List<FXPG_InfoData> FXPG_InfoDataList) {
        this.FXPG_InfoDataList = FXPG_InfoDataList;
    }

    public List<FXPG_InfoData> getFXPG_InfoDataList() {
        return FXPG_InfoDataList;
    }

    public static class FXPG_InfoData implements  Serializable{
        public  String WTXH;
        public  String GLLX;
        public  String GLXH;
        public  String WTMS;
    }
}
