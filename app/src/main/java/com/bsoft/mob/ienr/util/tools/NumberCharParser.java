package com.bsoft.mob.ienr.util.tools;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;

public class NumberCharParser {
    public static String parserNumWithCircle(int number) {
        String Char = "";
        switch (number) {
            case -1:
                Char = Constant.DEBUG ? "-1" : "";//特殊处理 -1 不显示
                break;
            case 0:
                Char = Constant.DEBUG ? "0" : "";//特殊处理 0 不显示
                break;
            case 1:
                Char = "①";
                break;
            case 2:
                Char = "②";
                break;
            case 3:
                Char = "③";
                break;
            case 4:
                Char = "④";
                break;
            case 5:
                Char = "⑤";
                break;
            case 6:
                Char = "⑥";
                break;
            case 7:
                Char = "⑦";
                break;
            case 8:
                Char = "⑧";
                break;
            case 9:
                Char = "⑨";
                break;
            case 10:
                Char = "⑩";
                break;
            case 11:
                Char = "⑪";
                break;
            case 12:
                Char = "⑫";
                break;
            case 13:
                Char = "⑬";
                break;
            case 14:
                Char = "⑭";
                break;
            case 15:
                Char = "⑮";
                break;
            case 16:
                Char = "⑯";
                break;
            case 17:
                Char = "⑰";
                break;
            case 18:
                Char = "⑱";
                break;
            case 19:
                Char = "⑲";
                break;
            case 20:
                Char = "⑳";
                break;
            default:
                Char = "(" + number + ")";
        }
        return Char;
    }
}
