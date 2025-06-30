package com.healthsys.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类。
 * 提供日期格式化、转换等常用方法。
 * 
 * @author 梦辰
 */
public class DateUtil {

  private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

  // 常用日期格式
  public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
  public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
  public static final String DATE_FORMAT_CHINESE = "yyyy年MM月dd日";

  /**
   * 私有构造函数，防止实例化
   */
  private DateUtil() {
    throw new UnsupportedOperationException("工具类不能被实例化");
  }

  /**
   * 获取当前日期字符串
   * 
   * @param format 日期格式
   * @return 格式化后的日期字符串
   */
  public static String getCurrentDateString(String format) {
    try {
      String dateStr = cn.hutool.core.date.DateUtil.format(new Date(), format);
      logger.debug("获取当前日期字符串: {}", dateStr);
      return dateStr;
    } catch (Exception e) {
      logger.error("获取当前日期字符串失败", e);
      return "";
    }
  }

  /**
   * 获取当前日期字符串（默认格式：yyyy-MM-dd）
   * 
   * @return 格式化后的日期字符串
   */
  public static String getCurrentDateString() {
    return getCurrentDateString(DATE_FORMAT_YYYY_MM_DD);
  }

  /**
   * 获取当前日期时间字符串（默认格式：yyyy-MM-dd HH:mm:ss）
   * 
   * @return 格式化后的日期时间字符串
   */
  public static String getCurrentDateTimeString() {
    return getCurrentDateString(DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
  }

  /**
   * 将Date转换为字符串
   * 
   * @param date   日期对象
   * @param format 日期格式
   * @return 格式化后的日期字符串
   */
  public static String formatDate(Date date, String format) {
    if (date == null || StrUtil.isBlank(format)) {
      return "";
    }

    try {
      String dateStr = cn.hutool.core.date.DateUtil.format(date, format);
      logger.debug("格式化日期: {} -> {}", date, dateStr);
      return dateStr;
    } catch (Exception e) {
      logger.error("格式化日期失败", e);
      return "";
    }
  }

  /**
   * 将LocalDate转换为字符串
   * 
   * @param localDate 本地日期对象
   * @param format    日期格式
   * @return 格式化后的日期字符串
   */
  public static String formatLocalDate(LocalDate localDate, String format) {
    if (localDate == null || StrUtil.isBlank(format)) {
      return "";
    }

    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
      String dateStr = localDate.format(formatter);
      logger.debug("格式化本地日期: {} -> {}", localDate, dateStr);
      return dateStr;
    } catch (Exception e) {
      logger.error("格式化本地日期失败", e);
      return "";
    }
  }

  /**
   * 将LocalDateTime转换为字符串
   * 
   * @param localDateTime 本地日期时间对象
   * @param format        日期格式
   * @return 格式化后的日期时间字符串
   */
  public static String formatLocalDateTime(LocalDateTime localDateTime, String format) {
    if (localDateTime == null || StrUtil.isBlank(format)) {
      return "";
    }

    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
      String dateTimeStr = localDateTime.format(formatter);
      logger.debug("格式化本地日期时间: {} -> {}", localDateTime, dateTimeStr);
      return dateTimeStr;
    } catch (Exception e) {
      logger.error("格式化本地日期时间失败", e);
      return "";
    }
  }

  /**
   * 将字符串解析为Date
   * 
   * @param dateStr 日期字符串
   * @param format  日期格式
   * @return Date对象
   */
  public static Date parseDate(String dateStr, String format) {
    if (StrUtil.isBlank(dateStr) || StrUtil.isBlank(format)) {
      return null;
    }

    try {
      Date date = cn.hutool.core.date.DateUtil.parse(dateStr, format);
      logger.debug("解析日期字符串: {} -> {}", dateStr, date);
      return date;
    } catch (Exception e) {
      logger.error("解析日期字符串失败: {}", dateStr, e);
      return null;
    }
  }

  /**
   * 将字符串解析为LocalDate
   * 
   * @param dateStr 日期字符串
   * @param format  日期格式
   * @return LocalDate对象
   */
  public static LocalDate parseLocalDate(String dateStr, String format) {
    if (StrUtil.isBlank(dateStr) || StrUtil.isBlank(format)) {
      return null;
    }

    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
      LocalDate localDate = LocalDate.parse(dateStr, formatter);
      logger.debug("解析本地日期字符串: {} -> {}", dateStr, localDate);
      return localDate;
    } catch (Exception e) {
      logger.error("解析本地日期字符串失败: {}", dateStr, e);
      return null;
    }
  }

  /**
   * 计算两个日期之间的天数差
   * 
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return 天数差（正数表示endDate晚于startDate）
   */
  public static long daysBetween(Date startDate, Date endDate) {
    if (startDate == null || endDate == null) {
      return 0;
    }

    try {
      long days = cn.hutool.core.date.DateUtil.betweenDay(startDate, endDate, false);
      logger.debug("计算日期差: {} 到 {} = {} 天", startDate, endDate, days);
      return days;
    } catch (Exception e) {
      logger.error("计算日期差失败", e);
      return 0;
    }
  }

  /**
   * 计算两个LocalDate之间的天数差
   * 
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return 天数差（正数表示endDate晚于startDate）
   */
  public static long daysBetween(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      return 0;
    }

    try {
      long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
      logger.debug("计算本地日期差: {} 到 {} = {} 天", startDate, endDate, days);
      return days;
    } catch (Exception e) {
      logger.error("计算本地日期差失败", e);
      return 0;
    }
  }

  /**
   * 给日期增加指定天数
   * 
   * @param date 原日期
   * @param days 要增加的天数（可以为负数）
   * @return 新的日期
   */
  public static Date addDays(Date date, int days) {
    if (date == null) {
      return null;
    }

    try {
      DateTime dateTime = cn.hutool.core.date.DateUtil.offsetDay(date, days);
      Date newDate = dateTime.toJdkDate();
      logger.debug("日期增加天数: {} + {} 天 = {}", date, days, newDate);
      return newDate;
    } catch (Exception e) {
      logger.error("日期增加天数失败", e);
      return date;
    }
  }

  /**
   * 给LocalDate增加指定天数
   * 
   * @param localDate 原日期
   * @param days      要增加的天数（可以为负数）
   * @return 新的日期
   */
  public static LocalDate addDays(LocalDate localDate, int days) {
    if (localDate == null) {
      return null;
    }

    try {
      LocalDate newDate = localDate.plusDays(days);
      logger.debug("本地日期增加天数: {} + {} 天 = {}", localDate, days, newDate);
      return newDate;
    } catch (Exception e) {
      logger.error("本地日期增加天数失败", e);
      return localDate;
    }
  }

  /**
   * 检查日期是否在今天之前
   * 
   * @param date 要检查的日期
   * @return 如果在今天之前返回true
   */
  public static boolean isBeforeToday(Date date) {
    if (date == null) {
      return false;
    }

    try {
      Date today = cn.hutool.core.date.DateUtil.beginOfDay(new Date());
      boolean result = date.before(today);
      logger.debug("检查日期是否在今天之前: {} -> {}", date, result);
      return result;
    } catch (Exception e) {
      logger.error("检查日期失败", e);
      return false;
    }
  }

  /**
   * 检查LocalDate是否在今天之前
   * 
   * @param localDate 要检查的日期
   * @return 如果在今天之前返回true
   */
  public static boolean isBeforeToday(LocalDate localDate) {
    if (localDate == null) {
      return false;
    }

    try {
      LocalDate today = LocalDate.now();
      boolean result = localDate.isBefore(today);
      logger.debug("检查本地日期是否在今天之前: {} -> {}", localDate, result);
      return result;
    } catch (Exception e) {
      logger.error("检查本地日期失败", e);
      return false;
    }
  }

  /**
   * 根据生日计算年龄
   * 
   * @param birthDate 生日
   * @return 年龄
   */
  public static int calculateAge(LocalDate birthDate) {
    if (birthDate == null) {
      return 0;
    }

    try {
      LocalDate today = LocalDate.now();
      int age = today.getYear() - birthDate.getYear();

      // 如果今年的生日还没到，年龄减1
      if (today.getMonthValue() < birthDate.getMonthValue() ||
          (today.getMonthValue() == birthDate.getMonthValue() &&
              today.getDayOfMonth() < birthDate.getDayOfMonth())) {
        age--;
      }

      logger.debug("计算年龄: 生日 {} -> 年龄 {}", birthDate, age);
      return Math.max(0, age);
    } catch (Exception e) {
      logger.error("计算年龄失败", e);
      return 0;
    }
  }
}