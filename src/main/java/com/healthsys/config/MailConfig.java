package com.healthsys.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 邮件服务配置类。
 * 配置邮件发送相关参数。
 * 
 * @author 梦辰
 */
public class MailConfig {

  private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);
  private static volatile MailAccount mailAccount;
  private static final Object lock = new Object();

  /**
   * 获取邮件账户配置实例（单例模式）
   * 
   * @return MailAccount 邮件账户配置
   */
  public static MailAccount getMailAccount() {
    if (mailAccount == null) {
      synchronized (lock) {
        if (mailAccount == null) {
          mailAccount = createMailAccount();
        }
      }
    }
    return mailAccount;
  }

  /**
   * 创建邮件账户配置
   * 
   * @return MailAccount 邮件账户配置
   */
  private static MailAccount createMailAccount() {
    try {
      Properties props = loadProperties();

      // 从环境变量或配置文件获取邮件配置
      String host = props.getProperty("spring.mail.host", "smtp.qq.com");
      String port = props.getProperty("spring.mail.port", "587");
      String username = getConfigValue("SPRING_MAIL_USERNAME",
          props.getProperty("spring.mail.username", ""));
      String password = getConfigValue("SPRING_MAIL_PASSWORD",
          props.getProperty("spring.mail.password", ""));

      // 清理和验证邮件地址
      username = StrUtil.trim(username);
      password = StrUtil.trim(password);

      // 验证必要的配置
      if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
        String errorMsg = "邮件配置不完整，请检查环境变量或配置文件中的用户名和密码";
        logger.error(errorMsg);
        logger.error("当前配置 - username: {}, password: {}",
            StrUtil.isBlank(username) ? "未配置" : "已配置",
            StrUtil.isBlank(password) ? "未配置" : "已配置");
        throw new RuntimeException(errorMsg);
      }

      // 验证邮件地址格式
      if (!isValidEmail(username)) {
        String errorMsg = "邮件地址格式不正确: " + username;
        logger.error(errorMsg);
        throw new RuntimeException(errorMsg);
      }

      // 创建邮件账户
      MailAccount account = new MailAccount();
      account.setHost(host);
      account.setPort(Integer.parseInt(port));
      account.setAuth(true);
      account.setFrom(username);
      account.setUser(username);
      account.setPass(password);
      account.setStarttlsEnable(true);
      account.setSslEnable(false);

      logger.debug("邮件账户配置详情:");
      logger.debug("  Host: {}", account.getHost());
      logger.debug("  Port: {}", account.getPort());
      logger.debug("  From: {}", account.getFrom());
      logger.debug("  User: {}", account.getUser());

      logger.info("邮件账户配置初始化成功 - SMTP服务器: {}, 端口: {}, 用户: {}",
          host, port, username);
      return account;

    } catch (Exception e) {
      logger.error("初始化邮件账户配置失败: {}", e.getMessage());
      throw new RuntimeException("邮件服务初始化失败", e);
    }
  }

  /**
   * 加载配置文件
   * 
   * @return Properties 配置属性
   */
  private static Properties loadProperties() {
    Properties props = new Properties();
    try (InputStream is = MailConfig.class.getClassLoader()
        .getResourceAsStream("application.properties")) {
      if (is != null) {
        props.load(is);
        logger.info("成功加载应用配置文件");
      } else {
        logger.warn("未找到application.properties文件，使用默认配置");
      }
    } catch (IOException e) {
      logger.warn("加载配置文件失败，使用默认配置", e);
    }
    return props;
  }

  /**
   * 获取配置值，优先从环境变量获取
   * 
   * @param envKey       环境变量键名
   * @param defaultValue 默认值
   * @return 配置值
   */
  private static String getConfigValue(String envKey, String defaultValue) {
    // 首先尝试从环境变量获取
    String envValue = System.getenv(envKey);
    if (StrUtil.isNotBlank(envValue)) {
      logger.debug("从环境变量获取配置: {} = ***", envKey);
      return envValue;
    }

    // 尝试从系统属性获取（支持IDE运行时配置）
    String propValue = System.getProperty(envKey.toLowerCase().replace('_', '.'));
    if (StrUtil.isNotBlank(propValue)) {
      logger.debug("从系统属性获取配置: {} = ***", envKey);
      return propValue;
    }

    // 使用默认值
    if (StrUtil.isNotBlank(defaultValue)) {
      logger.debug("使用配置文件中的默认值: {} = ***", envKey);
    } else {
      logger.warn("配置项 {} 未设置，且无默认值", envKey);
    }

    return defaultValue;
  }

  /**
   * 验证邮件配置是否正确
   * 
   * @return boolean 配置是否有效
   */
  public static boolean isConfigValid() {
    try {
      MailAccount account = getMailAccount();
      return StrUtil.isNotBlank(account.getUser()) &&
          StrUtil.isNotBlank(account.getPass()) &&
          StrUtil.isNotBlank(account.getHost());
    } catch (Exception e) {
      logger.error("验证邮件配置失败: {}", e.getMessage());
      return false;
    }
  }

  /**
   * 验证邮件地址格式
   */
  private static boolean isValidEmail(String email) {
    if (StrUtil.isBlank(email)) {
      return false;
    }

    // 简单的邮件格式验证
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    return email.matches(emailRegex);
  }

  /**
   * 重新加载配置（用于配置更新后的重新初始化）
   */
  public static void reloadConfig() {
    synchronized (lock) {
      mailAccount = null;
      logger.info("邮件配置已重置，将在下次使用时重新加载");
    }
  }
}