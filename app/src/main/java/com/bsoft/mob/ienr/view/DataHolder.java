package com.bsoft.mob.ienr.view;


import android.util.Log;

import com.bsoft.mob.ienr.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by louisgeek on 2017/6/6.
 * 尽量少用
 * 使用时候注意内存缓存和生命周期
 */
@Deprecated
public class DataHolder {

    private Map<String, Object> mDataMap;
    /**
     * ====================================================
     */
    private static volatile DataHolder mInstance;

    /* 私有构造方法，防止被实例化 */
    private DataHolder() {
        mDataMap = new HashMap<>();
    }

    public static DataHolder getInstance() {
        if (mInstance == null) {
            synchronized (DataHolder.class) {
                if (mInstance == null) {
                    mInstance = new DataHolder();
                }
            }
        }
        return mInstance;
    }

    /**
     * ==========================================
     */


    public void putData(String key, Object value) {
        mDataMap.put(key, value);
    }

    public Object getData(String key) {
        return mDataMap.get(key);
    }


    public void showAllData() {
        for (String key : mDataMap.keySet()) {
            Log.d(Constant.TAG_COMM,key);
            Log.d(Constant.TAG_COMM,mDataMap.get(key).toString());
        }
    }


}
