package com.healthsys.viewmodel.auth;

import com.healthsys.model.entity.User;
import com.healthsys.service.IUserService;
import com.healthsys.viewmodel.base.BaseViewModel;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 登录视图模型
 * 处理用户登录相关的数据和逻辑
 */
public class LoginViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(LoginViewModel.class);

  // 服务依赖
  private final IUserService userService;

  // 属性
  private String username = "";
  private String password = "";
  private boolean isLoading = false;
  private String errorMessage = "";
  private User currentUser = null;

  // 回调函数
  private Consumer<User> loginSuccessCallback;
  private Consumer<String> loginErrorCallback;

  /**
   * 构造函数
   * 
   * @param userService 用户服务
   */
  public LoginViewModel(IUserService userService) {
    this.userService = userService;
  }

  // Getter和Setter方法

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = setProperty(this.username, username == null ? "" : username, "username");
    // 清除错误消息
    if (StrUtil.isNotBlank(this.errorMessage)) {
      setErrorMessage("");
    }
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = setProperty(this.password, password == null ? "" : password, "password");
    // 清除错误消息
    if (StrUtil.isNotBlank(this.errorMessage)) {
      setErrorMessage("");
    }
  }

  public boolean isLoading() {
    return isLoading;
  }

  private void setLoading(boolean loading) {
    this.isLoading = setProperty(this.isLoading, loading, "loading");
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  private void setErrorMessage(String errorMessage) {
    this.errorMessage = setProperty(this.errorMessage, errorMessage == null ? "" : errorMessage, "errorMessage");
  }

  public User getCurrentUser() {
    return currentUser;
  }

  private void setCurrentUser(User user) {
    this.currentUser = setProperty(this.currentUser, user, "currentUser");
  }

  /**
   * 设置登录成功回调
   * 
   * @param callback 回调函数
   */
  public void setLoginSuccessCallback(Consumer<User> callback) {
    this.loginSuccessCallback = callback;
  }

  /**
   * 设置登录失败回调
   * 
   * @param callback 回调函数
   */
  public void setLoginErrorCallback(Consumer<String> callback) {
    this.loginErrorCallback = callback;
  }

  /**
   * 登录命令
   * 在后台线程执行登录逻辑，避免阻塞UI线程
   */
  public void loginCommand() {
    // 参数验证
    if (!validateInput()) {
      return;
    }

    setLoading(true);
    setErrorMessage("");

    // 在后台线程执行登录
    CompletableFuture.supplyAsync(() -> {
      try {
        logger.info("开始执行登录，用户名: {}", username);
        return userService.login(username, password);
      } catch (Exception e) {
        logger.error("登录过程中发生异常: {}", e.getMessage());
        throw new RuntimeException("登录失败：" + e.getMessage());
      }
    }).thenAccept(user -> {
      // 在EDT线程中更新UI
      javax.swing.SwingUtilities.invokeLater(() -> {
        setLoading(false);

        if (user != null) {
          // 登录成功
          setCurrentUser(user);
          logger.info("用户登录成功: {}", user.getUsername());

          // 清空密码（安全考虑）
          setPassword("");

          // 调用成功回调
          if (loginSuccessCallback != null) {
            loginSuccessCallback.accept(user);
          }
        } else {
          // 登录失败
          String errorMsg = "用户名或密码错误";
          setErrorMessage(errorMsg);
          logger.info("用户登录失败: {}", username);

          // 调用失败回调
          if (loginErrorCallback != null) {
            loginErrorCallback.accept(errorMsg);
          }
        }
      });
    }).exceptionally(throwable -> {
      // 异常处理
      javax.swing.SwingUtilities.invokeLater(() -> {
        setLoading(false);
        String errorMsg = "登录失败：" + throwable.getMessage();
        setErrorMessage(errorMsg);

        if (loginErrorCallback != null) {
          loginErrorCallback.accept(errorMsg);
        }
      });
      return null;
    });
  }

  /**
   * 验证输入参数
   * 
   * @return 验证结果
   */
  private boolean validateInput() {
    if (StrUtil.isBlank(username)) {
      setErrorMessage("请输入用户名");
      return false;
    }

    if (StrUtil.isBlank(password)) {
      setErrorMessage("请输入密码");
      return false;
    }

    if (username.length() < 3 || username.length() > 50) {
      setErrorMessage("用户名长度应在3-50个字符之间");
      return false;
    }

    if (password.length() < 6) {
      setErrorMessage("密码长度不能少于6位");
      return false;
    }

    return true;
  }

  /**
   * 清空表单数据
   */
  public void clearForm() {
    setUsername("");
    setPassword("");
    setErrorMessage("");
    setCurrentUser(null);
  }

  /**
   * 检查是否可以执行登录
   * 
   * @return 是否可以登录
   */
  public boolean canLogin() {
    return !isLoading && StrUtil.isNotBlank(username) && StrUtil.isNotBlank(password);
  }
}