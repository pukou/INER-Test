package com.bsoft.mob.ienr.util.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 当前用户的设置项 preference辅助类 Created by hy on 14-3-25.
 */
// TODO 临时方案，解决条码扫描等，提示对context的非正常引用
public class SettingUtils {

    public static final String SETTING_PREF = "setting_pref";
    public static final String VIB_KEY = "vib";
    public static final String LOG_KEY = "log";
    public static final String parseBarcode_KEY = "parseBarcode_KEY";
    /*
       升级编号【56010049】============================================= start
       病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
       ================= Classichu 2017/10/18 9:34
       */
    public static final String ITEM_CLICK_KEY = "item_click_key";

    public static final String ITEM_CUSTOM_BARCODE_KEY = "item_custom_bc_key";

    /* =============================================================== end */
    private static SharedPreferences preferences;

    /**
     * 获取IP
     *
     * @param mContext 上下文
     * @return WIFI 密码,默认为""字符串，失败返回null。
     */
    public static boolean isVib(Context mContext) {

        if (!initPref(mContext)) {
            return true;
        }
        return preferences.getBoolean(VIB_KEY, true);
    }

    public static boolean isNeedParseBarcode(Context mContext) {
        if (!initPref(mContext)) {
            return true;
        }
        return preferences.getBoolean(parseBarcode_KEY, true);//默认true
    }

    public static void setNeedParseBarcode(Context mContext, boolean value) {
        initPref(mContext);
        preferences.edit().putBoolean(parseBarcode_KEY, value).apply();
    }

    public static boolean isJL_Log(Context mContext) {
        if (!initPref(mContext)) {
            return true;
        }
        return preferences.getBoolean(LOG_KEY, false);
    }

    public static void setJL_Log(Context mContext, boolean value) {
        initPref(mContext);
        preferences.edit().putBoolean(LOG_KEY, value).apply();
    }

    /*
       升级编号【56010049】============================================= start
       病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
       ================= Classichu 2017/10/18 9:34
       */
    public static boolean isSickerItemCanNotClick(Context mContext) {

        if (!initPref(mContext)) {
            return true;
        }
        return preferences.getBoolean(ITEM_CLICK_KEY, false);//默认false
    }

    public static void saveSickerItemCanNotClick(Context mContext, boolean isSickerItemCanNotClick) {

        initPref(mContext);
        preferences.edit().putBoolean(ITEM_CLICK_KEY, isSickerItemCanNotClick).apply();
    }

    //读取是否要自定义扫描头接口
    public static boolean isCustomBarcode(Context mContext) {

        if (!initPref(mContext)) {
            return true;
        }
        return preferences.getBoolean(ITEM_CUSTOM_BARCODE_KEY, false);//默认false
    }

    //保存是否自定义扫描头接口
    public static void saveCustomBarcode(Context mContext, boolean isCustomBarcode) {

        initPref(mContext);
        preferences.edit().putBoolean(ITEM_CUSTOM_BARCODE_KEY, isCustomBarcode).apply();
    }
    /* =============================================================== end */
    public static boolean initPref(Context mContext) {

        if (mContext == null) {
            return false;
        }
        if (preferences == null) {
            preferences = mContext.getSharedPreferences(SETTING_PREF,
                    Context.MODE_PRIVATE);
        }
        return preferences != null;
    }
}
