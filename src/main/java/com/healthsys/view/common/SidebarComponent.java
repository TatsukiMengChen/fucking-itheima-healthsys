package com.healthsys.view.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.healthsys.config.AppContext;
import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.util.IconUtil;

/**
 * 侧边栏组件。
 * 提供主导航入口。
 * 
 * @author 梦辰
 * @since 1.0
 */
public class SidebarComponent extends JPanel {

  private List<JButton> navigationButtons;
  private NavigationListener navigationListener;

  /**
   * 导航监听器接口
   */
  public interface NavigationListener {
    void onNavigate(String targetPanel);
  }

  /**
   * 构造函数
   */
  public SidebarComponent() {
    navigationButtons = new ArrayList<>();
    initComponents();
    setupLayout();
    setupStyles();
    updateNavigationButtons();
  }

  /**
   * 设置导航监听器
   */
  public void setNavigationListener(NavigationListener listener) {
    this.navigationListener = listener;
  }

  /**
   * 初始化组件
   */
  private void initComponents() {
    // 初始化时创建空的按钮列表
    // 实际按钮会在updateNavigationButtons中创建
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    // 添加标题
    JLabel titleLabel = new JLabel("导航菜单");
    titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(titleLabel);

    // 添加分隔线
    add(Box.createRigidArea(new Dimension(0, 5)));
    add(new JSeparator());
    add(Box.createRigidArea(new Dimension(0, 5)));
  }

  /**
   * 设置样式
   */
  private void setupStyles() {
    setPreferredSize(new Dimension(200, 0));
    setBackground(UIManager.getColor("Panel.background"));
    setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
  }

  /**
   * 根据用户角色更新导航按钮
   */
  public void updateNavigationButtons() {
    // 清除现有按钮
    for (JButton button : navigationButtons) {
      remove(button);
    }
    navigationButtons.clear();

    UserRoleEnum userRole = AppContext.getCurrentUserRole();

    if (userRole == null) {
      // 未登录用户，不显示任何导航
      return;
    }

    // 根据角色添加相应的导航项
    addNavigationByRole(userRole);

    // 刷新界面
    revalidate();
    repaint();
  }

  /**
   * 根据用户角色添加导航项
   */
  private void addNavigationByRole(UserRoleEnum userRole) {
    switch (userRole) {
      case NORMAL_USER:
        // 普通用户功能
        addUserNavigationButtons();
        addSeparator();
        addCommonNavigationButtons();
        break;

      case ADMIN:
        // 基本功能
        addUserNavigationButtons();
        addSeparator();
        // 管理功能
        addAdminOnlyNavigationButtons();
        addSeparator();
        // 系统功能
        addCommonNavigationButtons();
        break;

      case SUPER_ADMIN:
        // 基本功能
        addUserNavigationButtons();
        addSeparator();
        // 管理功能
        addAdminOnlyNavigationButtons();
        addSeparator();
        // 系统管理功能
        addSuperAdminOnlyNavigationButtons();
        addSeparator();
        // 系统功能
        addCommonNavigationButtons();
        break;
    }
  }

  /**
   * 添加普通用户导航按钮
   */
  private void addUserNavigationButtons() {
    addNavigationButton("预约管理", "appointment", "add");
    addNavigationButton("健康数据", "userdata", "user");
  }

  /**
   * 添加管理员专有导航按钮
   */
  private void addAdminOnlyNavigationButtons() {
    addNavigationButton("预约管理", "adminappointment", "calendar");
    addNavigationButton("检查项管理", "checkitem", "edit");
    addNavigationButton("检查组管理", "checkgroup", "settings");
    addNavigationButton("用户健康数据", "admindata", "search");
  }

  /**
   * 添加超级管理员专有导航按钮
   */
  private void addSuperAdminOnlyNavigationButtons() {
    addNavigationButton("用户管理", "usermanagement", "user");
  }

  /**
   * 添加通用导航按钮
   */
  private void addCommonNavigationButtons() {
    addNavigationButton("系统设置", "settings", "settings");
    addNavigationButton("退出登录", "logout", "logout");
  }

  /**
   * 添加导航按钮（带图标）
   */
  private void addNavigationButton(String text, String action, String iconName) {
    JButton button = new JButton(text);

    // 添加图标
    if (iconName != null && !iconName.isEmpty()) {
      try {
        Icon icon = IconUtil.loadSmallIcon(iconName);
        button.setIcon(icon);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(8);
      } catch (Exception e) {
        // 如果图标加载失败，继续使用文本按钮
      }
    }

    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.setMaximumSize(new Dimension(180, 35));
    button.setPreferredSize(new Dimension(180, 35));
    button.setMinimumSize(new Dimension(180, 35));
    button.setFont(new Font("微软雅黑", Font.PLAIN, 12));

    // 设置按钮样式
    button.setFocusPainted(false);
    button.setBorderPainted(true);
    button.setContentAreaFilled(true);
    button.setBackground(UIManager.getColor("Button.background"));
    button.setForeground(UIManager.getColor("Button.foreground"));

    // 添加悬停效果
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
        button.setBackground(UIManager.getColor("Button.select"));
      }

      @Override
      public void mouseExited(java.awt.event.MouseEvent e) {
        button.setBackground(UIManager.getColor("Button.background"));
      }
    });

    // 添加点击事件
    button.addActionListener(e -> {
      if (navigationListener != null) {
        navigationListener.onNavigate(action);
      }
    });

    navigationButtons.add(button);
    add(button);
    add(Box.createRigidArea(new Dimension(0, 5)));
  }

  /**
   * 添加导航按钮（无图标）
   */
  private void addNavigationButton(String text, String action) {
    addNavigationButton(text, action, null);
  }

  /**
   * 添加分组标题
   */
  private void addSectionTitle(String title) {
    JLabel sectionLabel = new JLabel(title);
    sectionLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
    sectionLabel.setForeground(Color.GRAY);
    sectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    sectionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    add(sectionLabel);
  }

  /**
   * 添加分隔线
   */
  private void addSeparator() {
    add(Box.createRigidArea(new Dimension(0, 10)));
    JSeparator separator = new JSeparator();
    separator.setMaximumSize(new Dimension(180, 1));
    add(separator);
    add(Box.createRigidArea(new Dimension(0, 10)));
  }

  /**
   * 设置选中的按钮
   */
  public void setSelectedButton(String action) {
    for (JButton button : navigationButtons) {
      button.setSelected(false);
      // 可以在这里添加选中状态的视觉效果
    }

    // 找到对应的按钮并设置为选中状态
    for (JButton button : navigationButtons) {
      if (button.getActionCommand() != null && button.getActionCommand().equals(action)) {
        button.setSelected(true);
        break;
      }
    }
  }
}