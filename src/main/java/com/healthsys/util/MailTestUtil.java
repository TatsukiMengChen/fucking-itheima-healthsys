package com.healthsys.util;

import com.healthsys.config.MailConfig;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件测试工具类。
 * 用于测试邮件发送功能。
 * 
 * @author 梦辰
 */
public class MailTestUtil {

  private static final Logger logger = LoggerFactory.getLogger(MailTestUtil.class);

  /**
   * 测试邮件发送
   * 
   * @param toEmail 收件人邮箱
   * @return boolean 是否发送成功
   */
  public static boolean testSendMail(String toEmail) {
    if (StrUtil.isBlank(toEmail)) {
      logger.error("收件人邮箱不能为空");
      return false;
    }

    try {
      logger.info("开始测试邮件发送到: {}", toEmail);

      // 获取邮件配置
      MailAccount mailAccount = MailConfig.getMailAccount();

      String subject = "【健康管理系统】邮件测试";
      String content = buildTestEmailContent();

      // 发送邮件
      MailUtil.send(mailAccount, toEmail, subject, content, false);

      logger.info("测试邮件发送成功!");
      return true;

    } catch (Exception e) {
      logger.error("测试邮件发送失败: {}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * 构建测试邮件内容
   */
  private static String buildTestEmailContent() {
    StringBuilder content = new StringBuilder();
    content.append("<html><body>");
    content.append("<h2>健康管理系统 - 邮件配置测试</h2>");
    content.append("<p>您好！</p>");
    content.append("<p>这是一封测试邮件，用于验证邮件配置是否正确。</p>");
    content.append("<p>如果您收到了这封邮件，说明邮件配置成功！</p>");
    content.append("<p>测试时间: ").append(java.time.LocalDateTime.now()).append("</p>");
    content.append("<br>");
    content.append("<p>此邮件由系统自动发送，请勿回复。</p>");
    content.append("<p>健康管理系统团队</p>");
    content.append("</body></html>");
    return content.toString();
  }

  /**
   * 运行完整的邮件测试
   */
  public static void runMailTest(String testEmail) {
    logger.info("=== 邮件功能测试开始 ===");

    // 1. 测试配置
    logger.info("1. 检查邮件配置...");
    if (!ConfigTestUtil.testMailConfig()) {
      logger.error("邮件配置检查失败，无法进行测试");
      return;
    }

    // 2. 测试发送
    logger.info("2. 测试邮件发送...");
    if (testSendMail(testEmail)) {
      logger.info("✅ 邮件测试成功！请检查您的邮箱收件箱");
    } else {
      logger.error("❌ 邮件测试失败！请检查配置和网络连接");
    }

    logger.info("=== 邮件功能测试结束 ===");
  }

  /**
   * 主方法 - 可以独立运行测试
   */
  public static void main(String[] args) {
    // 可以直接运行此类来测试邮件功能
    String testEmail = System.getProperty("test.email");

    if (StrUtil.isBlank(testEmail)) {
      System.out.println("请设置测试邮箱地址:");
      System.out
          .println("java -Dtest.email=your@email.com -cp build/classes/java/main com.healthsys.util.MailTestUtil");
      return;
    }

    runMailTest(testEmail);
  }
}