package com.bsoft.mob.ienr.components.datetime;

import android.annotation.TargetApi;
import android.os.Build;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Java 8 及以上可用
 * 使用
 * Instant 代替 Date
 * LocalDateTime/ZonedDateTime 代替 Calendar
 * DateTimeFormatter 代替 SimpleDateFormat
 */
//@RequiresApi(api = Build.VERSION_CODES.O)
@TargetApi(Build.VERSION_CODES.O)
public class JavaDateTime implements IDateTime {
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    public JavaDateTime() {
        System.out.println("Java 8 及以上 使用 JavaDateTime ");
    }

    @Override
    public String getDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZONE_ID);
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatterSrc.format(zonedDateTime);
    }

    @Override
    public String getDateTime(long timeInMillis) {
        Instant instant = Instant.ofEpochMilli(timeInMillis);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZONE_ID);
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatterSrc.format(zonedDateTime);
    }

    @Override
    public String getDate() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZONE_ID);
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        return dateTimeFormatterSrc.format(zonedDateTime);
    }

    @Override
    public String getDate(long timeInMillis) {
        Instant instant = Instant.ofEpochMilli(timeInMillis);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZONE_ID);
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        return dateTimeFormatterSrc.format(zonedDateTime);
    }

    @Override
    public String dateTime2Custom(String serverDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String dateTime2Date(String serverDateTime) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String dateTime2Time(String serverDateTime) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_TIME);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String date2DateTime(String serverDate) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        LocalDate localDate = LocalDate.parse(serverDate, dateTimeFormatterSrc);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public long dateTime2DateTimeInMillis(String serverDateTime) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZONE_ID);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    @Override
    public String dateTimeAddDays(String serverDateTime, long days) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        localDateTime.plusDays(days);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String dateTimeAddHours(String serverDateTime, long hours) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        localDateTime.plusHours(hours);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String dateTimeAddMinutes(String serverDateTime, long minutes) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        localDateTime.plusMinutes(minutes);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String dateTimeAddSeconds(String serverDateTime, long seconds) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        localDateTime.plusSeconds(seconds);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String dateTimeAddMilliseconds(String serverDateTime, long milliseconds) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(serverDateTime, dateTimeFormatterSrc);
        localDateTime.plus(10, ChronoUnit.MILLIS);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String custom2DateTime(String serverStrCustom, String patternSrc) {
        return custom2Custom(serverStrCustom,patternSrc,DateTimeFormat.FORMAT_DATE_TIME);
    }

    @Override
    public String custom2Date(String serverStrCustom, String patternSrc) {
        return custom2Custom(serverStrCustom,patternSrc,DateTimeFormat.FORMAT_DATE);
    }

    @Override
    public String custom2Time(String serverStrCustom, String patternSrc) {
        return custom2Custom(serverStrCustom,patternSrc,DateTimeFormat.FORMAT_TIME);
    }

    @Override
    public String custom2Custom(String serverStrCustom, String patternSrc, String pattern) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(patternSrc);
        LocalDateTime localDateTime;
        try {
            //日期时间
            localDateTime = LocalDateTime.parse(serverStrCustom, dateTimeFormatterSrc);
        } catch (DateTimeException e) {
            try {
                //日期
                LocalDate localDate = LocalDate.parse(serverStrCustom, dateTimeFormatterSrc);
                localDateTime = localDate.atStartOfDay();
            } catch (DateTimeException e_) {
                //时间
                LocalTime localTime = LocalTime.parse(serverStrCustom, dateTimeFormatterSrc);
                ZonedDateTime zonedDateTime = ZonedDateTime.now(ZONE_ID);
                localDateTime = LocalDateTime.of(zonedDateTime.toLocalDate(), localTime);
            }
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public int compareTo(String dateTime, String anotherDateTime) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatterSrc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime anotherLocalDateTime = LocalDateTime.parse(anotherDateTime, dateTimeFormatter);
        return localDateTime.compareTo(anotherLocalDateTime);
    }

    @Override
    public long timeDifference(String dateTime, String anotherDateTime) {
        return dateTime2DateTimeInMillis(dateTime) - dateTime2DateTimeInMillis(anotherDateTime);
    }

    @Override
    public boolean dateTimeBefore(String dateTime, String anotherDateTime) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatterSrc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime anotherLocalDateTime = LocalDateTime.parse(anotherDateTime, dateTimeFormatter);
        return localDateTime.isBefore(anotherLocalDateTime);
    }

    @Override
    public boolean dateTimeAfter(String dateTime, String anotherDateTime) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatterSrc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime anotherLocalDateTime = LocalDateTime.parse(anotherDateTime, dateTimeFormatter);
        return localDateTime.isAfter(anotherLocalDateTime);
    }

    @Override
    public boolean dateBefore(String date, String anotherDate) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        LocalDate localDate = LocalDate.parse(date, dateTimeFormatterSrc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        LocalDate anotherLocalDate = LocalDate.parse(anotherDate, dateTimeFormatter);
        return localDate.isBefore(anotherLocalDate);
    }

    @Override
    public boolean dateAfter(String date, String anotherDate) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        LocalDate localDate = LocalDate.parse(date, dateTimeFormatterSrc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        LocalDate anotherLocalDate = LocalDate.parse(anotherDate, dateTimeFormatter);
        return localDate.isAfter(anotherLocalDate);
    }

    @Override
    public String ymd2Date(int year, int month, int dayOfMonth) {
        LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        return dateTimeFormatter.format(localDate);
    }

    @Override
    public String ymdhms2DateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute, second);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public String ymdhms2Custom(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, String pattern) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute, second);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(localDateTime);
    }

    @Override
    public YmdHMs date2Ymd(String date) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE);
        LocalDate localDate = LocalDate.parse(date, dateTimeFormatterSrc);
        YmdHMs  ymdHMs= new YmdHMs();
        ymdHMs.year = localDate.getYear();
        ymdHMs.month = localDate.getMonthValue();
        ymdHMs.day = localDate.getDayOfMonth();
        return ymdHMs;
    }

    @Override
    public YmdHMs dateTime2YmdHMs(String dateTime) {
        DateTimeFormatter dateTimeFormatterSrc = DateTimeFormatter.ofPattern(DateTimeFormat.FORMAT_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatterSrc);
        YmdHMs ymdHMs = new YmdHMs();
        ymdHMs.year = localDateTime.getYear();
        ymdHMs.month = localDateTime.getMonthValue();
        ymdHMs.day = localDateTime.getDayOfMonth();
        ymdHMs.hour = localDateTime.getHour();
        ymdHMs.minute = localDateTime.getMinute();
        ymdHMs.second = localDateTime.getSecond();
        return ymdHMs;
    }

}
