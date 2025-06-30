package com.healthsys.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 密码工具类。
 * 提供密码加密、校验等相关方法。
 * 
 * @author 梦辰
 */
public class PasswordUtil {

  private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

  /**
   * 私有构造函数，防止实例化
   */
  private PasswordUtil() {
    throw new UnsupportedOperationException("工具类不能被实例化");
  }

  /**
   * 使用BCrypt算法加密密码
   * 
   * @param plainPassword 明文密码
   * @return 加密后的密码hash
   * @throws IllegalArgumentException 如果密码为空
   */
  public static String encryptPassword(String plainPassword) {
    if (StrUtil.isBlank(plainPassword)) {
      throw new IllegalArgumentException("密码不能为空");
    }

    try {
      String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
      logger.debug("密码加密成功");
      return hashedPassword;
    } catch (Exception e) {
      logger.error("密码加密失败", e);
      throw new RuntimeException("密码加密失败", e);
    }
  }

  /**
   * 验证密码是否正确
   * 
   * @param plainPassword  明文密码
   * @param hashedPassword 加密后的密码hash
   * @return 如果密码正确返回true，否则返回false
   */
  public static boolean verifyPassword(String plainPassword, String hashedPassword) {
    if (StrUtil.isBlank(plainPassword) || StrUtil.isBlank(hashedPassword)) {
      logger.warn("密码验证失败：密码或hash为空");
      return false;
    }

    try {
      boolean isValid = BCrypt.checkpw(plainPassword, hashedPassword);
      logger.debug("密码验证结果: {}", isValid);
      return isValid;
    } catch (Exception e) {
      logger.error("密码验证过程中发生错误", e);
      return false;
    }
  }

  /**
   * 使用MD5算法加密密码（不推荐用于新系统，仅用于兼容性）
   * 
   * @param plainPassword 明文密码
   * @return MD5加密后的密码
   * @deprecated 建议使用BCrypt算法
   */
  @Deprecated
  public static String encryptPasswordMD5(String plainPassword) {
    if (StrUtil.isBlank(plainPassword)) {
      throw new IllegalArgumentException("密码不能为空");
    }

    try {
      String hashedPassword = SecureUtil.md5(plainPassword);
      logger.debug("MD5密码加密成功");
      return hashedPassword;
    } catch (Exception e) {
      logger.error("MD5密码加密失败", e);
      throw new RuntimeException("MD5密码加密失败", e);
    }
  }

  /**
   * 验证密码强度
   * 
   * @param password 密码
   * @return 密码强度等级：0-弱，1-中，2-强
   */
  public static int checkPasswordStrength(String password) {
    if (StrUtil.isBlank(password)) {
      return 0;
    }

    int score = 0;

    // 长度检查
    if (password.length() >= 8) {
      score++;
    }

    // 包含数字
    if (password.matches(".*\\d.*")) {
      score++;
    }

    // 包含大写字母
    if (password.matches(".*[A-Z].*")) {
      score++;
    }

    // 包含小写字母
    if (password.matches(".*[a-z].*")) {
      score++;
    }

    // 包含特殊字符
    if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
      score++;
    }

    // 计算强度等级
    if (score >= 4) {
      return 2; // 强
    } else if (score >= 2) {
      return 1; // 中
    } else {
      return 0; // 弱
    }
  }

  /**
   * 获取密码强度描述
   * 
   * @param password 密码
   * @return 密码强度描述
   */
  public static String getPasswordStrengthDescription(String password) {
    int strength = checkPasswordStrength(password);
    switch (strength) {
      case 0:
        return "弱";
      case 1:
        return "中";
      case 2:
        return "强";
      default:
        return "未知";
    }
  }

  /**
   * 生成随机密码
   * 
   * @param length 密码长度
   * @return 随机密码
   */
  public static String generateRandomPassword(int length) {
    if (length < 4) {
      throw new IllegalArgumentException("密码长度不能小于4位");
    }

    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    StringBuilder password = new StringBuilder();

    for (int i = 0; i < length; i++) {
      int index = (int) (Math.random() * chars.length());
      password.append(chars.charAt(index));
    }

    return password.toString();
  }
}