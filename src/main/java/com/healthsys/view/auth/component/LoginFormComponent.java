package com.healthsys.view.auth.component;

import com.healthsys.view.base.BasePanel;
import com.healthsys.viewmodel.auth.LoginViewModel;
import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 登录表单组件
 * 包含用户名、密码输入框和登录按钮
 */
public class LoginFormComponent extends BasePanel implements PropertyChangeListener {

  // UI组件
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JLabel errorLabel;
  private JProgressBar loadingBar;

  // ViewModel绑定
  private LoginViewModel viewModel;

  /**
   * 构造函数
   * 
   * @param viewModel 登录视图模型
   */
  public LoginFormComponent(LoginViewModel viewModel) {
    super();
    this.viewModel = viewModel;
    initializeComponents();
    setupLayout();
    bindViewModel();
    setupEventHandlers();
  }

  /**
   * 初始化UI组件
   */
  private void initializeComponents() {
    // 创建输入框
    usernameField = createTextField("请输入用户名");
    passwordField = createPasswordField("请输入密码");

    // 创建登录按钮
    loginButton = createPrimaryButton("登录");
    loginButton.setPreferredSize(new Dimension(INPUT_SIZE.width, 40));

    // 创建错误消息标签
    errorLabel = createErrorLabel("");
    errorLabel.setVisible(false);

    // 创建加载进度条
    loadingBar = new JProgressBar();
    loadingBar.setIndeterminate(true);
    loadingBar.setVisible(false);
    loadingBar.setPreferredSize(new Dimension(INPUT_SIZE.width, 4));
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    // 用户名输入区域
    JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    usernamePanel.setOpaque(false);
    usernamePanel.add(createLabel("用户名:"));
    usernamePanel.add(createVerticalStrut(5));

    JPanel usernameInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    usernameInputPanel.setOpaque(false);
    usernameInputPanel.add(usernameField);

    // 密码输入区域
    JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    passwordPanel.setOpaque(false);
    passwordPanel.add(createLabel("密码:"));
    passwordPanel.add(createVerticalStrut(5));

    JPanel passwordInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    passwordInputPanel.setOpaque(false);
    passwordInputPanel.add(passwordField);

    // 错误消息区域
    JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    errorPanel.setOpaque(false);
    errorPanel.add(errorLabel);

    // 登录按钮区域
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    buttonPanel.setOpaque(false);
    buttonPanel.add(loginButton);

    // 加载进度条区域
    JPanel loadingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    loadingPanel.setOpaque(false);
    loadingPanel.add(loadingBar);

    // 添加到主面板
    add(usernamePanel);
    add(createVerticalStrut(5));
    add(usernameInputPanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    add(passwordPanel);
    add(createVerticalStrut(5));
    add(passwordInputPanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    add(errorPanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    add(loadingPanel);
    add(createVerticalStrut(5));
    add(buttonPanel);
  }

  /**
   * 绑定ViewModel
   */
  private void bindViewModel() {
    if (viewModel != null) {
      // 监听ViewModel属性变化
      viewModel.addPropertyChangeListener(this);

      // 初始化UI状态
      updateFromViewModel();
    }
  }

  /**
   * 设置事件处理器
   */
  private void setupEventHandlers() {
    // 用户名输入框变化事件
    usernameField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateUsernameInViewModel();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateUsernameInViewModel();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateUsernameInViewModel();
      }
    });

    // 密码输入框变化事件
    passwordField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updatePasswordInViewModel();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updatePasswordInViewModel();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updatePasswordInViewModel();
      }
    });

    // 登录按钮点击事件
    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (viewModel != null) {
          viewModel.loginCommand();
        }
      }
    });

    // 密码框回车事件
    passwordField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (viewModel != null && viewModel.canLogin()) {
          viewModel.loginCommand();
        }
      }
    });

    // 用户名框回车事件
    usernameField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        passwordField.requestFocus();
      }
    });
  }

  /**
   * 更新ViewModel中的用户名
   */
  private void updateUsernameInViewModel() {
    if (viewModel != null) {
      String text = usernameField.getText();
      if (!text.equals(viewModel.getUsername())) {
        viewModel.setUsername(text);
      }
    }
  }

  /**
   * 更新ViewModel中的密码
   */
  private void updatePasswordInViewModel() {
    if (viewModel != null) {
      String text = new String(passwordField.getPassword());
      if (!text.equals(viewModel.getPassword())) {
        viewModel.setPassword(text);
      }
    }
  }

  /**
   * 从ViewModel更新UI状态
   */
  private void updateFromViewModel() {
    if (viewModel == null)
      return;

    // 更新输入框内容（避免循环更新）
    if (!usernameField.getText().equals(viewModel.getUsername())) {
      usernameField.setText(viewModel.getUsername());
    }

    if (!new String(passwordField.getPassword()).equals(viewModel.getPassword())) {
      passwordField.setText(viewModel.getPassword());
    }

    // 更新按钮状态
    loginButton.setEnabled(viewModel.canLogin());
    loginButton.setText(viewModel.isLoading() ? "登录中..." : "登录");

    // 更新加载状态
    loadingBar.setVisible(viewModel.isLoading());

    // 更新错误消息
    String errorMessage = viewModel.getErrorMessage();
    if (StrUtil.isNotBlank(errorMessage)) {
      errorLabel.setText(errorMessage);
      errorLabel.setVisible(true);
    } else {
      errorLabel.setVisible(false);
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
   * 获取绑定的ViewModel
   * 
   * @return LoginViewModel
   */
  public LoginViewModel getViewModel() {
    return viewModel;
  }

  /**
   * 设置ViewModel
   * 
   * @param viewModel 登录视图模型
   */
  public void setViewModel(LoginViewModel viewModel) {
    // 移除旧的监听器
    if (this.viewModel != null) {
      this.viewModel.removePropertyChangeListener(this);
    }

    // 设置新的ViewModel
    this.viewModel = viewModel;

    // 绑定新的ViewModel
    if (this.viewModel != null) {
      bindViewModel();
    }
  }

  /**
   * 清理资源
   */
  public void dispose() {
    if (viewModel != null) {
      viewModel.removePropertyChangeListener(this);
    }
  }

  /**
   * 请求焦点到用户名输入框
   */
  public void requestFocusOnUsername() {
    SwingUtilities.invokeLater(() -> {
      usernameField.requestFocus();
    });
  }
}