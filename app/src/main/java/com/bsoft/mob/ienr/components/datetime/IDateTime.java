package com.bsoft.mob.ienr.components.datetime;

public interface IDateTime {
    String getDateTime();

    String getDateTime(long timeInMillis);

    String getDate();

    String getDate(long timeInMillis);

    String dateTime2Custom(String serverDateTime, String pattern);

    String dateTime2Date(String serverDateTime);

    String dateTime2Time(String serverDateTime);

    String date2DateTime(String serverDate);

    long dateTime2DateTimeInMillis(String serverDateTime);

    String dateTimeAddDays(String serverDateTime, long days);
    String dateTimeAddHours(String serverDateTime, long hours);
    String dateTimeAddMinutes(String serverDateTime, long minutes);
    String dateTimeAddSeconds(String serverDateTime, long seconds);
    String dateTimeAddMilliseconds(String serverDateTime, long milliseconds);

    String custom2DateTime(String serverStrCustom, String patternSrc);

    String custom2Date(String serverStrCustom, String patternSrc);

    String custom2Time(String serverStrCustom, String patternSrc);

    String custom2Custom(String serverStrCustom, String patternSrc, String pattern);

    int compareTo(String dateTime, String anotherDateTime);
    long timeDifference(String dateTime, String anotherDateTime);

    boolean dateTimeBefore(String dateTime, String anotherDateTime);

    boolean dateTimeAfter(String dateTime, String anotherDateTime);

    boolean dateBefore(String date, String anotherDate);

    boolean dateAfter(String date, String anotherDate);

    String ymd2Date(int year, int month, int dayOfMonth);

    String ymdhms2DateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second);

    String ymdhms2Custom(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, String pattern);

    YmdHMs date2Ymd(String date);

    YmdHMs dateTime2YmdHMs(String dateTime);

}
