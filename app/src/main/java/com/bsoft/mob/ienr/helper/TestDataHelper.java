package com.bsoft.mob.ienr.helper;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.dynamicui.evaluate.ReflectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Classichu on 2018/2/10.
 */

public class TestDataHelper {

    public static <T> void buidTestData(Class<T> tClass, List<T> list) {
        if (Constant.DEBUG_DEVELOP_TEST_DATA) {
            list.addAll(buidTestDataInner(tClass));
        }
    }

    private static <T> List<T> buidTestDataInner(Class<T> tClass) {
        List<T> tList = new ArrayList<>();
        try {
            for (int i = 0; i < 20; i++) {
                T t = ReflectHelper.setupData(tClass, String.valueOf(i));
                tList.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tList;
    }
}
