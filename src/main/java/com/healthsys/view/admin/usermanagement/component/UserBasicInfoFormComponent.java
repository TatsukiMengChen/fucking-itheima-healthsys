package com.healthsys.view.admin.usermanagement.component;

import com.healthsys.viewmodel.admin.usermanagement.UserEditViewModel;
import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 用户基本信息表单组件
 * 包含用户名、密码、邮箱等基本登录信息
 */
public class UserBasicInfoFormComponent extends JPanel implements PropertyChangeListener {

  private final UserEditViewModel viewModel;

  // UI 组件
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JPasswordField confirmPasswordField;
  private JTextField emailField;

  // 标签
  private JLabel usernameLabel;
  private JLabel passwordLabel;
  private JLabel confirmPasswordLabel;
  private JLabel emailLabel;

  // 验证状态标签
  private JLabel usernameValidationLabel;
  private JLabel passwordValidationLabel;
  private JLabel confirmPasswordValidationLabel;
  private JLabel emailValidationLabel;

  /**
   * 构造函数
   *
   * @param viewModel 用户编辑ViewModel
   */
  public UserBasicInfoFormComponent(UserEditViewModel viewModel) {
    this.viewModel = viewModel;

    initializeComponents();
    setupLayout();
    setupEventListeners();
    setupDataBinding();
  }

  /**
   * 初始化UI组件
   */
  private void initializeComponents() {
    // 创建标签
    usernameLabel = new JLabel("用户名 *:");
    passwordLabel = new JLabel("密码 *:");
    confirmPasswordLabel = new JLabel("确认密码 *:");
    emailLabel = new JLabel("邮箱 *:");

    // 创建输入框
    usernameField = new JTextField(20);
    passwordField = new JPasswordField(20);
    confirmPasswordField = new JPasswordField(20);
    emailField = new JTextField(20);

    // 设置提示文本
    usernameField.setToolTipText("请输入3-50个字符的用户名");
    passwordField.setToolTipText("请输入至少6位密码");
    confirmPasswordField.setToolTipText("请再次输入密码");
    emailField.setToolTipText("请输入有效的邮箱地址");

    // 创建验证状态标签
    usernameValidationLabel = createValidationLabel();
    passwordValidationLabel = createValidationLabel();
    confirmPasswordValidationLabel = createValidationLabel();
    emailValidationLabel = createValidationLabel();

    // 设置标签样式
    setupLabelStyles();
  }

  /**
   * 创建验证状态标签
   *
   * @return 验证状态标签
   */
  private JLabel createValidationLabel() {
    JLabel label = new JLabel(" ");
    label.setFont(new Font("微软雅黑", Font.PLAIN, 10));
    label.setPreferredSize(new Dimension(200, 15));
    return label;
  }

  /**
   * 设置标签样式
   */
  private void setupLabelStyles() {
    Font labelFont = new Font("微软雅黑", Font.PLAIN, 12);

    usernameLabel.setFont(labelFont);
    passwordLabel.setFont(labelFont);
    confirmPasswordLabel.setFont(labelFont);
    emailLabel.setFont(labelFont);

    // 必填字段标记为红色
    Color requiredColor = new Color(220, 53, 69);
    usernameLabel.setForeground(requiredColor);
    passwordLabel.setForeground(requiredColor);
    confirmPasswordLabel.setForeground(requiredColor);
    emailLabel.setForeground(requiredColor);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "基本信息"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 2, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // 用户名行
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(usernameLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    add(usernameField, gbc);

    gbc.gridy = 1;
    gbc.gridx = 1;
    gbc.insets = new Insets(0, 5, 5, 5);
    add(usernameValidationLabel, gbc);

    // 密码行
    gbc.gridy = 2;
    gbc.gridx = 0;
    gbc.insets = new Insets(5, 5, 2, 5);
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    add(passwordLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    add(passwordField, gbc);

    gbc.gridy = 3;
    gbc.gridx = 1;
    gbc.insets = new Insets(0, 5, 5, 5);
    add(passwordValidationLabel, gbc);

    // 确认密码行
    gbc.gridy = 4;
    gbc.gridx = 0;
    gbc.insets = new Insets(5, 5, 2, 5);
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    add(confirmPasswordLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    add(confirmPasswordField, gbc);

    gbc.gridy = 5;
    gbc.gridx = 1;
    gbc.insets = new Insets(0, 5, 5, 5);
    add(confirmPasswordValidationLabel, gbc);

    // 邮箱行
    gbc.gridy = 6;
    gbc.gridx = 0;
    gbc.insets = new Insets(5, 5, 2, 5);
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    add(emailLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    add(emailField, gbc);

    gbc.gridy = 7;
    gbc.gridx = 1;
    gbc.insets = new Insets(0, 5, 10, 5);
    add(emailValidationLabel, gbc);
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 用户名验证
    usernameField.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        validateUsername();
      }
    });

    // 密码验证
    passwordField.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        validatePassword();
        validateConfirmPassword(); // 密码改变时也要重新验证确认密码
      }
    });

    // 确认密码验证
    confirmPasswordField.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        validateConfirmPassword();
      }
    });

    // 邮箱验证
    emailField.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        validateEmail();
      }
    });
  }

  /**
   * 设置数据绑定
   */
  private void setupDataBinding() {
    // 监听ViewModel属性变化
    viewModel.addPropertyChangeListener(this);

    // 初始化数据
    updateFromViewModel();

    // 设置输入框变化监听
    setupInputListeners();
  }

  /**
   * 设置输入框变化监听
   */
  private void setupInputListeners() {
    usernameField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setUsername(usernameField.getText());
    }));

    passwordField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setPassword(new String(passwordField.getPassword()));
    }));

    confirmPasswordField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setConfirmPassword(new String(confirmPasswordField.getPassword()));
    }));

    emailField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setEmail(emailField.getText());
    }));
  }

  /**
   * 从ViewModel更新UI
   */
  private void updateFromViewModel() {
    usernameField.setText(viewModel.getUsername());
    passwordField.setText(viewModel.getPassword());
    confirmPasswordField.setText(viewModel.getConfirmPassword());
    emailField.setText(viewModel.getEmail());

    // 编辑模式下密码字段的处理
    if (!viewModel.isAddMode()) {
      passwordLabel.setText("密码:");
      confirmPasswordLabel.setText("确认密码:");
      passwordField.setToolTipText("留空表示不修改密码");
      confirmPasswordField.setToolTipText("留空表示不修改密码");
    }
  }

  // 验证方法

  /**
   * 验证用户名
   */
  private void validateUsername() {
    String username = usernameField.getText().trim();

    if (StrUtil.isBlank(username)) {
      showValidationError(usernameValidationLabel, "用户名不能为空");
    } else if (username.length() < 3) {
      showValidationError(usernameValidationLabel, "用户名至少3个字符");
    } else if (username.length() > 50) {
      showValidationError(usernameValidationLabel, "用户名不能超过50个字符");
    } else {
      showValidationSuccess(usernameValidationLabel, "用户名格式正确");
    }
  }

  /**
   * 验证密码
   */
  private void validatePassword() {
    String password = new String(passwordField.getPassword());

    // 编辑模式下密码可以为空
    if (!viewModel.isAddMode() && StrUtil.isBlank(password)) {
      showValidationInfo(passwordValidationLabel, "留空表示不修改密码");
      return;
    }

    if (StrUtil.isBlank(password)) {
      showValidationError(passwordValidationLabel, "密码不能为空");
    } else if (password.length() < 6) {
      showValidationError(passwordValidationLabel, "密码至少6位");
    } else {
      showValidationSuccess(passwordValidationLabel, "密码格式正确");
    }
  }

  /**
   * 验证确认密码
   */
  private void validateConfirmPassword() {
    String password = new String(passwordField.getPassword());
    String confirmPassword = new String(confirmPasswordField.getPassword());

    // 编辑模式下密码可以为空
    if (!viewModel.isAddMode() && StrUtil.isBlank(password) && StrUtil.isBlank(confirmPassword)) {
      showValidationInfo(confirmPasswordValidationLabel, "留空表示不修改密码");
      return;
    }

    if (!password.equals(confirmPassword)) {
      showValidationError(confirmPasswordValidationLabel, "两次输入的密码不一致");
    } else if (StrUtil.isNotBlank(confirmPassword)) {
      showValidationSuccess(confirmPasswordValidationLabel, "密码确认正确");
    }
  }

  /**
   * 验证邮箱
   */
  private void validateEmail() {
    String email = emailField.getText().trim();

    if (StrUtil.isBlank(email)) {
      showValidationError(emailValidationLabel, "邮箱不能为空");
    } else if (!isValidEmail(email)) {
      showValidationError(emailValidationLabel, "邮箱格式不正确");
    } else {
      showValidationSuccess(emailValidationLabel, "邮箱格式正确");
    }
  }

  /**
   * 简单的邮箱格式验证
   *
   * @param email 邮箱地址
   * @return 是否有效
   */
  private boolean isValidEmail(String email) {
    return email.contains("@") && email.contains(".") &&
        email.indexOf("@") < email.lastIndexOf(".");
  }

  // 验证状态显示方法

  private void showValidationError(JLabel label, String message) {
    label.setText("✗ " + message);
    label.setForeground(new Color(220, 53, 69));
  }

  private void showValidationSuccess(JLabel label, String message) {
    label.setText("✓ " + message);
    label.setForeground(new Color(40, 167, 69));
  }

  private void showValidationInfo(JLabel label, String message) {
    label.setText("ℹ " + message);
    label.setForeground(new Color(23, 162, 184));
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(this::updateFromViewModel);
  }

  /**
   * 简单的文档监听器
   */
  private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
    private final Runnable action;

    public SimpleDocumentListener(Runnable action) {
      this.action = action;
    }

    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
      SwingUtilities.invokeLater(action);
    }

    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
      SwingUtilities.invokeLater(action);
    }

    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
      SwingUtilities.invokeLater(action);
    }
  }
}