package com.healthsys.viewmodel.auth;

import com.healthsys.service.IEmailService;
import com.healthsys.service.IUserService;
import com.healthsys.viewmodel.base.BaseViewModel;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 注册视图模型
 * 处理用户注册相关的数据和逻辑
 */
public class RegistrationViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(RegistrationViewModel.class);

  // 服务依赖
  private final IUserService userService;
  private final IEmailService emailService;

  // 属性
  private String email = "";
  private String verificationCode = "";
  private String username = "";
  private String password = "";
  private String confirmPassword = "";
  private boolean isLoading = false;
  private boolean isSendingCode = false;
  private String errorMessage = "";
  private String successMessage = "";

  // 验证码倒计时相关
  private int countdownSeconds = 0;
  private boolean canSendCode = true;
  private Timer countdownTimer;

  // 回调函数
  private Runnable registerSuccessCallback;
  private Consumer<String> registerErrorCallback;
  private Consumer<String> sendCodeSuccessCallback;
  private Consumer<String> sendCodeErrorCallback;

  /**
   * 构造函数
   * 
   * @param userService  用户服务
   * @param emailService 邮件服务
   */
  public RegistrationViewModel(IUserService userService, IEmailService emailService) {
    this.userService = userService;
    this.emailService = emailService;
    initCountdownTimer();
  }

  /**
   * 初始化倒计时定时器
   */
  private void initCountdownTimer() {
    countdownTimer = new Timer(1000, e -> {
      if (countdownSeconds > 0) {
        setCountdownSeconds(countdownSeconds - 1);
      } else {
        countdownTimer.stop();
        setCanSendCode(true);
      }
    });
  }

  // Getter和Setter方法

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = setProperty(this.email, email == null ? "" : email, "email");
    clearMessages();
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  public void setVerificationCode(String verificationCode) {
    this.verificationCode = setProperty(this.verificationCode,
        verificationCode == null ? "" : verificationCode, "verificationCode");
    clearMessages();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = setProperty(this.username, username == null ? "" : username, "username");
    clearMessages();
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = setProperty(this.password, password == null ? "" : password, "password");
    clearMessages();
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = setProperty(this.confirmPassword,
        confirmPassword == null ? "" : confirmPassword, "confirmPassword");
    clearMessages();
  }

  public boolean isLoading() {
    return isLoading;
  }

  private void setLoading(boolean loading) {
    this.isLoading = setProperty(this.isLoading, loading, "loading");
  }

  public boolean isSendingCode() {
    return isSendingCode;
  }

  private void setSendingCode(boolean sendingCode) {
    this.isSendingCode = setProperty(this.isSendingCode, sendingCode, "sendingCode");
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  private void setErrorMessage(String errorMessage) {
    this.errorMessage = setProperty(this.errorMessage, errorMessage == null ? "" : errorMessage, "errorMessage");
  }

  public String getSuccessMessage() {
    return successMessage;
  }

  private void setSuccessMessage(String successMessage) {
    this.successMessage = setProperty(this.successMessage,
        successMessage == null ? "" : successMessage, "successMessage");
  }

  public int getCountdownSeconds() {
    return countdownSeconds;
  }

  private void setCountdownSeconds(int countdownSeconds) {
    this.countdownSeconds = setProperty(this.countdownSeconds, countdownSeconds, "countdownSeconds");
  }

  public boolean canSendCode() {
    return canSendCode && !isSendingCode;
  }

  private void setCanSendCode(boolean canSendCode) {
    this.canSendCode = setProperty(this.canSendCode, canSendCode, "canSendCode");
  }

  /**
   * 获取发送验证码按钮的文本
   */
  public String getSendCodeButtonText() {
    if (isSendingCode) {
      return "发送中...";
    } else if (countdownSeconds > 0) {
      return countdownSeconds + "秒后重发";
    } else {
      return "发送验证码";
    }
  }

  // 回调设置方法

  public void setRegisterSuccessCallback(Runnable callback) {
    this.registerSuccessCallback = callback;
  }

  public void setRegisterErrorCallback(Consumer<String> callback) {
    this.registerErrorCallback = callback;
  }

  public void setSendCodeSuccessCallback(Consumer<String> callback) {
    this.sendCodeSuccessCallback = callback;
  }

  public void setSendCodeErrorCallback(Consumer<String> callback) {
    this.sendCodeErrorCallback = callback;
  }

  /**
   * 发送验证码命令
   */
  public void sendCodeCommand() {
    // 验证邮箱
    if (!validateEmail()) {
      return;
    }

    setSendingCode(true);
    setErrorMessage("");

    CompletableFuture.supplyAsync(() -> {
      try {
        // 生成验证码
        String code = emailService.generateVerificationCode();
        boolean success = emailService.sendVerificationCode(email, code);

        if (success) {
          logger.info("验证码发送成功，邮箱: {}", email);
          return "验证码发送成功，请查收邮件";
        } else {
          throw new RuntimeException("验证码发送失败，请稍后重试");
        }
      } catch (Exception e) {
        logger.error("发送验证码异常: {}", e.getMessage());
        throw new RuntimeException("发送验证码失败：" + e.getMessage());
      }
    }).thenAccept(message -> {
      javax.swing.SwingUtilities.invokeLater(() -> {
        setSendingCode(false);
        setSuccessMessage(message);

        // 开始倒计时
        setCountdownSeconds(60);
        setCanSendCode(false);
        countdownTimer.start();

        if (sendCodeSuccessCallback != null) {
          sendCodeSuccessCallback.accept(message);
        }
      });
    }).exceptionally(throwable -> {
      javax.swing.SwingUtilities.invokeLater(() -> {
        setSendingCode(false);
        String errorMsg = throwable.getMessage();
        setErrorMessage(errorMsg);

        if (sendCodeErrorCallback != null) {
          sendCodeErrorCallback.accept(errorMsg);
        }
      });
      return null;
    });
  }

  /**
   * 注册命令
   */
  public void registerCommand() {
    // 参数验证
    if (!validateAllInput()) {
      return;
    }

    setLoading(true);
    setErrorMessage("");

    CompletableFuture.supplyAsync(() -> {
      try {
        logger.info("开始执行注册，用户名: {}, 邮箱: {}", username, email);
        boolean success = userService.register(username, password, email, verificationCode);

        if (success) {
          logger.info("用户注册成功: {}", username);
          return true;
        } else {
          throw new RuntimeException("注册失败，请稍后重试");
        }
      } catch (Exception e) {
        logger.error("注册过程中发生异常: {}", e.getMessage());
        throw new RuntimeException(e.getMessage());
      }
    }).thenAccept(success -> {
      javax.swing.SwingUtilities.invokeLater(() -> {
        setLoading(false);

        if (success) {
          setSuccessMessage("注册成功！请使用新账号登录");
          // 清空表单
          clearForm();

          if (registerSuccessCallback != null) {
            registerSuccessCallback.run();
          }
        }
      });
    }).exceptionally(throwable -> {
      javax.swing.SwingUtilities.invokeLater(() -> {
        setLoading(false);
        String errorMsg = throwable.getMessage();
        setErrorMessage(errorMsg);

        if (registerErrorCallback != null) {
          registerErrorCallback.accept(errorMsg);
        }
      });
      return null;
    });
  }

  /**
   * 验证邮箱格式
   */
  private boolean validateEmail() {
    if (StrUtil.isBlank(email)) {
      setErrorMessage("请输入邮箱地址");
      return false;
    }

    if (!isValidEmail(email)) {
      setErrorMessage("邮箱格式不正确");
      return false;
    }

    return true;
  }

  /**
   * 验证所有输入参数
   */
  private boolean validateAllInput() {
    if (!validateEmail()) {
      return false;
    }

    if (StrUtil.isBlank(verificationCode)) {
      setErrorMessage("请输入验证码");
      return false;
    }

    if (verificationCode.length() != 6) {
      setErrorMessage("验证码应为6位数字");
      return false;
    }

    if (StrUtil.isBlank(username)) {
      setErrorMessage("请输入用户名");
      return false;
    }

    if (username.length() < 3 || username.length() > 50) {
      setErrorMessage("用户名长度应在3-50个字符之间");
      return false;
    }

    if (StrUtil.isBlank(password)) {
      setErrorMessage("请输入密码");
      return false;
    }

    if (password.length() < 6) {
      setErrorMessage("密码长度不能少于6位");
      return false;
    }

    if (StrUtil.isBlank(confirmPassword)) {
      setErrorMessage("请确认密码");
      return false;
    }

    if (!password.equals(confirmPassword)) {
      setErrorMessage("两次输入的密码不一致");
      return false;
    }

    return true;
  }

  /**
   * 简单的邮箱格式验证
   */
  private boolean isValidEmail(String email) {
    return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  }

  /**
   * 清除消息
   */
  private void clearMessages() {
    if (StrUtil.isNotBlank(errorMessage)) {
      setErrorMessage("");
    }
    if (StrUtil.isNotBlank(successMessage)) {
      setSuccessMessage("");
    }
  }

  /**
   * 清空表单数据
   */
  public void clearForm() {
    setEmail("");
    setVerificationCode("");
    setUsername("");
    setPassword("");
    setConfirmPassword("");
    setErrorMessage("");
    setSuccessMessage("");

    // 停止倒计时
    if (countdownTimer.isRunning()) {
      countdownTimer.stop();
    }
    setCountdownSeconds(0);
    setCanSendCode(true);
  }

  /**
   * 检查是否可以执行注册
   */
  public boolean canRegister() {
    return !isLoading &&
        StrUtil.isNotBlank(email) &&
        StrUtil.isNotBlank(verificationCode) &&
        StrUtil.isNotBlank(username) &&
        StrUtil.isNotBlank(password) &&
        StrUtil.isNotBlank(confirmPassword);
  }

  /**
   * 资源清理
   */
  public void dispose() {
    if (countdownTimer != null) {
      countdownTimer.stop();
    }
  }
}