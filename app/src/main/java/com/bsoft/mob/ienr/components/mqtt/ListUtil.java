package com.bsoft.mob.ienr.components.mqtt;

import java.util.ArrayList;

import android.text.TextUtils;

import com.bsoft.mob.ienr.util.tools.EmptyTool;

public class ListUtil {

	public static ArrayList<String> filterList(ArrayList<String> oration) {

		if (oration == null) {
			return oration;
		}
		ArrayList<String> result = new ArrayList<String>();
		for (String str : oration) {
			if (!EmptyTool.isBlank(str)) {
				result.add(str);
			}
		}
		return result;

	}
}
