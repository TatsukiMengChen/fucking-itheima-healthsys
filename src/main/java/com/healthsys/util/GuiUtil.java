package com.healthsys.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * GUI工具类
 * 提供Swing界面相关的实用方法
 * 
 * @author AI健康管理系统开发团队
 */
public class GuiUtil {

  private static final Logger logger = LoggerFactory.getLogger(GuiUtil.class);

  /**
   * 私有构造函数，防止实例化
   */
  private GuiUtil() {
    throw new UnsupportedOperationException("工具类不能被实例化");
  }

  /**
   * 将窗口居中显示在父容器中
   * 
   * @param window 要居中的窗口
   * @param parent 父容器，如果为null则相对于屏幕居中
   */
  public static void centerWindow(Window window, Component parent) {
    if (window == null) {
      return;
    }

    try {
      window.setLocationRelativeTo(parent);
      logger.debug("窗口居中显示完成");
    } catch (Exception e) {
      logger.error("居中显示窗口时发生错误", e);
    }
  }

  /**
   * 创建带有图标和文本的按钮
   * 
   * @param text     按钮文本
   * @param iconPath 图标路径（可选）
   * @param listener 点击事件监听器（可选）
   * @return 创建的按钮
   */
  public static JButton createButton(String text, String iconPath, ActionListener listener) {
    JButton button = new JButton(text);

    try {
      // 设置图标
      if (iconPath != null && !iconPath.trim().isEmpty()) {
        try {
          ImageIcon icon = new ImageIcon(GuiUtil.class.getResource(iconPath));
          button.setIcon(icon);
        } catch (Exception e) {
          logger.warn("加载按钮图标失败: {}", iconPath, e);
        }
      }

      // 设置事件监听器
      if (listener != null) {
        button.addActionListener(listener);
      }

      // 设置按钮样式
      button.setFocusPainted(false);
      button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      logger.debug("创建按钮完成: {}", text);
    } catch (Exception e) {
      logger.error("创建按钮时发生错误", e);
    }

    return button;
  }

  /**
   * 创建标签
   * 
   * @param text      标签文本
   * @param alignment 对齐方式（SwingConstants中的常量）
   * @return 创建的标签
   */
  public static JLabel createLabel(String text, int alignment) {
    JLabel label = new JLabel(text, alignment);
    try {
      logger.debug("创建标签完成: {}", text);
    } catch (Exception e) {
      logger.error("创建标签时发生错误", e);
    }
    return label;
  }

  /**
   * 创建输入框
   * 
   * @param columns     列数
   * @param placeholder 占位符文本（可选）
   * @return 创建的输入框
   */
  public static JTextField createTextField(int columns, String placeholder) {
    JTextField textField = new JTextField(columns);

    try {
      if (placeholder != null && !placeholder.trim().isEmpty()) {
        textField.setToolTipText(placeholder);
      }

      logger.debug("创建输入框完成，列数: {}", columns);
    } catch (Exception e) {
      logger.error("创建输入框时发生错误", e);
    }

    return textField;
  }

  /**
   * 创建密码输入框
   * 
   * @param columns 列数
   * @return 创建的密码输入框
   */
  public static JPasswordField createPasswordField(int columns) {
    JPasswordField passwordField = new JPasswordField(columns);

    try {
      logger.debug("创建密码输入框完成，列数: {}", columns);
    } catch (Exception e) {
      logger.error("创建密码输入框时发生错误", e);
    }

    return passwordField;
  }

  /**
   * 显示信息对话框
   * 
   * @param parent  父组件
   * @param message 消息内容
   * @param title   对话框标题
   */
  public static void showInfoDialog(Component parent, String message, String title) {
    try {
      SwingUtilities.invokeLater(() -> {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
      });
      logger.debug("显示信息对话框: {}", title);
    } catch (Exception e) {
      logger.error("显示信息对话框时发生错误", e);
    }
  }

  /**
   * 显示错误对话框
   * 
   * @param parent  父组件
   * @param message 错误消息
   * @param title   对话框标题
   */
  public static void showErrorDialog(Component parent, String message, String title) {
    try {
      SwingUtilities.invokeLater(() -> {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
      });
      logger.debug("显示错误对话框: {}", title);
    } catch (Exception e) {
      logger.error("显示错误对话框时发生错误", e);
    }
  }

  /**
   * 显示警告对话框
   * 
   * @param parent  父组件
   * @param message 警告消息
   * @param title   对话框标题
   */
  public static void showWarningDialog(Component parent, String message, String title) {
    try {
      SwingUtilities.invokeLater(() -> {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
      });
      logger.debug("显示警告对话框: {}", title);
    } catch (Exception e) {
      logger.error("显示警告对话框时发生错误", e);
    }
  }

  /**
   * 显示确认对话框
   * 
   * @param parent  父组件
   * @param message 确认消息
   * @param title   对话框标题
   * @return 用户选择的结果（JOptionPane的常量）
   */
  public static int showConfirmDialog(Component parent, String message, String title) {
    try {
      int result = JOptionPane.showConfirmDialog(parent, message, title,
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      logger.debug("显示确认对话框: {}，结果: {}", title, result);
      return result;
    } catch (Exception e) {
      logger.error("显示确认对话框时发生错误", e);
      return JOptionPane.NO_OPTION;
    }
  }

  /**
   * 设置组件的首选大小
   * 
   * @param component 组件
   * @param width     宽度
   * @param height    高度
   */
  public static void setPreferredSize(Component component, int width, int height) {
    if (component != null) {
      try {
        component.setPreferredSize(new Dimension(width, height));
        logger.debug("设置组件尺寸: {}x{}", width, height);
      } catch (Exception e) {
        logger.error("设置组件尺寸时发生错误", e);
      }
    }
  }

  /**
   * 为组件添加边距
   * 
   * @param component 组件
   * @param top       上边距
   * @param left      左边距
   * @param bottom    下边距
   * @param right     右边距
   */
  public static void addPadding(JComponent component, int top, int left, int bottom, int right) {
    if (component != null) {
      try {
        component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        logger.debug("添加组件边距: {},{},{},{}", top, left, bottom, right);
      } catch (Exception e) {
        logger.error("添加组件边距时发生错误", e);
      }
    }
  }

  /**
   * 创建网格布局面板
   * 
   * @param rows 行数
   * @param cols 列数
   * @param hgap 水平间距
   * @param vgap 垂直间距
   * @return 创建的面板
   */
  public static JPanel createGridPanel(int rows, int cols, int hgap, int vgap) {
    JPanel panel = new JPanel(new GridLayout(rows, cols, hgap, vgap));
    try {
      logger.debug("创建网格布局面板: {}x{}，间距: {},{})", rows, cols, hgap, vgap);
    } catch (Exception e) {
      logger.error("创建网格布局面板时发生错误", e);
    }
    return panel;
  }

  /**
   * 启用或禁用组件
   * 
   * @param component 组件
   * @param enabled   是否启用
   */
  public static void setComponentEnabled(Component component, boolean enabled) {
    if (component != null) {
      try {
        component.setEnabled(enabled);
        logger.debug("设置组件状态: {}", enabled ? "启用" : "禁用");
      } catch (Exception e) {
        logger.error("设置组件状态时发生错误", e);
      }
    }
  }
}