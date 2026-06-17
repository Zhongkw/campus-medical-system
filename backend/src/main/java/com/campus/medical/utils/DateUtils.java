package com.campus.medical.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期时间处理工具类
 * 符合文档第 12.3.2 节 DateUtils 的要求
 */
public class DateUtils {

    /**
     * 默认日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认日期时间格式
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 将日期格式化为 "yyyy-MM-dd" 字符串
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * 将日期时间格式化为 "yyyy-MM-dd HH:mm:ss" 字符串
     * @param date 日期时间
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.format(date);
    }

    /**
     * 将 "yyyy-MM-dd" 字符串解析为 Date 对象
     * @param dateStr 日期字符串
     * @return Date 对象
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期格式错误: " + dateStr);
        }
    }

    /**
     * 将 "yyyy-MM-dd HH:mm:ss" 字符串解析为 Date 对象
     * @param dateTimeStr 日期时间字符串
     * @return Date 对象
     */
    public static Date parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
            return sdf.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期时间格式错误: " + dateTimeStr);
        }
    }

    /**
     * 获取当前日期
     * @return 当前日期
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前日期时间
     * @return 当前日期时间
     */
    public static Date getCurrentDateTime() {
        return new Date();
    }

    /**
     * 给日期添加指定天数
     * @param date 日期
     * @param days 天数
     * @return 添加后的日期
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        localDateTime = localDateTime.plusDays(days);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 计算两个日期之间的天数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数
     */
    public static long getDaysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        LocalDate startLocalDate = startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate endLocalDate = endDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
    }
}
