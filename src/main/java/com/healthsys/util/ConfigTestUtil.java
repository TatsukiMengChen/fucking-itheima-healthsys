package com.healthsys.util;

import com.healthsys.config.MailConfig;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置测试工具类
 * 用于验证应用配置是否正确
 * 
 * @author AI健康管理系统开发团队
 */
public class ConfigTestUtil {

  private static final Logger logger = LoggerFactory.getLogger(ConfigTestUtil.class);

  /**
   * 测试邮件配置
   * 
   * @return boolean 配置是否正确
   */
  public static boolean testMailConfig() {
    logger.info("开始测试邮件配置...");

    try {
      // 检查邮件配置是否有效
      if (!MailConfig.isConfigValid()) {
        logger.error("邮件配置验证失败");
        return false;
      }

      // 获取邮件账户配置
      MailAccount account = MailConfig.getMailAccount();
      logger.info("邮件配置测试结果:");
      logger.info("  SMTP服务器: {}", account.getHost());
      logger.info("  端口: {}", account.getPort());
      logger.info("  用户名: {}", account.getUser());
      logger.info("  密码: {}", StrUtil.isBlank(account.getPass()) ? "未配置" : "已配置");
      logger.info("  STARTTLS: {}", account.isStarttlsEnable());
      logger.info("  SSL: {}", account.isSslEnable());

      logger.info("邮件配置测试通过");
      return true;

    } catch (Exception e) {
      logger.error("邮件配置测试失败: {}", e.getMessage());
      return false;
    }
  }

  /**
   * 打印环境变量信息
   */
  public static void printEnvironmentInfo() {
    logger.info("环境变量信息:");

    String mailUsername = System.getenv("SPRING_MAIL_USERNAME");
    String mailPassword = System.getenv("SPRING_MAIL_PASSWORD");

    logger.info("  SPRING_MAIL_USERNAME: {}",
        StrUtil.isBlank(mailUsername) ? "未设置" : "已设置");
    logger.info("  SPRING_MAIL_PASSWORD: {}",
        StrUtil.isBlank(mailPassword) ? "未设置" : "已设置");

    // 检查系统属性
    String propUsername = System.getProperty("spring.mail.username");
    String propPassword = System.getProperty("spring.mail.password");

    logger.info("系统属性信息:");
    logger.info("  spring.mail.username: {}",
        StrUtil.isBlank(propUsername) ? "未设置" : "已设置");
    logger.info("  spring.mail.password: {}",
        StrUtil.isBlank(propPassword) ? "未设置" : "已设置");
  }

  /**
   * 提供配置建议
   */
  public static void printConfigSuggestions() {
    logger.info("邮件配置建议:");
    logger.info("1. 创建.env文件在项目根目录，内容如下:");
    logger.info("   SPRING_MAIL_USERNAME=your_qq_email@qq.com");
    logger.info("   SPRING_MAIL_PASSWORD=your_qq_email_auth_code");
    logger.info("");
    logger.info("2. 或者直接修改application.properties文件:");
    logger.info("   spring.mail.username=your_qq_email@qq.com");
    logger.info("   spring.mail.password=your_qq_email_auth_code");
    logger.info("");
    logger.info("3. 或者在IDE中设置环境变量:");
    logger.info("   Run -> Edit Configurations -> Environment Variables");
    logger.info("");
    logger.info("注意：密码应该是QQ邮箱的16位授权码，不是QQ登录密码！");
  }

  /**
   * 测试数据库配置
   */
  public static boolean testDatabaseConfig() {
    logger.info("开始测试数据库配置...");

    try {
      // 测试数据库连接
      com.healthsys.config.DataAccessManager dataAccessManager = com.healthsys.config.DataAccessManager.getInstance();

      if (dataAccessManager.testConnection()) {
        logger.info("数据库配置测试通过");
        return true;
      } else {
        logger.error("数据库连接测试失败");
        return false;
      }

    } catch (Exception e) {
      logger.error("数据库配置测试失败: {}", e.getMessage());
      return false;
    }
  }

  /**
   * 运行所有配置测试
   */
  public static void runAllTests() {
    logger.info("=== 配置测试开始 ===");

    printEnvironmentInfo();
    logger.info("");

    // 测试邮件配置
    boolean mailConfigOk = testMailConfig();
    if (!mailConfigOk) {
      logger.info("");
      printConfigSuggestions();
    }

    logger.info("");

    // 测试数据库配置
    boolean dbConfigOk = testDatabaseConfig();

    logger.info("");
    logger.info("=== 配置测试结果 ===");
    logger.info("邮件配置: {}", mailConfigOk ? "✅ 通过" : "❌ 失败");
    logger.info("数据库配置: {}", dbConfigOk ? "✅ 通过" : "❌ 失败");

    logger.info("=== 配置测试结束 ===");
  }
}