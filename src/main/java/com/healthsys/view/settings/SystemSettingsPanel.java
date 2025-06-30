package com.healthsys.view.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.healthsys.config.AppContext;
import com.healthsys.model.entity.User;
import com.healthsys.view.base.BasePanel;

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
  private JPanel accountPanel;
  private JPanel aboutPanel;

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

    // 只创建账户和关于面板
    createAccountPanel();
    createAboutPanel();

    // 添加选项卡
    tabbedPane.addTab("账户信息", accountPanel);
    tabbedPane.addTab("关于系统", aboutPanel);
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

    // 获取环境变量信息
    String school = System.getenv("STUDENT_SCHOOL");
    String major = System.getenv("STUDENT_MAJOR");
    String classInfo = System.getenv("STUDENT_CLASS");
    String studentName = System.getenv("STUDENT_NAME");
    String studentId = System.getenv("STUDENT_ID");

    // 设置默认值
    if (school == null)
      school = "未设置";
    if (major == null)
      major = "未设置";
    if (classInfo == null)
      classInfo = "未设置";
    if (studentName == null)
      studentName = "未设置";
    if (studentId == null)
      studentId = "未设置";

    // 创建关于信息
    String aboutText = "<html><div style='text-align: center;'>" +
        "<h2>健康管理系统</h2>" +
        "<p><b>版本:</b> 1.0.0</p>" +
        "<p><b>构建时间:</b> 2025年7月</p>" +
        "<hr>" +
        "<p><b>开发者信息:</b></p>" +
        "<p><b>学校:</b> " + school + "</p>" +
        "<p><b>专业:</b> " + major + "</p>" +
        "<p><b>班级:</b> " + classInfo + "</p>" +
        "<p><b>姓名:</b> " + studentName + "</p>" +
        "<p><b>学号:</b> " + studentId + "</p>" +
        "<hr>" +
        "<p><b>开发团队:</b> HealthSys Development Team</p>" +
        "<p><b>技术栈:</b></p>" +
        "<p>• Java Swing + FlatLaf UI</p>" +
        "<p>• MyBatis-Plus ORM</p>" +
        "<p>• PostgreSQL 数据库</p>" +
        "<p>• Hutool 工具库</p>" +
        "<hr>" +
        "<p><b>系统功能:</b></p>" +
        "<p>• 用户认证与权限管理</p>" +
        "<p>• 体检预约与管理</p>" +
        "<p>• 健康数据跟踪</p>" +
        "<p>• 检查项目管理</p>" +
        "<p>• 体检结果分析</p>" +
        "<hr>" +
        "<p style='color: #666;'>©2025 健康管理系统. 保留所有权利.</p>" +
        "</div></html>";

    JLabel aboutLabel = new JLabel(aboutText);
    aboutLabel.setHorizontalAlignment(SwingConstants.CENTER);
    aboutLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

    aboutPanel.add(aboutLabel, BorderLayout.CENTER);

    // 开源组件信息按钮
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton componentInfoButton = new JButton("查看开源组件");
    componentInfoButton.addActionListener(e -> showOpenSourceComponents());
    buttonPanel.add(componentInfoButton);

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
    // 只保留账户和关于设置的初始化（如有）
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 只保留账户相关按钮事件
    changePasswordButton.addActionListener(e -> {
      JOptionPane.showMessageDialog(this,
          "密码修改功能正在开发中...",
          "提示",
          JOptionPane.INFORMATION_MESSAGE);
    });

    updateProfileButton.addActionListener(e -> {
      JOptionPane.showMessageDialog(this,
          "个人信息更新功能正在开发中...",
          "提示",
          JOptionPane.INFORMATION_MESSAGE);
    });
  }

  /**
   * 显示开源组件信息
   */
  private void showOpenSourceComponents() {
    String componentInfo = "项目使用的开源组件信息:\n\n" +
        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
        "UI 框架:\n" +
        "  • FlatLaf 3.4 - 现代化Swing外观库\n" +
        "    提供扁平化、现代化的用户界面风格\n\n" +
        "数据库相关:\n" +
        "  • PostgreSQL JDBC Driver 42.7.3 - 数据库驱动\n" +
        "  • MyBatis-Plus 3.5.7 - ORM框架和扩展\n" +
        "    └─ MyBatis-Plus Core 3.5.7\n" +
        "    └─ MyBatis-Plus Extension 3.5.7\n" +
        "    └─ MyBatis-Plus Annotation 3.5.7\n" +
        "  • MyBatis 3.5.16 - 持久层框架\n" +
        "  • MyBatis-Spring 3.0.3 - Spring集成\n" +
        "  • HikariCP 5.1.0 - 高性能数据库连接池\n\n" +
        "工具库:\n" +
        "  • Hutool 5.8.27 - Java工具包\n" +
        "    提供字符串、日期、加密、校验等常用功能\n\n" +
        "邮件服务:\n" +
        "  • JavaMail API 1.6.2 - 邮件发送接口\n" +
        "  • Sun JavaMail 1.6.2 - 邮件实现\n" +
        "  • Java Activation 1.1.1 - 激活框架\n\n" +
        "日志框架:\n" +
        "  • SLF4J API 2.0.13 - 日志门面\n" +
        "  • Logback Classic 1.5.6 - 日志实现\n\n" +
        "开发工具:\n" +
        "  • Lombok 1.18.30 - 注解处理器\n" +
        "    自动生成getter/setter等样板代码\n\n" +
        "测试框架:\n" +
        "  • JUnit Jupiter 5.11.0 - 单元测试框架\n" +
        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
        "构建工具: Gradle 8.x\n" +
        "Java版本: " + System.getProperty("java.version") + "\n" +
        "运行平台: " + System.getProperty("os.name") + " " + System.getProperty("os.version");

    JTextArea textArea = new JTextArea(componentInfo);
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(600, 500));

    JOptionPane.showMessageDialog(this, scrollPane, "开源组件信息", JOptionPane.INFORMATION_MESSAGE);
  }
}