package com.bsoft.mob.ienr.model;

import android.content.Context;
import android.text.TextUtils;

import com.bsoft.mob.ienr.util.prefs.SettingUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;
import com.bsoft.mob.ienr.util.VibratorUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-14 下午6:44:26
 * @类说明 解析对象集合
 */
@SuppressWarnings("all")
public class ParserModel {

	// 状态
	public int statue = Statue.ERROR;
	public String ExceptionMessage;

	public String Message;

	public HashMap<String, ArrayList> tableMap = new HashMap<String, ArrayList>();

	public BaseVo vo;

	public ParserModel() {

	}

	public ParserModel(int statue) {
		this.statue = statue;
	}

	public ArrayList getList(String name) {
		return tableMap.get(name);
	}

	public void setIsFalse(String IsFalse) {
		if (EmptyTool.isBlank(IsFalse) || "false".equalsIgnoreCase(IsFalse)) {
			this.statue = Statue.SUCCESS;
		} else {
			this.statue = Statue.ERROR;
		}
	}

	public boolean isOK() {
		return this.statue == Statue.SUCCESS;
	}

	public void showToast(Context context) {

		boolean vib = SettingUtils.isVib(context);
        VibratorUtil.vibrator(context,vib);
		switch (statue) {
		case Statue.NET_ERROR:
 		BSToast.showToast(context, "网络加载失败", BSToast.LENGTH_SHORT);
			break;
		case Statue.ERROR:
			BSToast.showToast(context, null != ExceptionMessage
					&& ExceptionMessage.length() > 0 ? ExceptionMessage
					: "请求失败", BSToast.LENGTH_SHORT);
			break;
		case Statue.PARSER_ERROR:
			BSToast.showToast(context, "解析失败", BSToast.LENGTH_SHORT);
			break;
		default:
			BSToast.showToast(context, "失败", BSToast.LENGTH_SHORT);
			break;
		}
	}
}
