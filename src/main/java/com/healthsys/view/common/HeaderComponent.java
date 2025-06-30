package com.healthsys.view.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.healthsys.config.AppContext;
import com.healthsys.model.entity.User;

/**
 * 顶部信息栏组件。
 * 展示用户信息和全局操作入口。
 * 
 * @author 梦辰
 * @since 1.0
 */
public class HeaderComponent extends JPanel {

  private JLabel titleLabel;
  private JLabel userInfoLabel;
  private JLabel timeLabel;
  private Timer timeTimer;

  /**
   * 构造函数
   */
  public HeaderComponent() {
    initComponents();
    setupLayout();
    setupStyles();
    startTimeUpdate();
  }

  /**
   * 初始化组件
   */
  private void initComponents() {
    titleLabel = new JLabel("健康管理系统", SwingConstants.LEFT);
    userInfoLabel = new JLabel("", SwingConstants.CENTER);
    timeLabel = new JLabel("", SwingConstants.RIGHT);

    // 设置字体
    titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    userInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    timeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

    // 更新用户信息
    updateUserInfo();
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());

    // 创建左侧面板（标题）
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    leftPanel.setOpaque(false);
    leftPanel.add(titleLabel);

    // 创建中央面板（用户信息）
    JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    centerPanel.setOpaque(false);
    centerPanel.add(userInfoLabel);

    // 创建右侧面板（时间）
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.setOpaque(false);
    rightPanel.add(timeLabel);

    add(leftPanel, BorderLayout.WEST);
    add(centerPanel, BorderLayout.CENTER);
    add(rightPanel, BorderLayout.EAST);
  }

  /**
   * 设置样式
   */
  private void setupStyles() {
    setPreferredSize(new Dimension(0, 60));

    // 创建渐变背景
    setOpaque(false);

    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(15, 25, 15, 25)));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // 绘制渐变背景
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Color startColor = UIManager.getColor("Panel.background");
    Color endColor = startColor.brighter();

    GradientPaint gradient = new GradientPaint(
        0, 0, startColor,
        0, getHeight(), endColor);

    g2d.setPaint(gradient);
    g2d.fillRect(0, 0, getWidth(), getHeight());
    g2d.dispose();
  }

  /**
   * 更新用户信息显示
   */
  public void updateUserInfo() {
    User currentUser = AppContext.getCurrentUser();
    if (currentUser != null) {
      String roleText = getRoleDisplayName(currentUser.getRole());
      userInfoLabel.setText(String.format("欢迎您，%s (%s)",
          currentUser.getUsername(), roleText));
    } else {
      userInfoLabel.setText("未登录");
    }
  }

  /**
   * 获取角色显示名称
   */
  private String getRoleDisplayName(String role) {
    if (role == null) {
      return "未知角色";
    }

    switch (role) {
      case "SUPER_ADMIN":
        return "超级管理员";
      case "ADMIN":
        return "管理员";
      case "NORMAL_USER":
        return "普通用户";
      default:
        return "未知角色";
    }
  }

  /**
   * 开始时间更新
   */
  private void startTimeUpdate() {
    timeTimer = new Timer(1000, e -> updateTime());
    timeTimer.start();
    updateTime(); // 立即更新一次
  }

  /**
   * 更新时间显示
   */
  private void updateTime() {
    timeLabel.setText(java.time.LocalDateTime.now()
        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }

  /**
   * 清理资源
   */
  public void dispose() {
    if (timeTimer != null && timeTimer.isRunning()) {
      timeTimer.stop();
    }
  }
}