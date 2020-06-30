package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-27 下午7:32:09
 * @类说明 字符串工具类
 */
public class StringUtil {

	private static Toast toast;

	// 1字符串，防止出现null
	public static String getUnEmptText(String value) {
		if (null != value && value.length() > 0) {
			return value;
		}
		return "";
	}

	// 二个字符串，防止出现null
	public static String getText(String type, String value) {
		
		if (!EmptyTool.isBlank(value)) {
			return new StringBuffer(type).append(value).toString();
		}
		return type;
	}

	// 截字符串
	public static String getStringLength(String str, int len) {
		if (null != str && str.length() > len) {
			return str.substring(0, len - 1) + "...";
		}
		return str;
	}

	// 三个字符串叠加(时间)
	public static String getStringText(String timeType, String time, String rm) {
		StringBuffer buf = new StringBuffer(timeType);
		if (null != time && time.length() > 0) {
			Date date = DateUtil.getDateCompat(time);
			String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
			buf.append(dateStr);
		}
		if (null != rm && rm.length() > 0) {
			buf.append("    ").append(rm);
		}
		return buf.toString();
	}

	// 二个字符串叠加(非时间)
	public static String getStringTexts(String s1, String s2) {
		StringBuffer buf = new StringBuffer();
		if (null != s1 && s1.length() > 0) {
			buf.append(s1);
		}
		if (null != s2 && s2.length() > 0) {
			buf.append(s2);
		}
		return buf.toString();
	}
	// 三个字符串叠加(非时间)
	public static String getStringTexts(String timeType, String s1, String s2) {
		StringBuffer buf = new StringBuffer(timeType);
		if (null != s1 && s1.length() > 0) {
			buf.append(s1);
		}
		if (null != s2 && s2.length() > 0) {
			buf.append(s2);
		}
		return buf.toString();
	}

	public static String getStringTexts(String timeType, String s1, String s2,
			String s3) {
		StringBuffer buf = new StringBuffer(timeType);
		if (null != s1 && s1.length() > 0) {
			buf.append(s1);
		}
		if (null != s2 && s2.length() > 0) {
			buf.append(s2);
		}
		if (null != s3 && s3.length() > 0) {
			buf.append(s3);
		}
		return buf.toString();
	}

	/**
	 * 替换字符串空白
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|t|r");
			Matcher m = p.matcher(str);
			dest = m.replaceAll(" ");
		}
		return dest;
	}
	public static void showToast(String s) {
		showToast(AppApplication.getContext(), s);
	}
	public static void showToast(Context context, String title) {

		if (toast == null) {
			toast = Toast.makeText(context, title, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else {
			toast.setText(title);
			toast.show();
		}
	}
}
