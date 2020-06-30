package com.bsoft.mob.ienr.util;

public class MessageUtils {

	public static CharSequence getTopicChars(int topic) {

		switch (topic) {
			case 1:
				return "变动医嘱";
			case 2:
				return "标本采集";
			case 3:
				return "危机值提示";
			case 4:
				return "系统提醒";
			case 5:
				return "体征采集";
			case 6:
				return "风险评估";
			case 7:
				return "风险措施";
		}
		//return null;
		return "未知类型消息";
	}
}
