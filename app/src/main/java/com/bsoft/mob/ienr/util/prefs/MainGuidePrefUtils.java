package com.bsoft.mob.ienr.util.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import com.bsoft.mob.ienr.R;

import java.util.HashMap;

/**
 * 主导航 preference辅助类
 * Created by hy on 14-3-25.
 */
public class MainGuidePrefUtils {


    public static final String SETTING_PREF = "main_guide_pref";


    private static SharedPreferences preferences;


    /**
     * 将数组列表组装成 map
     *
     * @param mContext
     * @return key-value对，失败返回null,例如 病人列表-com.bsoft.mob.ienr.fragment.SickPersonListFragment
     */
    public static HashMap<String, String> getAllMenus(Context mContext) {


        if (mContext == null) {
            return null;
        }

        String[] items = mContext.getResources().getStringArray(R.array.main_menu_class_array);
        String[] keys = mContext.getResources().getStringArray(R.array.setting_main_menu_array);

        if (items == null || keys == null || items.length != keys.length) {
            return null;
        }
        HashMap<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < items.length; i++) {
            result.put(keys[i], items[i]);
        }
        return result;
    }

    /**
     * 获取当前所选导航列,默认为true
     *
     * @param mContext
     * @return
     */
    public static boolean[] getsltMenus(Context mContext) {

        if (!initPref(mContext)) {
            return null;
        }

        String[] items = mContext.getResources().getStringArray(R.array.main_menu_class_array);
        if (items == null) {
            return null;
        }
        boolean[] result = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = preferences.getBoolean(items[i], true);
        }
        return result;
    }

    public static boolean saveMenus(Context mContext, boolean[] state) {

        if (!initPref(mContext)) {
            return false;
        }

        String[] items = mContext.getResources().getStringArray(R.array.main_menu_class_array);

        if (state == null || items == null || state.length != items.length) {
            return false;
        }

        SharedPreferences.Editor editor = preferences.edit();

        for (int i = 0; i < items.length; i++) {

            editor.putBoolean(items[i], state[i]);
        }
        return editor.commit();

    }


    public static boolean initPref(Context mContext) {

        if (mContext == null) {
            return false;
        }
        if (preferences == null) {
            preferences = mContext.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE);
        }
        return preferences != null;
    }
}
