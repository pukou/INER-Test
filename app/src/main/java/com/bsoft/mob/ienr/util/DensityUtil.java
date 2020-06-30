package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * dp   px   转换
 * 
 * @author Tank
 * 
 */
@Deprecated
public class DensityUtil {
	public static int dp2px(float dpValue) {
		final float scale = Resources.getSystem().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 从dp获取像素值
	 * 
	 * @param resource
	 * @param value
	 * @return
	 */
	public static int getPixelsFromdp(Resources resource, float value) {
        return (int) getPixelsFromdpf(resource, value);
    }
	
	/**
	 * 从dp获取像素值
	 * 
	 * @param resource
	 * @param value
	 * @return
	 */
	public static float getPixelsFromdpf(Resources resource, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
        		resource.getDisplayMetrics());
    }
}
