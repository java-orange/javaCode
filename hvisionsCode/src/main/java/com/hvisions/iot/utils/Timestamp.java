package com.hvisions.iot.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class Timestamp {

    private Timestamp() {
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]");

    /**
     * 获取当前时间，返回字符串
     *
     * @return  ISO-8601 format string
     */
    public static String current() {
        return ZonedDateTime.now().toOffsetDateTime().format(formatter);
    }

    /**
     * 获取当前本地时间
     *
     * @return LocalDateTime
     */
    public static LocalDateTime currentOfLocal() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前带时区时间
     *
     * @return ZonedDateTime对象
     */
    public static ZonedDateTime currentWithZone() {
        return ZonedDateTime.now();
    }

    /**
     * 将日期字符串转换为LocalDate，date符合ISO_DATE定义
     *
     * @param date 日期字符串，如 2021-01-01
     * @return LocalDate对象
     */
    public static LocalDate toLocalDate(String date) {
        String validDate = date;
        if (date.length() > 10) {
            validDate = date.substring(0, 10);
        }

        return LocalDate.parse(validDate, DateTimeFormatter.ISO_DATE);
    }

    /**
     * 将时间字符串转换为LocalTime
     *
     * @param time 时间字符串，如 01:02:03
     * @return LocalTime对象
     */
    public static LocalTime toLocalTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ISO_TIME);
    }

    /**
     * 将带时区的日期时间字符串转换为本地时间
     *
     * @param datetime 带时区的日期时间字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDatetime(String datetime) {
        ZonedDateTime zonedDateTime = toZoneDateTime(datetime);

        return zonedDateTime.toLocalDateTime();
    }

    /**
     * 更改ZonedDateTime时区
     *
     * @param zonedDateTime ZonedDateTime对象
     * @param zoneId ZoneId对象，如 ZoneId.of("+08:00")
     * @return ZonedDateTime对象
     */
    public static ZonedDateTime changeZone(ZonedDateTime zonedDateTime, ZoneId zoneId) {
        return zonedDateTime.withZoneSameInstant(zoneId);
    }

    /**
     * 将字符串转换为ZonedDateTime
     *
     * @param datetime 日期时间字符串
     * @param fmt 解析的格式
     * @return ZonedDateTime对象
     */
    public static ZonedDateTime toZoneDateTime(String datetime, DateTimeFormatter fmt) {
        if (fmt == null) {
            return ZonedDateTime.parse(datetime);
        }

        return ZonedDateTime.parse(datetime, fmt);
    }

    /**
     * 将字符串转换为ZonedDateTime，按照ISO标准格式进行解析
     *
     * @param datetime 日期时间字符串
     * @return ZonedDateTime对象
     */
    public static ZonedDateTime toZoneDateTime(String datetime) {
        return ZonedDateTime.parse(datetime);
    }

    /**
     * 将ZonedDateTime按照指定的fmt转换为字符串
     *
     * @param zonedDateTime ZonedDateTime对象
     * @param fmt DateTimeFormatter指定的格式
     * @return 日期时间字符串
     */
    public static String toString(ZonedDateTime zonedDateTime, DateTimeFormatter fmt) {
        if (fmt == null) {
            return zonedDateTime.format(formatter);
        }

        return zonedDateTime.format(fmt);
    }

    /**
     * 将ZonedDateTime按照默认的fmt转换为字符串
     *
     * @param zonedDateTime ZonedDateTime对象
     * @return 日期时间字符串
     */
    public static String toString(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(formatter);
    }

    /**
     * 比较两个时间是否是同一天
     *
     * @param dateTimeFirst 比较的第一个时间
     * @param dateTimeSecond 比较的第二个时间
     * @return 日期相同返回true，否则返回false
     */
    public static Boolean isSameDate(LocalDateTime dateTimeFirst, LocalDateTime dateTimeSecond){
        if (dateTimeFirst == null || dateTimeSecond == null) {
            return false;
        }
        return LocalDate.from(dateTimeFirst).equals(LocalDate.from(dateTimeSecond));
    }
}
