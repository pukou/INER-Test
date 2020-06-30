package com.bsoft.mob.ienr.dynamicui;

/**
 * Created by TXM on 2016-5-30.
 */
public interface DialogListener {
    /**
     * 在此方法中实现图片的显示
     */
    public void refreshActivity(Object object);

    /**
     * 在此方法中实现图片的异步保存
     */
    public void saveImage(byte[] bytes);
}
