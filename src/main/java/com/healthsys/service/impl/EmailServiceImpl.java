package com.healthsys.service.impl;

import com.healthsys.service.IEmailService;
import com.healthsys.config.MailConfig;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 邮件服务实现。
 * 实现邮件发送与验证码相关的业务逻辑。
 * 
 * @author 梦辰
 */
public class EmailServiceImpl implements IEmailService {

  private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

  // 验证码过期时间（分钟）
  private static final int CODE_EXPIRE_MINUTES = 5;

  // 验证码缓存：key为邮箱地址，value为验证码信息
  private final Map<String, CodeInfo> verificationCodeCache = new ConcurrentHashMap<>();

  // 定时任务执行器，用于清理过期验证码
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  // 邮件账户配置（从MailConfig获取）
  private MailAccount mailAccount;

  /**
   * 验证码信息内部类
   */
  private static class CodeInfo {
    private final String code;
    private final long createTime;

    public CodeInfo(String code) {
      this.code = code;
      this.createTime = System.currentTimeMillis();
    }

    public String getCode() {
      return code;
    }

    public boolean isExpired() {
      long elapsedMinutes = (System.currentTimeMillis() - createTime) / (1000 * 60);
      return elapsedMinutes > CODE_EXPIRE_MINUTES;
    }
  }

  /**
   * 构造函数，启动定时清理任务
   */
  public EmailServiceImpl() {
    // 初始化邮件账户配置
    initMailAccount();

    // 每分钟清理一次过期的验证码
    scheduler.scheduleAtFixedRate(this::cleanExpiredCodes, 1, 1, TimeUnit.MINUTES);
  }

  @Override
  public boolean sendVerificationCode(String email, String verificationCode) {
    if (StrUtil.hasBlank(email, verificationCode)) {
      logger.warn("邮箱或验证码为空，无法发送");
      return false;
    }

    // 检查邮件配置是否可用
    if (mailAccount == null) {
      logger.error("邮件配置未初始化，无法发送邮件");
      return false;
    }

    try {
      String subject = "【健康管理系统】邮箱验证码";
      String content = buildEmailContent(verificationCode);

      // 使用Hutool发送邮件
      MailUtil.send(mailAccount, email, subject, content, false);

      // 缓存验证码
      verificationCodeCache.put(email, new CodeInfo(verificationCode));

      logger.info("验证码邮件发送成功，邮箱: {}", email);
      return true;

    } catch (Exception e) {
      logger.error("验证码邮件发送失败，邮箱: {}, 错误: {}", email, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public String generateVerificationCode() {
    // 生成6位随机数字验证码
    return RandomUtil.randomNumbers(6);
  }

  @Override
  public boolean verifyCode(String email, String inputCode) {
    if (StrUtil.hasBlank(email, inputCode)) {
      return false;
    }

    CodeInfo codeInfo = verificationCodeCache.get(email);
    if (codeInfo == null) {
      logger.info("验证码不存在，邮箱: {}", email);
      return false;
    }

    if (codeInfo.isExpired()) {
      // 验证码已过期，清除缓存
      verificationCodeCache.remove(email);
      logger.info("验证码已过期，邮箱: {}", email);
      return false;
    }

    boolean isValid = codeInfo.getCode().equals(inputCode);
    if (isValid) {
      logger.info("验证码验证成功，邮箱: {}", email);
    } else {
      logger.info("验证码验证失败，邮箱: {}", email);
    }

    return isValid;
  }

  @Override
  public void clearVerificationCode(String email) {
    if (StrUtil.isNotBlank(email)) {
      verificationCodeCache.remove(email);
      logger.debug("清除验证码缓存，邮箱: {}", email);
    }
  }

  /**
   * 初始化邮件账户配置
   */
  private void initMailAccount() {
    try {
      this.mailAccount = MailConfig.getMailAccount();
      logger.info("邮件服务初始化完成");
    } catch (Exception e) {
      logger.error("邮件服务初始化失败: {}", e.getMessage());
      // 不抛出异常，允许服务启动，但邮件功能不可用
      this.mailAccount = null;
    }
  }

  /**
   * 构建邮件内容
   */
  private String buildEmailContent(String verificationCode) {
    StringBuilder content = new StringBuilder();
    content.append("<html><body>");
    content.append("<h2>健康管理系统 - 邮箱验证</h2>");
    content.append("<p>您好！</p>");
    content.append("<p>您的验证码是：<strong style='color: #007cff; font-size: 18px;'>")
        .append(verificationCode)
        .append("</strong></p>");
    content.append("<p>验证码有效期为 ").append(CODE_EXPIRE_MINUTES).append(" 分钟，请及时使用。</p>");
    content.append("<p>如果这不是您本人的操作，请忽略此邮件。</p>");
    content.append("<br>");
    content.append("<p>此邮件由系统自动发送，请勿回复。</p>");
    content.append("<p>健康管理系统团队</p>");
    content.append("</body></html>");
    return content.toString();
  }

  /**
   * 清理过期的验证码
   */
  private void cleanExpiredCodes() {
    try {
      verificationCodeCache.entrySet().removeIf(entry -> {
        boolean expired = entry.getValue().isExpired();
        if (expired) {
          logger.debug("清理过期验证码，邮箱: {}", entry.getKey());
        }
        return expired;
      });
    } catch (Exception e) {
      logger.error("清理过期验证码时发生错误: {}", e.getMessage());
    }
  }

  /**
   * 关闭服务时清理资源
   */
  public void shutdown() {
    if (scheduler != null && !scheduler.isShutdown()) {
      scheduler.shutdown();
      try {
        if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
          scheduler.shutdownNow();
        }
      } catch (InterruptedException e) {
        scheduler.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
    verificationCodeCache.clear();
    logger.info("邮件服务已关闭");
  }
}