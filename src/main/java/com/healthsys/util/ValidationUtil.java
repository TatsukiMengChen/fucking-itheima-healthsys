package com.healthsys.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdcardUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 验证工具类
 * 提供各种数据格式验证功能
 * 使用Hutool的验证工具实现
 * 
 * @author AI健康管理系统开发团队
 */
public class ValidationUtil {

  private static final Logger logger = LoggerFactory.getLogger(ValidationUtil.class);

  // 正则表达式常量
  private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
  private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
  private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,16}$";
  private static final String CODE_REGEX = "^[a-zA-Z0-9_]{1,50}$";

  /**
   * 私有构造函数，防止实例化
   */
  private ValidationUtil() {
    throw new UnsupportedOperationException("工具类不能被实例化");
  }

  /**
   * 验证邮箱格式是否正确
   * 
   * @param email 邮箱地址
   * @return 如果格式正确返回true，否则返回false
   */
  public static boolean isValidEmail(String email) {
    if (StrUtil.isBlank(email)) {
      return false;
    }

    try {
      boolean isValid = ReUtil.isMatch(EMAIL_REGEX, email);
      logger.debug("邮箱验证结果: {} -> {}", email, isValid);
      return isValid;
    } catch (Exception e) {
      logger.error("验证邮箱格式时发生错误", e);
      return false;
    }
  }

  /**
   * 验证手机号格式是否正确
   * 
   * @param phone 手机号
   * @return 如果格式正确返回true，否则返回false
   */
  public static boolean isValidPhone(String phone) {
    if (StrUtil.isBlank(phone)) {
      return false;
    }

    try {
      boolean isValid = ReUtil.isMatch(PHONE_REGEX, phone);
      logger.debug("手机号验证结果: {} -> {}", phone, isValid);
      return isValid;
    } catch (Exception e) {
      logger.error("验证手机号格式时发生错误", e);
      return false;
    }
  }

  /**
   * 验证用户名格式是否正确
   * 用户名要求：3-16位，只能包含字母、数字、下划线
   * 
   * @param username 用户名
   * @return 如果格式正确返回true，否则返回false
   */
  public static boolean isValidUsername(String username) {
    if (StrUtil.isBlank(username)) {
      return false;
    }

    try {
      boolean isValid = ReUtil.isMatch(USERNAME_REGEX, username);
      logger.debug("用户名验证结果: {} -> {}", username, isValid);
      return isValid;
    } catch (Exception e) {
      logger.error("验证用户名格式时发生错误", e);
      return false;
    }
  }

  /**
   * 验证代码格式是否正确
   * 代码要求：1-50位，只能包含字母、数字、下划线
   * 
   * @param code 代码
   * @return 如果格式正确返回true，否则返回false
   */
  public static boolean isValidCode(String code) {
    if (StrUtil.isBlank(code)) {
      return false;
    }

    try {
      boolean isValid = ReUtil.isMatch(CODE_REGEX, code);
      logger.debug("代码验证结果: {} -> {}", code, isValid);
      return isValid;
    } catch (Exception e) {
      logger.error("验证代码格式时发生错误", e);
      return false;
    }
  }

  /**
   * 验证身份证号码是否正确
   * 
   * @param idCard 身份证号码
   * @return 如果格式正确返回true，否则返回false
   */
  public static boolean isValidIdCard(String idCard) {
    if (StrUtil.isBlank(idCard)) {
      return false;
    }

    try {
      boolean isValid = IdcardUtil.isValidCard(idCard);
      logger.debug("身份证验证结果: {} -> {}", idCard, isValid);
      return isValid;
    } catch (Exception e) {
      logger.error("验证身份证号码时发生错误", e);
      return false;
    }
  }

  /**
   * 验证日期格式是否正确
   * 
   * @param dateStr 日期字符串
   * @param pattern 日期格式模式，如"yyyy-MM-dd"
   * @return 如果格式正确返回true，否则返回false
   */
  public static boolean isValidDate(String dateStr, String pattern) {
    if (StrUtil.isBlank(dateStr) || StrUtil.isBlank(pattern)) {
      return false;
    }

    try {
      DateUtil.parse(dateStr, pattern);
      logger.debug("日期验证结果: {} -> true", dateStr);
      return true;
    } catch (Exception e) {
      logger.debug("日期验证结果: {} -> false", dateStr);
      return false;
    }
  }

  /**
   * 验证日期是否为有效的生日（不能是未来日期）
   * 
   * @param birthDate 生日
   * @return 如果是有效生日返回true，否则返回false
   */
  public static boolean isValidBirthDate(LocalDate birthDate) {
    if (birthDate == null) {
      return false;
    }

    try {
      LocalDate now = LocalDate.now();
      boolean isValid = !birthDate.isAfter(now) && birthDate.isAfter(LocalDate.of(1900, 1, 1));
      logger.debug("生日验证结果: {} -> {}", birthDate, isValid);
      return isValid;
    } catch (Exception e) {
      logger.error("验证生日时发生错误", e);
      return false;
    }
  }

  /**
   * 验证字符串长度是否在指定范围内
   * 
   * @param str       待验证字符串
   * @param minLength 最小长度
   * @param maxLength 最大长度
   * @return 如果长度在范围内返回true，否则返回false
   */
  public static boolean isValidLength(String str, int minLength, int maxLength) {
    if (str == null) {
      return minLength <= 0;
    }

    int length = str.length();
    boolean isValid = length >= minLength && length <= maxLength;
    logger.debug("字符串长度验证结果: {} (长度:{}) -> {}", str, length, isValid);
    return isValid;
  }

  /**
   * 验证数值是否在指定范围内
   * 
   * @param value 待验证数值
   * @param min   最小值
   * @param max   最大值
   * @return 如果在范围内返回true，否则返回false
   */
  public static boolean isValidRange(double value, double min, double max) {
    boolean isValid = value >= min && value <= max;
    logger.debug("数值范围验证结果: {} -> {}", value, isValid);
    return isValid;
  }

  /**
   * 验证字符串是否为有效的数字
   * 
   * @param str 待验证字符串
   * @return 如果是有效数字返回true，否则返回false
   */
  public static boolean isValidNumber(String str) {
    if (StrUtil.isBlank(str)) {
      return false;
    }

    try {
      Double.parseDouble(str);
      logger.debug("数字验证结果: {} -> true", str);
      return true;
    } catch (NumberFormatException e) {
      logger.debug("数字验证结果: {} -> false", str);
      return false;
    }
  }

  /**
   * 验证字符串是否为有效的整数
   * 
   * @param str 待验证字符串
   * @return 如果是有效整数返回true，否则返回false
   */
  public static boolean isValidInteger(String str) {
    if (StrUtil.isBlank(str)) {
      return false;
    }

    try {
      Integer.parseInt(str);
      logger.debug("整数验证结果: {} -> true", str);
      return true;
    } catch (NumberFormatException e) {
      logger.debug("整数验证结果: {} -> false", str);
      return false;
    }
  }

  /**
   * 验证性别字符串是否有效
   * 
   * @param gender 性别字符串
   * @return 如果是有效性别返回true，否则返回false
   */
  public static boolean isValidGender(String gender) {
    if (StrUtil.isBlank(gender)) {
      return false;
    }

    boolean isValid = "男".equals(gender) || "女".equals(gender) ||
        "Male".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender) ||
        "M".equalsIgnoreCase(gender) || "F".equalsIgnoreCase(gender);

    logger.debug("性别验证结果: {} -> {}", gender, isValid);
    return isValid;
  }

  /**
   * 获取验证失败的错误信息
   * 
   * @param fieldName      字段名
   * @param value          字段值
   * @param validationType 验证类型
   * @return 错误信息
   */
  public static String getValidationErrorMessage(String fieldName, String value, String validationType) {
    return String.format("字段 '%s' 的值 '%s' 不符合 %s 格式要求", fieldName, value, validationType);
  }
}