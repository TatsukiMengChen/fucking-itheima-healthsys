package com.healthsys.view.auth.component;

import com.healthsys.view.base.BasePanel;
import com.healthsys.viewmodel.auth.RegistrationViewModel;
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
 * 注册表单组件
 * 包含邮箱、验证码、用户名、密码等输入框和注册相关按钮
 */
public class RegistrationFormComponent extends BasePanel implements PropertyChangeListener {

  // UI组件
  private JTextField emailField;
  private JTextField verificationCodeField;
  private JButton sendCodeButton;
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JPasswordField confirmPasswordField;
  private JButton registerButton;
  private JLabel messageLabel;
  private JProgressBar loadingBar;

  // ViewModel绑定
  private RegistrationViewModel viewModel;

  /**
   * 构造函数
   * 
   * @param viewModel 注册视图模型
   */
  public RegistrationFormComponent(RegistrationViewModel viewModel) {
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
    emailField = createTextField("请输入邮箱地址");
    verificationCodeField = createTextField("请输入验证码");
    verificationCodeField.setPreferredSize(new Dimension(150, INPUT_SIZE.height));

    usernameField = createTextField("请输入用户名");
    passwordField = createPasswordField("请输入密码");
    confirmPasswordField = createPasswordField("请再次输入密码");

    // 创建按钮
    sendCodeButton = createSecondaryButton("发送验证码");
    sendCodeButton.setPreferredSize(new Dimension(100, INPUT_SIZE.height));

    registerButton = createPrimaryButton("注册");
    registerButton.setPreferredSize(new Dimension(INPUT_SIZE.width, 40));

    // 创建消息标签
    messageLabel = new JLabel();
    messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    messageLabel.setVisible(false);

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

    // 邮箱输入区域
    JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    emailLabelPanel.setOpaque(false);
    emailLabelPanel.add(createLabel("邮箱地址:"));
    add(emailLabelPanel);
    add(createVerticalStrut(5));
    JPanel emailInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    emailInputPanel.setOpaque(false);
    emailInputPanel.add(emailField);
    add(emailInputPanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    // 验证码输入区域
    JPanel codeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    codeLabelPanel.setOpaque(false);
    codeLabelPanel.add(createLabel("验证码:"));
    add(codeLabelPanel);
    add(createVerticalStrut(5));
    JPanel codeInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    codeInputPanel.setOpaque(false);
    codeInputPanel.add(verificationCodeField);
    codeInputPanel.add(createHorizontalStrut(10));
    codeInputPanel.add(sendCodeButton);
    add(codeInputPanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    // 用户名输入区域
    JPanel usernameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    usernameLabelPanel.setOpaque(false);
    usernameLabelPanel.add(createLabel("用户名:"));
    add(usernameLabelPanel);
    add(createVerticalStrut(5));
    JPanel usernameInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    usernameInputPanel.setOpaque(false);
    usernameInputPanel.add(usernameField);
    add(usernameInputPanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    // 密码输入区域
    JPanel passwordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    passwordLabelPanel.setOpaque(false);
    passwordLabelPanel.add(createLabel("密码:"));
    add(passwordLabelPanel);
    add(createVerticalStrut(5));
    JPanel passwordInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    passwordInputPanel.setOpaque(false);
    passwordInputPanel.add(passwordField);
    add(passwordInputPanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    // 确认密码输入区域
    JPanel confirmPasswordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    confirmPasswordLabelPanel.setOpaque(false);
    confirmPasswordLabelPanel.add(createLabel("确认密码:"));
    add(confirmPasswordLabelPanel);
    add(createVerticalStrut(5));
    JPanel confirmPasswordInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    confirmPasswordInputPanel.setOpaque(false);
    confirmPasswordInputPanel.add(confirmPasswordField);
    add(confirmPasswordInputPanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    // 消息显示区域
    JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    messagePanel.setOpaque(false);
    messagePanel.add(messageLabel);
    add(messagePanel);
    add(createVerticalStrut(COMPONENT_SPACING));

    // 加载进度条区域
    JPanel loadingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    loadingPanel.setOpaque(false);
    loadingPanel.add(loadingBar);
    add(loadingPanel);
    add(createVerticalStrut(5));

    // 注册按钮区域
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    buttonPanel.setOpaque(false);
    buttonPanel.add(registerButton);
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
    // 邮箱输入框变化事件
    emailField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateEmailInViewModel();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateEmailInViewModel();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateEmailInViewModel();
      }
    });

    // 验证码输入框变化事件
    verificationCodeField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateVerificationCodeInViewModel();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateVerificationCodeInViewModel();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateVerificationCodeInViewModel();
      }
    });

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

    // 确认密码输入框变化事件
    confirmPasswordField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateConfirmPasswordInViewModel();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateConfirmPasswordInViewModel();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateConfirmPasswordInViewModel();
      }
    });

    // 发送验证码按钮点击事件
    sendCodeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (viewModel != null) {
          viewModel.sendCodeCommand();
        }
      }
    });

    // 注册按钮点击事件
    registerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (viewModel != null) {
          viewModel.registerCommand();
        }
      }
    });

    // 回车事件处理
    setupEnterKeyHandlers();
  }

  /**
   * 设置回车键处理
   */
  private void setupEnterKeyHandlers() {
    emailField.addActionListener(e -> verificationCodeField.requestFocus());
    verificationCodeField.addActionListener(e -> usernameField.requestFocus());
    usernameField.addActionListener(e -> passwordField.requestFocus());
    passwordField.addActionListener(e -> confirmPasswordField.requestFocus());
    confirmPasswordField.addActionListener(e -> {
      if (viewModel != null && viewModel.canRegister()) {
        viewModel.registerCommand();
      }
    });
  }

  /**
   * 更新ViewModel中的邮箱
   */
  private void updateEmailInViewModel() {
    if (viewModel != null) {
      String text = emailField.getText();
      if (!text.equals(viewModel.getEmail())) {
        viewModel.setEmail(text);
      }
    }
  }

  /**
   * 更新ViewModel中的验证码
   */
  private void updateVerificationCodeInViewModel() {
    if (viewModel != null) {
      String text = verificationCodeField.getText();
      if (!text.equals(viewModel.getVerificationCode())) {
        viewModel.setVerificationCode(text);
      }
    }
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
   * 更新ViewModel中的确认密码
   */
  private void updateConfirmPasswordInViewModel() {
    if (viewModel != null) {
      String text = new String(confirmPasswordField.getPassword());
      if (!text.equals(viewModel.getConfirmPassword())) {
        viewModel.setConfirmPassword(text);
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
    updateFieldIfChanged(emailField, viewModel.getEmail());
    updateFieldIfChanged(verificationCodeField, viewModel.getVerificationCode());
    updateFieldIfChanged(usernameField, viewModel.getUsername());
    updatePasswordFieldIfChanged(passwordField, viewModel.getPassword());
    updatePasswordFieldIfChanged(confirmPasswordField, viewModel.getConfirmPassword());

    // 更新发送验证码按钮
    sendCodeButton.setEnabled(viewModel.canSendCode());
    sendCodeButton.setText(viewModel.getSendCodeButtonText());

    // 更新注册按钮
    registerButton.setEnabled(viewModel.canRegister());
    registerButton.setText(viewModel.isLoading() ? "注册中..." : "注册");

    // 更新加载状态
    boolean isLoading = viewModel.isLoading() || viewModel.isSendingCode();
    loadingBar.setVisible(isLoading);

    // 更新消息显示
    updateMessageDisplay();
  }

  /**
   * 更新文本字段内容（如果有变化）
   */
  private void updateFieldIfChanged(JTextField field, String newValue) {
    if (newValue == null)
      newValue = "";
    if (!field.getText().equals(newValue)) {
      field.setText(newValue);
    }
  }

  /**
   * 更新密码字段内容（如果有变化）
   */
  private void updatePasswordFieldIfChanged(JPasswordField field, String newValue) {
    if (newValue == null)
      newValue = "";
    if (!new String(field.getPassword()).equals(newValue)) {
      field.setText(newValue);
    }
  }

  /**
   * 更新消息显示
   */
  private void updateMessageDisplay() {
    String errorMessage = viewModel.getErrorMessage();
    String successMessage = viewModel.getSuccessMessage();

    if (StrUtil.isNotBlank(errorMessage)) {
      messageLabel.setText(errorMessage);
      messageLabel.setForeground(ERROR_COLOR);
      messageLabel.setVisible(true);
    } else if (StrUtil.isNotBlank(successMessage)) {
      messageLabel.setText(successMessage);
      messageLabel.setForeground(SUCCESS_COLOR);
      messageLabel.setVisible(true);
    } else {
      messageLabel.setVisible(false);
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
   * @return RegistrationViewModel
   */
  public RegistrationViewModel getViewModel() {
    return viewModel;
  }

  /**
   * 设置ViewModel
   * 
   * @param viewModel 注册视图模型
   */
  public void setViewModel(RegistrationViewModel viewModel) {
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
      viewModel.dispose();
    }
  }

  /**
   * 请求焦点到邮箱输入框
   */
  public void requestFocusOnEmail() {
    SwingUtilities.invokeLater(() -> {
      emailField.requestFocus();
    });
  }
}