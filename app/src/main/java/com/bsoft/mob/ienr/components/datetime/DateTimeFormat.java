package com.bsoft.mob.ienr.components.datetime;

import java.util.regex.Pattern;

/**
 * 不遵守命名规则，方便使用
 */
public class DateTimeFormat {
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String HHmm = "HH:mm";
    public static final String MM_dd = "MM-dd";
    public static final String MM_dd_HHmm = "MM-dd HH:mm";
    public static final String yyyy_MM_dd_HHmm = "yyyy-MM-dd HH:mm";
    public static final String yyyy_MM_dd_HHmmss_S = "yyyy-MM-dd HH:mm:ss.S";
    public static final String yyyy_MM_dd_HHmmSS = "yyyy-MM-dd HH:mm:SS";
    public static final String yyyy_MM_dd_T_HHmm = "yyyy-MM-dd'T'HH:mm";
    public static final String yyyy_MM_dd_T_HHmmss_SSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String yyyy_MM_dd_T_HHmmss_Z = "yyyy-MM-dd'T'HH:mm:ssZ";


    public static class Judge {

        /**
         * yyyyMMddHHmmss
         */
        public static boolean is_yyyyMMddHHmmss(String input) {
            // 不带"-"或":"或" " (横杠，冒号，空格)，而且必须14位
            String dateTimeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229))([0-1]?[0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$";
            boolean is = Pattern.matches(dateTimeRegex, input);
            return is;
        }

        /**
         * yyyy-MM-dd HH:mm:ss
         * 日期和时间之间可能有一个或多个空格
         */
        public static boolean is_yyyy_MM_dd_HH_mm_ss(String input) {
            String dateTimeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
            boolean is = Pattern.matches(dateTimeRegex, input);
            return is;
        }

        /**
         * yyyy-MM-dd HH:mm:ss
         * 日期和时间之间可能有一个或多个空格
         */
        public static boolean is_yyyy_MM_dd_HH_mm(String input) {
            String dateTimeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9])";
            boolean is = Pattern.matches(dateTimeRegex, input);
            return is;
        }

        /**
         * yyyyMMdd
         */
        public static boolean is_yyyyMMdd(String input) {
            String dateRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229))$";
            boolean is = Pattern.matches(dateRegex, input);
            return is;
        }

        /**
         * yyyy-MM-dd
         */
        public static boolean is_yyyy_MM_dd(String input) {
            String dateRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))$";
            boolean is = Pattern.matches(dateRegex, input);
            return is;
        }

        /**
         * HHmmss
         */
        public static boolean is_HHmmss(String input) {
            String timeRegex = "([0-1]?[0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$";
            boolean is = Pattern.matches(timeRegex, input);
            return is;
        }

        /**
         * HH:mm:ss
         */
        public static boolean is_HH_mm_ss(String input) {
            String timeRegex = "([0-1]?[0-9]|2[0-3]);([0-5][0-9]):([0-5][0-9])$";
            boolean is = Pattern.matches(timeRegex, input);
            return is;
        }
    }

}
