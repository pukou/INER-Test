package com.bsoft.mob.ienr.util.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by louisgeek on 2016/7/12.
 */
public class ParamsTool {
    private static final String TAG = "ParamsTool";

    /**
     * paramsStr  "id=1&qq=222"  baseid=27&identitycardimages=755&businesslicenceimages=111
     *
     * @param paramsStr
     * @return
     */
    public static Map<String, String> paramsStrToMap(String paramsStr) {
        Map<String, String> map = null;
        if (paramsStr != null && !paramsStr.equals("")) {
            map = new HashMap<>();
            String[] paramsStrs = paramsStr.split("&");
            if (paramsStrs != null && paramsStrs.length > 0) {
                for (int i = 0; i < paramsStrs.length; i++) {
                    String paramsStrChild = paramsStrs[i];
                    if (paramsStrChild != null && !paramsStrChild.equals("")) {
                        String[] paramsStrChilds = paramsStrChild.split("=");
                        //###  Log.d(TAG, "upxxx paramsStrToMap: paramsStrChilds.length:"+paramsStrChilds.length);
                        if (paramsStrChilds != null && paramsStrChilds.length > 0) {
                            String key = paramsStrChilds[0];
                            String value = "";
                            if (paramsStrChilds.length > 1) {
                                value = paramsStrChilds[1];
                            }
                            map.put(key, value);
                            //Log.d(TAG, "upxxx paramsStrToMap: key:" + key + ",value:" + value);
                        }
                    }
                }
            }

        }
        return map;
    }

    public static String paramsMapToStr(Map<String, String> paramsMap) {
        String paramsStr = "";
        if (paramsMap != null && paramsMap.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            // keySet遍历key和value
            for (String key : paramsMap.keySet()) {
                // System.out.println("key= "+ key + " and value= " + paramsMap.get(key));
                String value = paramsMap.get(key);
                stringBuilder.append(key);
                stringBuilder.append("=");
                stringBuilder.append(value);
                stringBuilder.append("&");
            }
            paramsStr = stringBuilder.toString();
            //
            //### Log.d(TAG, "paramsMapToStr: paramsStr start:"+paramsStr);
            if (paramsStr != null && !("").equals(paramsStr)) {
                paramsStr = paramsStr.substring(0, paramsStr.length() - 1);
            }
            //Log.d(TAG, "paramsMapToStr: paramsStr end:" + paramsStr);
        }
        return paramsStr;
    }

}
