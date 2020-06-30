package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bsoft.mob.ienr.util.prefs.SettingUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;



/**
 * 体征采集辅助类
 * Created by huangy on 2015/8/17.
 */
public class SpecimenUtil {


    /**
     * 根据扫描条码号，获取条码编号 (函数对应LIS 存储过程)
     *
     * @param barcode
     * @param now
     * @return 失败返回NULL
     */
    public static String getTmbh(Context context, String barcode, String nowServerDate) {
        if (EmptyTool.isBlank(barcode) || nowServerDate == null) {
            return null;
        }
        String first = nowServerDate.substring(0, 3);

        String last = "A";
        	 /*
            升级编号【56010013】============================================= start
            标本采集：是否需求转换条码:需要加入参数控制：是否需求转换条码
            ================= Classichu 2017/10/18 9:34
            */
        if (SettingUtils.isNeedParseBarcode(context)) {
            // 如果需要校验位请打开下面一行代码
            last = getTmbhLastNo(barcode);
        }
        /* =============================================================== end */
        String tmbh = first + barcode + last;
        Log.d("zfqq", "getTmbh: " + tmbh);
        return tmbh;
    }

    private static String getTmbhLastNo(String barcode) {
        int sum = 0;
        int length = barcode.length();//10
        for (int i = 0; i < length; i++) {
            sum += Integer.valueOf(barcode.substring(i, i + 1));
        }

        sum = sum % 43;
        if (sum > 9 && sum < 36) {
            char last = (char) (sum + 55);
            return String.valueOf(last);
        }

        String last = null;

        if (sum > 35) {
            switch (sum) {
                case 36:
                    last = "-";
                    break;
                case 37:
                    last = ".";
                    break;
                case 38:
                    last = " ";
                    break;
                case 39:
                    last = "$";
                    break;
                case 40:
                    last = "/";
                    break;
                case 41:
                    last = "+";
                    break;
                case 42:
                    last = "%";
                    break;
                default:
            }
        } else {
            last = String.valueOf(sum);
        }
        return last;
    }

}
