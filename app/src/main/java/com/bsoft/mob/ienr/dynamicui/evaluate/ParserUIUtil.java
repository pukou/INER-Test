package com.bsoft.mob.ienr.dynamicui.evaluate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

/**
 * 解析控件
 * 
 * @author hy
 * 
 */
public class ParserUIUtil {

	public static String getRquestXML(Form form, Classification cf, View cfView)
			throws JSONException {

		if (form == null || cf == null) {
			return null;
		}

		JSONObject obj = new JSONObject();
		JSONObject jsonForm = new JSONObject();

		jsonForm.put("ID", form.ID);
		jsonForm.put("YSXH", form.YSXH);
		jsonForm.put("Score", form.Score);
		jsonForm.put("TXGH", form.TXGH);
		jsonForm.put("JLGH", null);
		jsonForm.put("ZYH", null);
		jsonForm.put("YSLX", form.YSLX);

		JSONArray items = new JSONArray();
		for (ItemNode node : cf.itemNodes) {
			JSONObject jsonItem = new JSONObject();
			jsonItem.put("XMID", node.ID);
			// 遍历checkbox
			if (node.cbs != null) {
				for (CheckBox cb : node.cbs) {
					// if(cb.)

				}
			}

		}

		obj.put("ROOT", jsonForm);

		return obj.toString();
	}
}
