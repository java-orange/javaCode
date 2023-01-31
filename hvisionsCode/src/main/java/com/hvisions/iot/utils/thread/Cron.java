package com.hvisions.iot.utils.thread;

public class Cron {

    public static String daily(int day) {
        return String.format("0 0 0 1/%d * ?", day);
    }

    public static String hourly(int hour) {
        return String.format("0 0 0/%d * * ?", hour);
    }

    public static String every1Hour() {
        return "0 0 0/1* * ?";
    }

    public static String minutely(int minute) {
        return String.format("0 0/%d * * * ?", minute);
    }

    public static String every1minute() {
        return "0 0/1 * * * ?";
    }
    public static String every5minutes() {
        return "0 0/5 * * * ?";
    }

    public static String every10minutes() {
        return "0 0/10 * * * ?";
    }

    public static String every15minutes() {
        return "0 0/15 * * * ?";
    }

    public static String every30minutes() {
        return "0 0/30 * * * ?";
    }

    public static String secondly(int second) {
        return String.format("*/%d * * * * ?", second);
    }

    public static String every15seconds() {
        return "*/15 * * * * ?";
    }

    public static String every30seconds() {
        return "*/30 * * * * ?";
    }
}
