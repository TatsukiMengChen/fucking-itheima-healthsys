package com.healthsys.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 应用程序主窗口
 * 作为所有UI组件的容器，负责整体界面布局和导航
 * 
 * @author AI健康管理系统开发团队
 */
public class MainFrame extends JFrame {

  private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

  // 窗口尺寸常量
  private static final int DEFAULT_WIDTH = 1200;
  private static final int DEFAULT_HEIGHT = 800;
  private static final int MIN_WIDTH = 1000;
  private static final int MIN_HEIGHT = 600;

  // UI组件
  private JPanel contentPanel;
  private JPanel currentViewPanel;

  /**
   * 构造函数
   */
  public MainFrame() {
    initializeFrame();
    initializeComponents();
    layoutComponents();
    bindEvents();

    logger.info("主窗口初始化完成");
  }

  /**
   * 初始化窗口基本属性
   */
  private void initializeFrame() {
    setTitle("健康管理系统 v1.0");
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setLocationRelativeTo(null); // 居中显示

    // 设置窗口图标（如果有的话）
    try {
      // TODO: 添加应用图标
      // setIconImage(ImageIO.read(getClass().getResourceAsStream("/images/app-icon.png")));
    } catch (Exception e) {
      logger.debug("未设置应用图标");
    }
  }

  /**
   * 初始化UI组件
   */
  private void initializeComponents() {
    // 创建内容面板
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // 创建当前视图面板（用于动态切换不同的功能面板）
    currentViewPanel = new JPanel(new BorderLayout());
    currentViewPanel.setBorder(BorderFactory.createTitledBorder("系统功能"));

    // 初始显示欢迎信息
    showWelcomeView();
  }

  /**
   * 布局组件
   */
  private void layoutComponents() {
    // 设置主内容面板
    setContentPane(contentPanel);

    // 添加标题栏（暂时用标签代替，后续会被HeaderComponent替换）
    JLabel titleLabel = new JLabel("健康管理系统", JLabel.CENTER);
    titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    contentPanel.add(titleLabel, BorderLayout.NORTH);

    // 添加主内容区域
    contentPanel.add(currentViewPanel, BorderLayout.CENTER);

    // 添加状态栏（暂时用标签代替）
    JLabel statusLabel = new JLabel("就绪");
    statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    contentPanel.add(statusLabel, BorderLayout.SOUTH);
  }

  /**
   * 绑定事件处理器
   */
  private void bindEvents() {
    // 窗口关闭事件
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        handleWindowClosing();
      }
    });
  }

  /**
   * 显示欢迎视图
   */
  private void showWelcomeView() {
    JPanel welcomePanel = new JPanel(new BorderLayout());

    // 欢迎信息
    JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>" +
        "<h2>欢迎使用健康管理系统</h2>" +
        "<p>系统正在初始化中...</p>" +
        "<p>请稍候，即将显示登录界面</p>" +
        "</div></html>", JLabel.CENTER);
    welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

    welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

    // 临时按钮区域（用于测试）
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton testButton = new JButton("测试按钮");
    testButton.addActionListener(e -> {
      JOptionPane.showMessageDialog(this,
          "系统功能正在开发中...\n\n" +
              "即将实现的功能：\n" +
              "• 用户登录注册\n" +
              "• 检查项管理\n" +
              "• 检查组管理\n" +
              "• 健康数据管理\n" +
              "• 预约与跟踪",
          "系统信息",
          JOptionPane.INFORMATION_MESSAGE);
    });
    buttonPanel.add(testButton);

    welcomePanel.add(buttonPanel, BorderLayout.SOUTH);

    setCurrentView(welcomePanel);
  }

  /**
   * 设置当前显示的视图
   * 
   * @param viewPanel 要显示的面板
   */
  public void setCurrentView(JPanel viewPanel) {
    currentViewPanel.removeAll();
    currentViewPanel.add(viewPanel, BorderLayout.CENTER);
    currentViewPanel.revalidate();
    currentViewPanel.repaint();

    logger.debug("切换到新视图: {}", viewPanel.getClass().getSimpleName());
  }

  /**
   * 处理窗口关闭事件
   */
  private void handleWindowClosing() {
    int option = JOptionPane.showConfirmDialog(
        this,
        "确定要退出健康管理系统吗？",
        "确认退出",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (option == JOptionPane.YES_OPTION) {
      logger.info("用户确认退出系统");

      // TODO: 执行清理工作
      // - 关闭数据库连接
      // - 保存用户设置
      // - 清理临时文件

      System.exit(0);
    }
  }

  /**
   * 显示状态消息
   * 
   * @param message 状态消息
   */
  public void setStatusMessage(String message) {
    // TODO: 更新状态栏消息
    logger.info("状态: {}", message);
  }
}