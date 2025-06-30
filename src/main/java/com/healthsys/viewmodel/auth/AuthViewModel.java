package com.healthsys.viewmodel.auth;

import com.healthsys.model.entity.User;
import com.healthsys.service.IEmailService;
import com.healthsys.service.IUserService;
import com.healthsys.viewmodel.base.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * 认证主视图模型
 * 协调登录和注册两个子ViewModel的状态
 */
public class AuthViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(AuthViewModel.class);

  /**
   * 认证模式枚举
   */
  public enum AuthMode {
    LOGIN, // 登录模式
    REGISTER // 注册模式
  }

  // 子ViewModel
  private final LoginViewModel loginViewModel;
  private final RegistrationViewModel registrationViewModel;

  // 属性
  private AuthMode currentMode = AuthMode.LOGIN;
  private String pageTitle = "用户登录";
  private boolean isAuthenticating = false;

  // 回调函数
  private Consumer<User> authSuccessCallback;
  private Consumer<String> authErrorCallback;

  /**
   * 构造函数
   * 
   * @param userService  用户服务
   * @param emailService 邮件服务
   */
  public AuthViewModel(IUserService userService, IEmailService emailService) {
    // 初始化子ViewModel
    this.loginViewModel = new LoginViewModel(userService);
    this.registrationViewModel = new RegistrationViewModel(userService, emailService);

    // 设置子ViewModel的回调
    setupViewModelCallbacks();
  }

  /**
   * 设置子ViewModel的回调函数
   */
  private void setupViewModelCallbacks() {
    // 登录成功回调
    loginViewModel.setLoginSuccessCallback(user -> {
      logger.info("登录成功，用户: {}", user.getUsername());
      if (authSuccessCallback != null) {
        authSuccessCallback.accept(user);
      }
    });

    // 登录失败回调
    loginViewModel.setLoginErrorCallback(errorMsg -> {
      logger.info("登录失败: {}", errorMsg);
      if (authErrorCallback != null) {
        authErrorCallback.accept(errorMsg);
      }
    });

    // 注册成功回调
    registrationViewModel.setRegisterSuccessCallback(() -> {
      logger.info("注册成功，切换到登录模式");
      // 注册成功后自动切换到登录页面
      switchToLogin();
    });

    // 注册失败回调
    registrationViewModel.setRegisterErrorCallback(errorMsg -> {
      logger.info("注册失败: {}", errorMsg);
      if (authErrorCallback != null) {
        authErrorCallback.accept(errorMsg);
      }
    });

    // 监听loading状态变化
    loginViewModel.addPropertyChangeListener("loading", evt -> {
      updateAuthenticatingState();
    });

    registrationViewModel.addPropertyChangeListener("loading", evt -> {
      updateAuthenticatingState();
    });

    registrationViewModel.addPropertyChangeListener("sendingCode", evt -> {
      updateAuthenticatingState();
    });
  }

  /**
   * 更新认证状态
   */
  private void updateAuthenticatingState() {
    boolean newState = loginViewModel.isLoading() ||
        registrationViewModel.isLoading() ||
        registrationViewModel.isSendingCode();
    setAuthenticating(newState);
  }

  // Getter和Setter方法

  public AuthMode getCurrentMode() {
    return currentMode;
  }

  public void setCurrentMode(AuthMode mode) {
    if (mode != null && mode != this.currentMode) {
      AuthMode oldMode = this.currentMode;
      this.currentMode = mode;

      // 更新页面标题
      updatePageTitle();

      // 清空子ViewModel的状态
      clearViewModelStates();

      firePropertyChange("currentMode", oldMode, mode);
      logger.debug("认证模式切换: {} -> {}", oldMode, mode);
    }
  }

  public String getPageTitle() {
    return pageTitle;
  }

  private void setPageTitle(String title) {
    this.pageTitle = setProperty(this.pageTitle, title, "pageTitle");
  }

  public boolean isAuthenticating() {
    return isAuthenticating;
  }

  private void setAuthenticating(boolean authenticating) {
    this.isAuthenticating = setProperty(this.isAuthenticating, authenticating, "authenticating");
  }

  public LoginViewModel getLoginViewModel() {
    return loginViewModel;
  }

  public RegistrationViewModel getRegistrationViewModel() {
    return registrationViewModel;
  }

  // 公共方法

  /**
   * 切换到登录模式
   */
  public void switchToLogin() {
    setCurrentMode(AuthMode.LOGIN);
  }

  /**
   * 切换到注册模式
   */
  public void switchToRegister() {
    setCurrentMode(AuthMode.REGISTER);
  }

  /**
   * 是否为登录模式
   */
  public boolean isLoginMode() {
    return currentMode == AuthMode.LOGIN;
  }

  /**
   * 是否为注册模式
   */
  public boolean isRegisterMode() {
    return currentMode == AuthMode.REGISTER;
  }

  /**
   * 获取模式切换按钮文本
   */
  public String getSwitchModeButtonText() {
    return isLoginMode() ? "没有账号？立即注册" : "已有账号？立即登录";
  }

  /**
   * 获取提交按钮文本
   */
  public String getSubmitButtonText() {
    if (isLoginMode()) {
      return loginViewModel.isLoading() ? "登录中..." : "登录";
    } else {
      return registrationViewModel.isLoading() ? "注册中..." : "注册";
    }
  }

  /**
   * 检查提交按钮是否可用
   */
  public boolean canSubmit() {
    if (isAuthenticating) {
      return false;
    }

    if (isLoginMode()) {
      return loginViewModel.canLogin();
    } else {
      return registrationViewModel.canRegister();
    }
  }

  /**
   * 执行提交命令（登录或注册）
   */
  public void submitCommand() {
    if (isLoginMode()) {
      loginViewModel.loginCommand();
    } else {
      registrationViewModel.registerCommand();
    }
  }

  /**
   * 设置认证成功回调
   * 
   * @param callback 回调函数
   */
  public void setAuthSuccessCallback(Consumer<User> callback) {
    this.authSuccessCallback = callback;
  }

  /**
   * 设置认证错误回调
   * 
   * @param callback 回调函数
   */
  public void setAuthErrorCallback(Consumer<String> callback) {
    this.authErrorCallback = callback;
  }

  /**
   * 更新页面标题
   */
  private void updatePageTitle() {
    switch (currentMode) {
      case LOGIN:
        setPageTitle("用户登录");
        break;
      case REGISTER:
        setPageTitle("用户注册");
        break;
      default:
        setPageTitle("用户认证");
        break;
    }
  }

  /**
   * 清空子ViewModel状态
   */
  private void clearViewModelStates() {
    if (isLoginMode()) {
      // 切换到登录模式时，清空注册ViewModel状态
      registrationViewModel.clearForm();
    } else {
      // 切换到注册模式时，清空登录ViewModel状态
      loginViewModel.clearForm();
    }
  }

  /**
   * 重置所有状态
   */
  public void reset() {
    setCurrentMode(AuthMode.LOGIN);
    loginViewModel.clearForm();
    registrationViewModel.clearForm();
    setAuthenticating(false);
  }

  /**
   * 资源清理
   */
  public void dispose() {
    registrationViewModel.dispose();
    // 清理回调
    authSuccessCallback = null;
    authErrorCallback = null;
  }
}