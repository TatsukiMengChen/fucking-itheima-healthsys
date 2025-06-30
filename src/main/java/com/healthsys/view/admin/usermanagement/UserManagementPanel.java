package com.healthsys.view.admin.usermanagement;

import com.healthsys.config.AppContext;
import com.healthsys.model.entity.User;
import com.healthsys.service.IUserService;
import com.healthsys.view.admin.usermanagement.component.UserTableComponent;
import com.healthsys.view.admin.usermanagement.component.UserBasicInfoFormComponent;
import com.healthsys.view.admin.usermanagement.component.UserPersonalInfoFormComponent;
import com.healthsys.view.admin.usermanagement.component.UserProfessionalInfoFormComponent;
import com.healthsys.view.common.NotificationComponent;
import com.healthsys.viewmodel.admin.usermanagement.UserManagementViewModel;
import com.healthsys.viewmodel.admin.usermanagement.UserEditViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * 用户管理主面板
 * 组合用户表格和编辑功能，提供完整的用户管理界面
 */
public class UserManagementPanel extends JPanel {

  private final UserManagementViewModel managementViewModel;
  private final IUserService userService;
  private final NotificationComponent notificationComponent;

  // UI 组件
  private UserTableComponent userTableComponent;

  /**
   * 构造函数
   *
   * @param managementViewModel 用户管理ViewModel
   * @param userService         用户服务
   */
  public UserManagementPanel(UserManagementViewModel managementViewModel, IUserService userService) {
    this.managementViewModel = managementViewModel;
    this.userService = userService;
    this.notificationComponent = new NotificationComponent();

    initializeComponents();
    setupLayout();
    setupEventListeners();
  }

  /**
   * 初始化UI组件
   */
  private void initializeComponents() {
    // 创建用户表格组件
    userTableComponent = new UserTableComponent(managementViewModel);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 顶部通知组件
    add(notificationComponent, BorderLayout.NORTH);

    // 中央用户表格
    add(userTableComponent, BorderLayout.CENTER);
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 设置表格组件的事件处理
    userTableComponent.setOnAddUser(this::showAddUserDialog);
    userTableComponent.setOnEditUser(this::showEditUserDialog);

    // 设置管理ViewModel的事件监听
    managementViewModel.setOnError(message -> {
      SwingUtilities.invokeLater(() -> notificationComponent.showError(message));
    });

    managementViewModel.setOnSuccess(message -> {
      SwingUtilities.invokeLater(() -> notificationComponent.showSuccess(message));
    });
  }

  /**
   * 显示添加用户对话框
   */
  private void showAddUserDialog() {
    showUserEditDialog(null);
  }

  /**
   * 显示编辑用户对话框
   *
   * @param user 要编辑的用户
   */
  private void showEditUserDialog(User user) {
    showUserEditDialog(user);
  }

  /**
   * 显示用户编辑对话框
   *
   * @param user 要编辑的用户，null表示新增
   */
  private void showUserEditDialog(User user) {
    // 创建编辑ViewModel
    UserEditViewModel editViewModel = new UserEditViewModel(
        userService,
        AppContext.getCurrentUser());

    // 设置编辑模式
    editViewModel.setEditingUser(user);

    // 创建对话框
    JDialog dialog = createUserEditDialog(editViewModel, user == null ? "添加用户" : "编辑用户");

    // 设置编辑完成的回调
    editViewModel.setOnSaveCompleted(() -> {
      dialog.dispose();
      managementViewModel.refreshUserList();
    });

    editViewModel.setOnError(message -> {
      SwingUtilities.invokeLater(() -> notificationComponent.showError(message));
    });

    editViewModel.setOnSuccess(message -> {
      SwingUtilities.invokeLater(() -> notificationComponent.showSuccess(message));
    });

    // 显示对话框
    dialog.setVisible(true);
  }

  /**
   * 创建用户编辑对话框
   *
   * @param editViewModel 编辑ViewModel
   * @param title         对话框标题
   * @return 对话框
   */
  private JDialog createUserEditDialog(UserEditViewModel editViewModel, String title) {
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    // 创建表单组件
    UserBasicInfoFormComponent basicInfoForm = new UserBasicInfoFormComponent(editViewModel);
    UserPersonalInfoFormComponent personalInfoForm = new UserPersonalInfoFormComponent(editViewModel);
    UserProfessionalInfoFormComponent professionalInfoForm = new UserProfessionalInfoFormComponent(editViewModel);

    // 创建按钮面板
    JPanel buttonPanel = createButtonPanel(editViewModel, dialog);

    // 创建主面板
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // 创建表单容器
    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;

    // 基本信息
    gbc.gridy = 0;
    gbc.weighty = 0.3;
    formPanel.add(basicInfoForm, gbc);

    // 个人信息
    gbc.gridy = 1;
    gbc.weighty = 0.4;
    formPanel.add(personalInfoForm, gbc);

    // 专业信息
    gbc.gridy = 2;
    gbc.weighty = 0.3;
    formPanel.add(professionalInfoForm, gbc);

    // 组装对话框
    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setContentPane(mainPanel);
    dialog.setSize(600, 700);
    dialog.setLocationRelativeTo(this);

    return dialog;
  }

  /**
   * 创建按钮面板
   *
   * @param editViewModel 编辑ViewModel
   * @param dialog        对话框
   * @return 按钮面板
   */
  private JPanel createButtonPanel(UserEditViewModel editViewModel, JDialog dialog) {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

    // 提交按钮
    JButton submitButton = new JButton(editViewModel.isAddMode() ? "添加" : "保存");
    submitButton.setBackground(new Color(46, 204, 113));
    submitButton.setForeground(Color.WHITE);
    submitButton.setFocusPainted(false);
    submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // 取消按钮
    JButton cancelButton = new JButton("取消");
    cancelButton.setBackground(new Color(149, 165, 166));
    cancelButton.setForeground(Color.WHITE);
    cancelButton.setFocusPainted(false);
    cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // 设置按钮事件
    submitButton.addActionListener(e -> editViewModel.submitForm());
    cancelButton.addActionListener(e -> dialog.dispose());

    // 监听加载状态
    editViewModel.setOnLoading(isLoading -> {
      SwingUtilities.invokeLater(() -> {
        submitButton.setEnabled(!isLoading);
        submitButton.setText(isLoading ? "保存中..." : (editViewModel.isAddMode() ? "添加" : "保存"));
        dialog.setCursor(isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
      });
    });

    buttonPanel.add(submitButton);
    buttonPanel.add(cancelButton);

    return buttonPanel;
  }
}