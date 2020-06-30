package com.bsoft.mob.ienr.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Classichu on 2017/4/27.
 */
public class DataMapWrapper implements Serializable{
    public Map<String, String> getMap() {
        return map;
    }

    public DataMapWrapper(Map<String, String> map) {
        this.map = map;
    }

    private Map<String,String> map;
}
