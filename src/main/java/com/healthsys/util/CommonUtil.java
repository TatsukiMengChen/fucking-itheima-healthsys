package com.healthsys.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.collection.CollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 通用工具类。
 * 提供常用的静态工具方法。
 * 
 * @author 梦辰
 */
public class CommonUtil {

  private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

  /**
   * 私有构造函数，防止实例化
   */
  private CommonUtil() {
    throw new UnsupportedOperationException("工具类不能被实例化");
  }

  /**
   * 生成随机验证码
   * 
   * @param length 验证码长度
   * @return 验证码字符串
   */
  public static String generateVerificationCode(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("验证码长度必须大于0");
    }

    try {
      String code = RandomUtil.randomNumbers(length);
      logger.debug("生成验证码成功，长度: {}", length);
      return code;
    } catch (Exception e) {
      logger.error("生成验证码失败", e);
      return "";
    }
  }

  /**
   * 生成6位数字验证码
   * 
   * @return 6位验证码
   */
  public static String generateVerificationCode() {
    return generateVerificationCode(6);
  }

  /**
   * 生成随机字符串ID
   * 
   * @param prefix 前缀
   * @param length 随机部分长度
   * @return 生成的ID
   */
  public static String generateRandomId(String prefix, int length) {
    try {
      String randomPart = RandomUtil.randomStringUpper(length);
      String id = StrUtil.isBlank(prefix) ? randomPart : prefix + randomPart;
      logger.debug("生成随机ID: {}", id);
      return id;
    } catch (Exception e) {
      logger.error("生成随机ID失败", e);
      return "";
    }
  }

  /**
   * 脱敏处理手机号
   * 将手机号中间4位替换为星号
   * 
   * @param phone 手机号
   * @return 脱敏后的手机号
   */
  public static String desensitizePhone(String phone) {
    if (StrUtil.isBlank(phone) || phone.length() != 11) {
      return phone;
    }

    try {
      String masked = phone.substring(0, 3) + "****" + phone.substring(7);
      logger.debug("手机号脱敏: {} -> {}", phone, masked);
      return masked;
    } catch (Exception e) {
      logger.error("手机号脱敏失败", e);
      return phone;
    }
  }

  /**
   * 脱敏处理身份证号
   * 将身份证号中间部分替换为星号
   * 
   * @param idCard 身份证号
   * @return 脱敏后的身份证号
   */
  public static String desensitizeIdCard(String idCard) {
    if (StrUtil.isBlank(idCard)) {
      return idCard;
    }

    try {
      String masked;
      if (idCard.length() == 18) {
        masked = idCard.substring(0, 6) + "********" + idCard.substring(14);
      } else if (idCard.length() == 15) {
        masked = idCard.substring(0, 6) + "*****" + idCard.substring(11);
      } else {
        return idCard;
      }
      logger.debug("身份证脱敏: {} -> {}", idCard, masked);
      return masked;
    } catch (Exception e) {
      logger.error("身份证脱敏失败", e);
      return idCard;
    }
  }

  /**
   * 脱敏处理邮箱
   * 将@符号前的部分进行脱敏
   * 
   * @param email 邮箱
   * @return 脱敏后的邮箱
   */
  public static String desensitizeEmail(String email) {
    if (StrUtil.isBlank(email) || !email.contains("@")) {
      return email;
    }

    try {
      String[] parts = email.split("@");
      String username = parts[0];
      String domain = parts[1];

      String maskedUsername;
      if (username.length() <= 2) {
        maskedUsername = "*".repeat(username.length());
      } else {
        maskedUsername = username.substring(0, 1) + "*".repeat(username.length() - 2)
            + username.substring(username.length() - 1);
      }

      String masked = maskedUsername + "@" + domain;
      logger.debug("邮箱脱敏: {} -> {}", email, masked);
      return masked;
    } catch (Exception e) {
      logger.error("邮箱脱敏失败", e);
      return email;
    }
  }

  /**
   * 判断字符串是否为空或空白
   * 
   * @param str 字符串
   * @return 如果为空或空白返回true
   */
  public static boolean isEmpty(String str) {
    return StrUtil.isBlank(str);
  }

  /**
   * 判断字符串是否不为空且不为空白
   * 
   * @param str 字符串
   * @return 如果不为空且不为空白返回true
   */
  public static boolean isNotEmpty(String str) {
    return StrUtil.isNotBlank(str);
  }

  /**
   * 判断集合是否为空
   * 
   * @param collection 集合
   * @return 如果为空返回true
   */
  public static boolean isEmpty(List<?> collection) {
    return CollUtil.isEmpty(collection);
  }

  /**
   * 判断集合是否不为空
   * 
   * @param collection 集合
   * @return 如果不为空返回true
   */
  public static boolean isNotEmpty(List<?> collection) {
    return CollUtil.isNotEmpty(collection);
  }

  /**
   * 安全地转换字符串为整数
   * 
   * @param str          字符串
   * @param defaultValue 默认值
   * @return 转换结果，失败时返回默认值
   */
  public static Integer safeParseInt(String str, Integer defaultValue) {
    if (StrUtil.isBlank(str)) {
      return defaultValue;
    }

    try {
      return Integer.parseInt(str.trim());
    } catch (NumberFormatException e) {
      logger.warn("字符串转整数失败: {}, 使用默认值: {}", str, defaultValue);
      return defaultValue;
    }
  }

  /**
   * 安全地转换字符串为双精度浮点数
   * 
   * @param str          字符串
   * @param defaultValue 默认值
   * @return 转换结果，失败时返回默认值
   */
  public static Double safeParseDouble(String str, Double defaultValue) {
    if (StrUtil.isBlank(str)) {
      return defaultValue;
    }

    try {
      return Double.parseDouble(str.trim());
    } catch (NumberFormatException e) {
      logger.warn("字符串转双精度失败: {}, 使用默认值: {}", str, defaultValue);
      return defaultValue;
    }
  }

  /**
   * 获取友好的文件大小显示
   * 
   * @param bytes 字节数
   * @return 友好的文件大小字符串
   */
  public static String formatFileSize(long bytes) {
    if (bytes < 0) {
      return "0 B";
    }

    String[] units = { "B", "KB", "MB", "GB", "TB" };
    double size = bytes;
    int unitIndex = 0;

    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024;
      unitIndex++;
    }

    return String.format("%.2f %s", size, units[unitIndex]);
  }

  /**
   * 截取字符串，超出部分用省略号表示
   * 
   * @param str       原字符串
   * @param maxLength 最大长度
   * @return 截取后的字符串
   */
  public static String truncateString(String str, int maxLength) {
    if (StrUtil.isBlank(str) || str.length() <= maxLength) {
      return str;
    }

    return str.substring(0, maxLength - 3) + "...";
  }

  /**
   * 检查对象是否为null
   * 
   * @param obj 对象
   * @return 如果为null返回true
   */
  public static boolean isNull(Object obj) {
    return obj == null;
  }

  /**
   * 检查对象是否不为null
   * 
   * @param obj 对象
   * @return 如果不为null返回true
   */
  public static boolean isNotNull(Object obj) {
    return obj != null;
  }

  /**
   * 获取默认值（如果对象为null）
   * 
   * @param obj          对象
   * @param defaultValue 默认值
   * @param <T>          泛型类型
   * @return 对象本身或默认值
   */
  public static <T> T getOrDefault(T obj, T defaultValue) {
    return obj != null ? obj : defaultValue;
  }
}