package com.healthsys.view.admin.usermanagement.component;

import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.viewmodel.admin.usermanagement.UserEditViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * 用户专业信息表单组件
 * 包含科室、职级、角色等专业信息字段。
 *
 * @author 梦辰
 */
public class UserProfessionalInfoFormComponent extends JPanel implements PropertyChangeListener {

  private final UserEditViewModel viewModel;

  // UI 组件
  private JTextField depField;
  private JTextField levField;
  private JTextField avatarField;
  private JComboBox<UserRoleEnum> roleComboBox;

  // 标签
  private JLabel depLabel;
  private JLabel levLabel;
  private JLabel avatarLabel;
  private JLabel roleLabel;
  private JLabel roleHintLabel;

  /**
   * 构造函数
   *
   * @param viewModel 用户编辑ViewModel
   */
  public UserProfessionalInfoFormComponent(UserEditViewModel viewModel) {
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
    depLabel = new JLabel("科室:");
    levLabel = new JLabel("职级:");
    avatarLabel = new JLabel("头像路径:");
    roleLabel = new JLabel("用户角色:");

    // 创建输入框
    depField = new JTextField(15);
    levField = new JTextField(15);
    avatarField = new JTextField(15);

    // 角色下拉框
    roleComboBox = new JComboBox<>();

    // 角色提示标签
    roleHintLabel = new JLabel();
    roleHintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));

    // 设置提示文本
    depField.setToolTipText("请输入所属科室");
    levField.setToolTipText("请输入职级或级别");
    avatarField.setToolTipText("请输入头像文件路径（可选）");

    // 设置标签样式
    setupLabelStyles();

    // 初始化角色选择
    updateRoleComboBox();
  }

  /**
   * 设置标签样式
   */
  private void setupLabelStyles() {
    Font labelFont = new Font("微软雅黑", Font.PLAIN, 12);

    depLabel.setFont(labelFont);
    levLabel.setFont(labelFont);
    avatarLabel.setFont(labelFont);
    roleLabel.setFont(labelFont);

    // 角色字段标记为重要
    roleLabel.setForeground(new Color(52, 152, 219));
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "专业信息"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // 第一行：科室和职级
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(depLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.5;
    add(depField, gbc);

    gbc.gridx = 2;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    add(levLabel, gbc);
    gbc.gridx = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.5;
    add(levField, gbc);

    // 第二行：头像路径（跨两列）
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    add(avatarLabel, gbc);
    gbc.gridx = 1;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    add(avatarField, gbc);

    // 第三行：用户角色
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    add(roleLabel, gbc);
    gbc.gridx = 1;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    add(roleComboBox, gbc);

    // 第四行：角色提示
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    add(roleHintLabel, gbc);
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 科室输入监听
    depField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setDep(depField.getText());
    }));

    // 职级输入监听
    levField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setLev(levField.getText());
    }));

    // 头像路径输入监听
    avatarField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setAvatar(avatarField.getText());
    }));

    // 角色选择监听
    roleComboBox.addActionListener(e -> {
      UserRoleEnum selectedRole = (UserRoleEnum) roleComboBox.getSelectedItem();
      if (selectedRole != null) {
        viewModel.setRole(selectedRole);
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
  }

  /**
   * 从ViewModel更新UI
   */
  private void updateFromViewModel() {
    depField.setText(viewModel.getDep());
    levField.setText(viewModel.getLev());
    avatarField.setText(viewModel.getAvatar());

    // 更新角色选择
    roleComboBox.setSelectedItem(viewModel.getRole());

    // 更新角色相关的UI状态
    updateRoleUI();
  }

  /**
   * 更新角色下拉框
   */
  private void updateRoleComboBox() {
    roleComboBox.removeAllItems();

    List<UserRoleEnum> availableRoles = viewModel.getAvailableRoles();
    for (UserRoleEnum role : availableRoles) {
      roleComboBox.addItem(role);
    }

    // 如果没有可用角色，则禁用角色选择
    boolean hasRoles = !availableRoles.isEmpty();
    roleComboBox.setEnabled(hasRoles && viewModel.canModifyRole());

    if (!hasRoles) {
      roleHintLabel.setText("当前用户无权限分配角色");
      roleHintLabel.setForeground(new Color(220, 53, 69));
    } else if (!viewModel.canModifyRole()) {
      roleHintLabel.setText("当前用户无权限修改此用户的角色");
      roleHintLabel.setForeground(new Color(255, 193, 7));
    } else {
      updateRoleHint();
    }
  }

  /**
   * 更新角色相关的UI状态
   */
  private void updateRoleUI() {
    // 更新角色下拉框状态
    roleComboBox.setEnabled(viewModel.canModifyRole());

    // 更新提示信息
    updateRoleHint();
  }

  /**
   * 更新角色提示信息
   */
  private void updateRoleHint() {
    if (!viewModel.canModifyRole()) {
      if (viewModel.isAddMode()) {
        roleHintLabel.setText("当前用户无权限分配角色，将使用默认角色");
      } else {
        roleHintLabel.setText("当前用户无权限修改此用户的角色");
      }
      roleHintLabel.setForeground(new Color(255, 193, 7));
    } else {
      UserRoleEnum selectedRole = (UserRoleEnum) roleComboBox.getSelectedItem();
      if (selectedRole != null) {
        String hint = getRoleDescription(selectedRole);
        roleHintLabel.setText(hint);
        roleHintLabel.setForeground(new Color(108, 117, 125));
      }
    }
  }

  /**
   * 获取角色描述
   *
   * @param role 角色
   * @return 角色描述
   */
  private String getRoleDescription(UserRoleEnum role) {
    switch (role) {
      case SUPER_ADMIN:
        return "超级管理员：拥有系统全部权限，包括用户管理和角色分配";
      case ADMIN:
        return "管理员：拥有大部分管理权限，可管理普通用户";
      case NORMAL_USER:
        return "普通用户：拥有基本功能权限，可使用健康管理功能";
      default:
        return "未知角色";
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(() -> {
      String propertyName = evt.getPropertyName();

      // 如果是角色相关的属性变化，需要更新角色UI
      if ("role".equals(propertyName) || "availableRoles".equals(propertyName)) {
        updateRoleComboBox();
      }

      updateFromViewModel();
    });
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