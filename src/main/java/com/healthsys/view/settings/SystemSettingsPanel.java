package com.healthsys.view.settings;

import com.healthsys.view.base.BasePanel;
import com.healthsys.config.AppContext;
import com.healthsys.model.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 系统设置面板
 * 提供基本的系统配置和用户偏好设置
 * 
 * @author HealthSys Team
 * @since 1.0
 */
public class SystemSettingsPanel extends BasePanel {

  // UI组件
  private JTabbedPane tabbedPane;
  private JPanel generalPanel;
  private JPanel appearancePanel;
  private JPanel accountPanel;
  private JPanel aboutPanel;

  // 通用设置组件
  private JCheckBox autoSaveCheckBox;
  private JCheckBox autoBackupCheckBox;
  private JSpinner backupIntervalSpinner;
  private JComboBox<String> languageComboBox;

  // 外观设置组件
  private JComboBox<String> themeComboBox;
  private JSlider fontSizeSlider;
  private JLabel fontSizeLabel;

  // 账户设置组件
  private JLabel usernameLabel;
  private JLabel emailLabel;
  private JLabel roleLabel;
  private JButton changePasswordButton;
  private JButton updateProfileButton;

  /**
   * 构造函数
   */
  public SystemSettingsPanel() {
    initializeComponents();
    setupLayout();
    loadCurrentSettings();
    bindEvents();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 创建选项卡面板
    tabbedPane = new JTabbedPane();

    // 创建各个设置面板
    createGeneralPanel();
    createAppearancePanel();
    createAccountPanel();
    createAboutPanel();

    // 添加选项卡
    tabbedPane.addTab("常规设置", generalPanel);
    tabbedPane.addTab("外观设置", appearancePanel);
    tabbedPane.addTab("账户信息", accountPanel);
    tabbedPane.addTab("关于系统", aboutPanel);
  }

  /**
   * 创建通用设置面板
   */
  private void createGeneralPanel() {
    generalPanel = new JPanel();
    generalPanel.setLayout(new GridBagLayout());
    generalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    // 自动保存设置
    autoSaveCheckBox = new JCheckBox("启用自动保存");
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    generalPanel.add(autoSaveCheckBox, gbc);

    // 自动备份设置
    autoBackupCheckBox = new JCheckBox("启用自动备份");
    gbc.gridy = 1;
    generalPanel.add(autoBackupCheckBox, gbc);

    // 备份间隔设置
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    generalPanel.add(new JLabel("备份间隔(小时):"), gbc);

    backupIntervalSpinner = new JSpinner(new SpinnerNumberModel(24, 1, 168, 1));
    gbc.gridx = 1;
    generalPanel.add(backupIntervalSpinner, gbc);

    // 语言设置
    gbc.gridx = 0;
    gbc.gridy = 3;
    generalPanel.add(new JLabel("界面语言:"), gbc);

    languageComboBox = new JComboBox<>(new String[] { "简体中文", "English" });
    gbc.gridx = 1;
    generalPanel.add(languageComboBox, gbc);
  }

  /**
   * 创建外观设置面板
   */
  private void createAppearancePanel() {
    appearancePanel = new JPanel();
    appearancePanel.setLayout(new GridBagLayout());
    appearancePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    // 主题设置
    gbc.gridx = 0;
    gbc.gridy = 0;
    appearancePanel.add(new JLabel("界面主题:"), gbc);

    themeComboBox = new JComboBox<>(new String[] { "浅色主题", "深色主题", "自动切换" });
    gbc.gridx = 1;
    appearancePanel.add(themeComboBox, gbc);

    // 字体大小设置
    gbc.gridx = 0;
    gbc.gridy = 1;
    appearancePanel.add(new JLabel("字体大小:"), gbc);

    fontSizeSlider = new JSlider(8, 24, 12);
    fontSizeSlider.setMajorTickSpacing(4);
    fontSizeSlider.setMinorTickSpacing(2);
    fontSizeSlider.setPaintTicks(true);
    fontSizeSlider.setPaintLabels(true);

    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    appearancePanel.add(fontSizeSlider, gbc);

    fontSizeLabel = new JLabel("当前大小: 12px");
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.NONE;
    appearancePanel.add(fontSizeLabel, gbc);
  }

  /**
   * 创建账户设置面板
   */
  private void createAccountPanel() {
    accountPanel = new JPanel();
    accountPanel.setLayout(new GridBagLayout());
    accountPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    User currentUser = AppContext.getCurrentUser();

    // 用户名
    gbc.gridx = 0;
    gbc.gridy = 0;
    accountPanel.add(new JLabel("用户名:"), gbc);

    usernameLabel = new JLabel(currentUser != null ? currentUser.getUsername() : "未登录");
    gbc.gridx = 1;
    accountPanel.add(usernameLabel, gbc);

    // 邮箱
    gbc.gridx = 0;
    gbc.gridy = 1;
    accountPanel.add(new JLabel("邮箱:"), gbc);

    emailLabel = new JLabel(currentUser != null ? currentUser.getEmail() : "未设置");
    gbc.gridx = 1;
    accountPanel.add(emailLabel, gbc);

    // 角色
    gbc.gridx = 0;
    gbc.gridy = 2;
    accountPanel.add(new JLabel("用户角色:"), gbc);

    String roleText = "未知";
    if (currentUser != null) {
      switch (currentUser.getRole()) {
        case "SUPER_ADMIN":
          roleText = "超级管理员";
          break;
        case "ADMIN":
          roleText = "管理员";
          break;
        case "NORMAL_USER":
          roleText = "普通用户";
          break;
      }
    }
    roleLabel = new JLabel(roleText);
    gbc.gridx = 1;
    accountPanel.add(roleLabel, gbc);

    // 操作按钮
    changePasswordButton = new JButton("修改密码");
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    accountPanel.add(changePasswordButton, gbc);

    updateProfileButton = new JButton("更新个人信息");
    gbc.gridy = 4;
    accountPanel.add(updateProfileButton, gbc);
  }

  /**
   * 创建关于面板
   */
  private void createAboutPanel() {
    aboutPanel = new JPanel();
    aboutPanel.setLayout(new BorderLayout());
    aboutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // 创建关于信息
    String aboutText = "<html><div style='text-align: center;'>" +
        "<h2>健康管理系统</h2>" +
        "<p><b>版本:</b> 1.0.0</p>" +
        "<p><b>构建时间:</b> 2024年12月</p>" +
        "<hr>" +
        "<p><b>开发团队:</b> HealthSys Development Team</p>" +
        "<p><b>技术栈:</b></p>" +
        "<p>• Java Swing + FlatLaf UI</p>" +
        "<p>• MyBatis-Plus ORM</p>" +
        "<p>• SQLite 数据库</p>" +
        "<p>• Hutool 工具库</p>" +
        "<hr>" +
        "<p><b>系统功能:</b></p>" +
        "<p>• 用户认证与权限管理</p>" +
        "<p>• 体检预约与管理</p>" +
        "<p>• 健康数据跟踪</p>" +
        "<p>• 检查项目管理</p>" +
        "<p>• 体检结果分析</p>" +
        "<hr>" +
        "<p style='color: #666;'>©2024 健康管理系统. 保留所有权利.</p>" +
        "</div></html>";

    JLabel aboutLabel = new JLabel(aboutText);
    aboutLabel.setHorizontalAlignment(SwingConstants.CENTER);
    aboutLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

    aboutPanel.add(aboutLabel, BorderLayout.CENTER);

    // 系统信息按钮
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton systemInfoButton = new JButton("查看系统信息");
    systemInfoButton.addActionListener(e -> showSystemInfo());
    buttonPanel.add(systemInfoButton);

    aboutPanel.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());

    // 添加标题
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    JLabel titleLabel = new JLabel("系统设置");
    titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
    titleLabel.setForeground(new Color(51, 51, 51));

    titlePanel.add(titleLabel);
    add(titlePanel, BorderLayout.NORTH);

    // 添加选项卡面板
    add(tabbedPane, BorderLayout.CENTER);

    // 添加底部按钮
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

    JButton applyButton = new JButton("应用");
    JButton resetButton = new JButton("重置");

    applyButton.setBackground(new Color(46, 204, 113));
    applyButton.setForeground(Color.WHITE);
    applyButton.setFocusPainted(false);

    resetButton.setBackground(new Color(149, 165, 166));
    resetButton.setForeground(Color.WHITE);
    resetButton.setFocusPainted(false);

    bottomPanel.add(resetButton);
    bottomPanel.add(Box.createHorizontalStrut(10));
    bottomPanel.add(applyButton);

    add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * 加载当前设置
   */
  private void loadCurrentSettings() {
    // 设置默认值
    autoSaveCheckBox.setSelected(true);
    autoBackupCheckBox.setSelected(false);
    languageComboBox.setSelectedIndex(0);
    themeComboBox.setSelectedIndex(0);
    fontSizeSlider.setValue(12);
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 字体大小滑块事件
    fontSizeSlider.addChangeListener(e -> {
      int fontSize = fontSizeSlider.getValue();
      fontSizeLabel.setText("当前大小: " + fontSize + "px");
    });

    // 修改密码按钮事件
    changePasswordButton.addActionListener(e -> {
      JOptionPane.showMessageDialog(this,
          "密码修改功能正在开发中...",
          "提示",
          JOptionPane.INFORMATION_MESSAGE);
    });

    // 更新个人信息按钮事件
    updateProfileButton.addActionListener(e -> {
      JOptionPane.showMessageDialog(this,
          "个人信息更新功能正在开发中...",
          "提示",
          JOptionPane.INFORMATION_MESSAGE);
    });
  }

  /**
   * 显示系统信息
   */
  private void showSystemInfo() {
    Runtime runtime = Runtime.getRuntime();
    long maxMemory = runtime.maxMemory();
    long totalMemory = runtime.totalMemory();
    long freeMemory = runtime.freeMemory();
    long usedMemory = totalMemory - freeMemory;

    String systemInfo = String.format(
        "系统信息:\n\n" +
            "Java版本: %s\n" +
            "操作系统: %s %s\n" +
            "系统架构: %s\n" +
            "可用处理器: %d\n\n" +
            "内存使用情况:\n" +
            "已用内存: %.2f MB\n" +
            "总内存: %.2f MB\n" +
            "最大内存: %.2f MB\n" +
            "空闲内存: %.2f MB",
        System.getProperty("java.version"),
        System.getProperty("os.name"),
        System.getProperty("os.version"),
        System.getProperty("os.arch"),
        runtime.availableProcessors(),
        usedMemory / (1024.0 * 1024.0),
        totalMemory / (1024.0 * 1024.0),
        maxMemory / (1024.0 * 1024.0),
        freeMemory / (1024.0 * 1024.0));

    JTextArea textArea = new JTextArea(systemInfo);
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JOptionPane.showMessageDialog(this, scrollPane, "系统信息", JOptionPane.INFORMATION_MESSAGE);
  }
}