package com.bsoft.mob.ienr.components.datetime;

import android.os.Build;

/**
 * {@link DateTimeHelper 配合服务器时间相关处理的帮助类 }
 */
public class DateTimeFactory {

    public static IDateTime getInstance() {
        return DateTimeFactoryInner.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class DateTimeFactoryInner {
        private static IDateTime INSTANCE;
        static {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                INSTANCE = create(JavaDateTime.class);
            } else {
                INSTANCE = create(Java7DateTime.class);
            }
        }
    }

    //=========================================
    private static <T extends IDateTime> T create(Class<T> tClass) {
        IDateTime base = null;
        try {
            base = (IDateTime) Class.forName(tClass.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) base;
    }
}
