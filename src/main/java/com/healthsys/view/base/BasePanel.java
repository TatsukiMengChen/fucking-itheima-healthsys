package com.healthsys.view.base;

import javax.swing.*;
import java.awt.*;

/**
 * 基础面板类
 * 提供通用的UI构建方法和样式
 */
public abstract class BasePanel extends JPanel {

  // 通用颜色定义
  protected static final Color PRIMARY_COLOR = new Color(0, 123, 255);
  protected static final Color SUCCESS_COLOR = new Color(40, 167, 69);
  protected static final Color ERROR_COLOR = new Color(220, 53, 69);
  protected static final Color WARNING_COLOR = new Color(255, 193, 7);
  protected static final Color SECONDARY_COLOR = new Color(108, 117, 125);

  // 通用尺寸定义
  protected static final Dimension BUTTON_SIZE = new Dimension(120, 35);
  protected static final Dimension INPUT_SIZE = new Dimension(250, 30);
  protected static final int DEFAULT_PADDING = 10;
  protected static final int COMPONENT_SPACING = 8;

  /**
   * 构造函数
   */
  public BasePanel() {
    super();
    initializePanel();
  }

  /**
   * 构造函数
   * 
   * @param layout 布局管理器
   */
  public BasePanel(LayoutManager layout) {
    super(layout);
    initializePanel();
  }

  /**
   * 初始化面板
   */
  private void initializePanel() {
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createEmptyBorder(DEFAULT_PADDING, DEFAULT_PADDING,
        DEFAULT_PADDING, DEFAULT_PADDING));
  }

  /**
   * 创建标准文本输入框
   * 
   * @param placeholder 占位符文本
   * @return JTextField
   */
  protected JTextField createTextField(String placeholder) {
    JTextField textField = new JTextField();
    textField.setPreferredSize(INPUT_SIZE);
    textField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    textField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)));

    // 设置占位符效果
    if (placeholder != null && !placeholder.isEmpty()) {
      textField.setToolTipText(placeholder);
    }

    return textField;
  }

  /**
   * 创建密码输入框
   * 
   * @param placeholder 占位符文本
   * @return JPasswordField
   */
  protected JPasswordField createPasswordField(String placeholder) {
    JPasswordField passwordField = new JPasswordField();
    passwordField.setPreferredSize(INPUT_SIZE);
    passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    passwordField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)));

    if (placeholder != null && !placeholder.isEmpty()) {
      passwordField.setToolTipText(placeholder);
    }

    return passwordField;
  }

  /**
   * 创建主要按钮
   * 
   * @param text 按钮文本
   * @return JButton
   */
  protected JButton createPrimaryButton(String text) {
    JButton button = new JButton(text);
    button.setPreferredSize(BUTTON_SIZE);
    button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    button.setBackground(PRIMARY_COLOR);
    button.setForeground(Color.WHITE);
    button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // 悬停效果
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(PRIMARY_COLOR.darker());
        }
      }

      @Override
      public void mouseExited(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(PRIMARY_COLOR);
        }
      }
    });

    return button;
  }

  /**
   * 创建次要按钮
   * 
   * @param text 按钮文本
   * @return JButton
   */
  protected JButton createSecondaryButton(String text) {
    JButton button = new JButton(text);
    button.setPreferredSize(BUTTON_SIZE);
    button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    button.setBackground(Color.WHITE);
    button.setForeground(SECONDARY_COLOR);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
        BorderFactory.createEmptyBorder(8, 16, 8, 16)));
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return button;
  }

  /**
   * 创建链接样式按钮
   * 
   * @param text 按钮文本
   * @return JButton
   */
  protected JButton createLinkButton(String text) {
    JButton button = new JButton("<html><u>" + text + "</u></html>");
    button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    button.setForeground(PRIMARY_COLOR);
    button.setBorder(null);
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return button;
  }

  /**
   * 创建标签
   * 
   * @param text 标签文本
   * @return JLabel
   */
  protected JLabel createLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    label.setForeground(Color.DARK_GRAY);
    return label;
  }

  /**
   * 创建错误消息标签
   * 
   * @param text 错误文本
   * @return JLabel
   */
  protected JLabel createErrorLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    label.setForeground(ERROR_COLOR);
    return label;
  }

  /**
   * 创建成功消息标签
   * 
   * @param text 成功文本
   * @return JLabel
   */
  protected JLabel createSuccessLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    label.setForeground(SUCCESS_COLOR);
    return label;
  }

  /**
   * 创建标题标签
   * 
   * @param text 标题文本
   * @return JLabel
   */
  protected JLabel createTitleLabel(String text) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    label.setForeground(Color.DARK_GRAY);
    return label;
  }

  /**
   * 创建垂直间距
   * 
   * @param height 间距高度
   * @return Component
   */
  protected Component createVerticalStrut(int height) {
    return Box.createVerticalStrut(height);
  }

  /**
   * 创建水平间距
   * 
   * @param width 间距宽度
   * @return Component
   */
  protected Component createHorizontalStrut(int width) {
    return Box.createHorizontalStrut(width);
  }

  /**
   * 创建居中面板
   * 
   * @param component 要居中的组件
   * @return JPanel
   */
  protected JPanel createCenterPanel(Component component) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);
    panel.add(component);
    return panel;
  }
}