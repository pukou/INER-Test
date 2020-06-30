package com.bsoft.mob.ienr.model.catheter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/29.
 * 需测数据
 */
public class CatheterMeasurData {

    public String YZXH;

    //名称
    public String YZMC;

    public String YPXH;

    public ArrayList<CatheterSpinnerData> spinners;

    @JsonIgnore
    public String YLL;

    @JsonIgnore
    public String DZXH;

}
