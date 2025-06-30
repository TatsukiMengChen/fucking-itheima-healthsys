package com.healthsys.view.auth;

import com.healthsys.view.auth.component.LoginFormComponent;
import com.healthsys.view.auth.component.RegistrationFormComponent;
import com.healthsys.view.base.BasePanel;
import com.healthsys.viewmodel.auth.AuthViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 认证主面板
 * 作为容器，根据AuthViewModel的状态动态显示登录或注册组件
 */
public class AuthPanel extends BasePanel implements PropertyChangeListener {

  // ViewModel绑定
  private AuthViewModel authViewModel;

  // UI组件
  private JLabel titleLabel;
  private JPanel contentPanel;
  private LoginFormComponent loginFormComponent;
  private RegistrationFormComponent registrationFormComponent;
  private JButton switchModeButton;
  private JPanel footerPanel;

  // 当前显示的组件
  private JPanel currentFormComponent;

  /**
   * 构造函数
   * 
   * @param authViewModel 认证视图模型
   */
  public AuthPanel(AuthViewModel authViewModel) {
    super(new BorderLayout());
    this.authViewModel = authViewModel;
    initializeComponents();
    setupLayout();
    bindViewModel();
    setupEventHandlers();
  }

  /**
   * 初始化UI组件
   */
  private void initializeComponents() {
    // 创建标题标签
    titleLabel = createTitleLabel("用户登录");

    // 创建内容面板
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setOpaque(false);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

    // 创建表单组件
    loginFormComponent = new LoginFormComponent(authViewModel.getLoginViewModel());
    registrationFormComponent = new RegistrationFormComponent(authViewModel.getRegistrationViewModel());

    // 创建模式切换按钮
    switchModeButton = createLinkButton("没有账号？立即注册");

    // 创建底部面板
    footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    footerPanel.setOpaque(false);
    footerPanel.add(switchModeButton);

    // 初始显示登录组件
    currentFormComponent = loginFormComponent;
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    // 创建主容器
    JPanel mainContainer = new JPanel();
    mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
    mainContainer.setOpaque(false);
    mainContainer.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

    // 标题区域
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    titlePanel.setOpaque(false);
    titlePanel.add(titleLabel);

    // 内容区域设置
    contentPanel.add(currentFormComponent, BorderLayout.CENTER);

    // 组装主容器
    mainContainer.add(titlePanel);
    mainContainer.add(createVerticalStrut(30));
    mainContainer.add(contentPanel);
    mainContainer.add(createVerticalStrut(20));
    mainContainer.add(footerPanel);

    // 添加到主面板（居中显示）
    add(createCenterPanel(mainContainer), BorderLayout.CENTER);

    // 设置背景
    setBackground(Color.WHITE);
  }

  /**
   * 绑定ViewModel
   */
  private void bindViewModel() {
    if (authViewModel != null) {
      // 监听ViewModel属性变化
      authViewModel.addPropertyChangeListener(this);

      // 初始化UI状态
      updateFromViewModel();
    }
  }

  /**
   * 设置事件处理器
   */
  private void setupEventHandlers() {
    // 模式切换按钮点击事件
    switchModeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (authViewModel != null) {
          if (authViewModel.isLoginMode()) {
            authViewModel.switchToRegister();
          } else {
            authViewModel.switchToLogin();
          }
        }
      }
    });
  }

  /**
   * 从ViewModel更新UI状态
   */
  private void updateFromViewModel() {
    if (authViewModel == null)
      return;

    // 更新标题
    titleLabel.setText(authViewModel.getPageTitle());

    // 更新切换按钮文本
    switchModeButton.setText(authViewModel.getSwitchModeButtonText());

    // 切换显示的表单组件
    switchFormComponent();
  }

  /**
   * 切换表单组件
   */
  private void switchFormComponent() {
    JPanel newFormComponent;

    if (authViewModel.isLoginMode()) {
      newFormComponent = loginFormComponent;
    } else {
      newFormComponent = registrationFormComponent;
    }

    // 如果需要切换组件
    if (currentFormComponent != newFormComponent) {
      // 移除当前组件
      contentPanel.remove(currentFormComponent);

      // 添加新组件
      contentPanel.add(newFormComponent, BorderLayout.CENTER);
      currentFormComponent = newFormComponent;

      // 刷新布局
      contentPanel.revalidate();
      contentPanel.repaint();

      // 请求焦点到合适的输入框
      SwingUtilities.invokeLater(() -> {
        if (authViewModel.isLoginMode()) {
          loginFormComponent.requestFocusOnUsername();
        } else {
          registrationFormComponent.requestFocusOnEmail();
        }
      });
    }
  }

  /**
   * 处理ViewModel属性变化事件
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(this::updateFromViewModel);
  }

  /**
   * 获取绑定的AuthViewModel
   * 
   * @return AuthViewModel
   */
  public AuthViewModel getAuthViewModel() {
    return authViewModel;
  }

  /**
   * 设置AuthViewModel
   * 
   * @param authViewModel 认证视图模型
   */
  public void setAuthViewModel(AuthViewModel authViewModel) {
    // 移除旧的监听器
    if (this.authViewModel != null) {
      this.authViewModel.removePropertyChangeListener(this);
    }

    // 设置新的ViewModel
    this.authViewModel = authViewModel;

    // 更新子组件的ViewModel
    if (this.authViewModel != null) {
      loginFormComponent.setViewModel(this.authViewModel.getLoginViewModel());
      registrationFormComponent.setViewModel(this.authViewModel.getRegistrationViewModel());

      // 绑定新的ViewModel
      bindViewModel();
    }
  }

  /**
   * 重置认证状态
   */
  public void reset() {
    if (authViewModel != null) {
      authViewModel.reset();
    }
  }

  /**
   * 清理资源
   */
  public void dispose() {
    if (authViewModel != null) {
      authViewModel.removePropertyChangeListener(this);
      authViewModel.dispose();
    }

    if (loginFormComponent != null) {
      loginFormComponent.dispose();
    }

    if (registrationFormComponent != null) {
      registrationFormComponent.dispose();
    }
  }

  /**
   * 获取当前显示的模式
   * 
   * @return 认证模式
   */
  public AuthViewModel.AuthMode getCurrentMode() {
    return authViewModel != null ? authViewModel.getCurrentMode() : AuthViewModel.AuthMode.LOGIN;
  }

  /**
   * 强制切换到登录模式
   */
  public void forceLoginMode() {
    if (authViewModel != null) {
      authViewModel.switchToLogin();
    }
  }

  /**
   * 强制切换到注册模式
   */
  public void forceRegisterMode() {
    if (authViewModel != null) {
      authViewModel.switchToRegister();
    }
  }
}