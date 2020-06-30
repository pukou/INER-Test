package com.bsoft.mob.ienr.model.advice;

public class AdviceUtils {

	public static String getYCXXString(int YCLX) {
		switch (YCLX) {
		case -1:
			return "未作任何操作";
		case 1:
			return "医嘱计划不存在";
		case 2:
			return "医嘱计划作废";
		case 3:
			return "医嘱计划不属于此病人";
		case 4:
			return "重复执行";
		case 5:
			return "时间错误";
		case 31:
			return "医嘱不存在";
		case 32:
			return "医嘱作废";
		case 33:
			return "停瞩";
		case 34:
			return "WDW医嘱作废";
		case 35:
			return "WDW停瞩";
		case 36:
			return "WDW错误";
		case 40:
			return "输液错误";
		case 50:
			return "口服药错误";
		case 60:
			return "口服药错误";
		case 98:
			return "失败";
		case 99:
			return "执行出错";
		default:
			return "执行出错";
		}
	}
}
