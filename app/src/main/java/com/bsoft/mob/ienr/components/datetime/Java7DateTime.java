package com.bsoft.mob.ienr.components.datetime;

import java.util.Calendar;

public class Java7DateTime implements IDateTime {
    public Java7DateTime() {
        System.out.println("Java 7 及以下 使用 Java7DateTime ");
    }

    @Override
    public String getDateTime() {
        return DateTimeTool.getDateTime();
    }

    @Override
    public String getDateTime(long timeInMillis) {
        return DateTimeTool.getDateTime(timeInMillis);
    }

    @Override
    public String getDate() {
        return DateTimeTool.getDate();
    }

    @Override
    public String getDate(long timeInMillis) {
        return DateTimeTool.getDate(timeInMillis);
    }

    @Override
    public String dateTime2Custom(String serverDateTime, String pattern) {
        return DateTimeTool.dateTime2Custom(serverDateTime, pattern);
    }

    @Override
    public String dateTime2Date(String serverDateTime) {
        return DateTimeTool.dateTime2Date(serverDateTime);
    }

    @Override
    public String dateTime2Time(String serverDateTime) {
        return DateTimeTool.dateTime2Time(serverDateTime);
    }

    @Override
    public String date2DateTime(String serverDate) {
        return DateTimeTool.date2DateTime(serverDate);
    }

    @Override
    public long dateTime2DateTimeInMillis(String serverDateTime) {
        return DateTimeTool.dateTime2Calendar(serverDateTime).getTimeInMillis();
    }

    @Override
    public String dateTimeAddDays(String serverDateTime, long days) {
        return DateTimeTool.dateTimeAddDays(serverDateTime, (int) days);
    }

    @Override
    public String dateTimeAddHours(String serverDateTime, long hours) {
        return DateTimeTool.dateTimeAddHours(serverDateTime, (int) hours);
    }

    @Override
    public String dateTimeAddMinutes(String serverDateTime, long minutes) {
        return DateTimeTool.dateTimeAddMinutes(serverDateTime, (int) minutes);
    }

    @Override
    public String dateTimeAddSeconds(String serverDateTime, long seconds) {
        return DateTimeTool.dateTimeAddSeconds(serverDateTime, (int) seconds);
    }

    @Override
    public String dateTimeAddMilliseconds(String serverDateTime, long milliseconds) {
        return DateTimeHelper.dateTimeAddMilliseconds(serverDateTime, (int) milliseconds);
    }

    @Override
    public String custom2DateTime(String serverStrCustom, String patternSrc) {
        return DateTimeTool.custom2DateTime(serverStrCustom, patternSrc);
    }

    @Override
    public String custom2Date(String serverStrCustom, String patternSrc) {
        return DateTimeTool.custom2Date(serverStrCustom, patternSrc);
    }

    @Override
    public String custom2Time(String serverStrCustom, String patternSrc) {
        return DateTimeTool.custom2Time(serverStrCustom, patternSrc);
    }

    @Override
    public String custom2Custom(String serverStrCustom, String patternSrc, String pattern) {
        return DateTimeTool.custom2Custom(serverStrCustom, patternSrc, pattern);
    }

    @Override
    public int compareTo(String dateTime, String anotherDateTime) {
        return DateTimeTool.compareTo(dateTime, anotherDateTime);
    }

    @Override
    public long timeDifference(String dateTime, String anotherDateTime) {
        return dateTime2DateTimeInMillis(dateTime) - dateTime2DateTimeInMillis(anotherDateTime);
    }

    @Override
    public boolean dateTimeBefore(String dateTime, String anotherDateTime) {
        return DateTimeTool.before(dateTime, anotherDateTime);
    }

    @Override
    public boolean dateTimeAfter(String dateTime, String anotherDateTime) {
        return DateTimeTool.after(dateTime, anotherDateTime);
    }

    @Override
    public boolean dateBefore(String date, String anotherDate) {
        String dateTime = DateTimeTool.date2DateTime(date);
        String anotherDateTime = DateTimeTool.date2DateTime(anotherDate);
        return dateTimeBefore(dateTime, anotherDateTime);
    }

    @Override
    public boolean dateAfter(String date, String anotherDate) {
        String dateTime = DateTimeTool.date2DateTime(date);
        String anotherDateTime = DateTimeTool.date2DateTime(anotherDate);
        return dateTimeAfter(dateTime, anotherDateTime);
    }

    @Override
    public String ymd2Date(int year, int month, int dayOfMonth) {
        return DateTimeTool.ymd2Date(year, month, dayOfMonth);
    }

    @Override
    public String ymdhms2DateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        return DateTimeTool.ymdhms2DateTime(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    @Override
    public String ymdhms2Custom(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, String pattern) {
        return DateTimeTool.ymdhms2Custom(year, month, dayOfMonth, hourOfDay, minute, second, pattern);
    }

    @Override
    public YmdHMs date2Ymd(String date) {
        YmdHMs ymdHMs = new YmdHMs();
        Calendar calendar = DateTimeTool.date2Calendar(date);
        ymdHMs.year = calendar.get(Calendar.YEAR);
        int monthOrYear = calendar.get(Calendar.MONTH);//0~11
        ymdHMs.month = monthOrYear > 11 ? 12 : monthOrYear + 1;//1~12
        ymdHMs.day = calendar.get(Calendar.DAY_OF_MONTH);
        return ymdHMs;
    }

    @Override
    public YmdHMs dateTime2YmdHMs(String dateTime) {
        YmdHMs ymdHMs = new YmdHMs();
        Calendar calendar = DateTimeTool.dateTime2Calendar(dateTime);
        ymdHMs.year = calendar.get(Calendar.YEAR);
        int monthOrYear = calendar.get(Calendar.MONTH);//0~11
        ymdHMs.month = monthOrYear > 11 ? 12 : monthOrYear + 1;//1~12
        ymdHMs.day = calendar.get(Calendar.DAY_OF_MONTH);
        ymdHMs.hour = calendar.get(Calendar.HOUR_OF_DAY);
        ymdHMs.minute = calendar.get(Calendar.MINUTE);
        ymdHMs.second = calendar.get(Calendar.SECOND);
        return ymdHMs;
    }


}
