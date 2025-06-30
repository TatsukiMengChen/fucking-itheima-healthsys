package com.healthsys.view.common;

import com.healthsys.viewmodel.common.NotificationViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 通用通知组件
 * 用于显示全局成功/失败/信息提示
 * 
 * @author HealthSys Team
 */
public class NotificationComponent extends JPanel {

  /**
   * 通知类型枚举
   */
  public enum NotificationType {
    SUCCESS("成功", new Color(67, 160, 71)), // 绿色
    ERROR("错误", new Color(244, 67, 54)), // 红色
    WARNING("警告", new Color(255, 152, 0)), // 橙色
    INFO("信息", new Color(33, 150, 243)); // 蓝色

    private final String text;
    private final Color color;

    NotificationType(String text, Color color) {
      this.text = text;
      this.color = color;
    }

    public String getText() {
      return text;
    }

    public Color getColor() {
      return color;
    }
  }

  private JLabel typeLabel;
  private JLabel messageLabel;
  private JButton closeButton;
  private Timer autoHideTimer;
  private NotificationViewModel viewModel;

  /**
   * 构造函数
   */
  public NotificationComponent() {
    this(null);
  }

  /**
   * 构造函数（带ViewModel）
   */
  public NotificationComponent(NotificationViewModel viewModel) {
    this.viewModel = viewModel;
    initializeComponents();
    setupLayout();
    setupEventListeners();

    if (viewModel != null) {
      bindViewModel();
    }

    setVisible(false); // 默认隐藏
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    typeLabel = new JLabel();
    typeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    typeLabel.setOpaque(true);
    typeLabel.setBorder(new EmptyBorder(4, 8, 4, 8));

    messageLabel = new JLabel();
    messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

    closeButton = new JButton("×");
    closeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    closeButton.setBorder(null);
    closeButton.setContentAreaFilled(false);
    closeButton.setFocusPainted(false);
    closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    closeButton.setPreferredSize(new Dimension(20, 20));

    // 设置边框和背景
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
        new EmptyBorder(8, 12, 8, 8)));
    setBackground(Color.WHITE);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(8, 0));

    add(typeLabel, BorderLayout.WEST);
    add(messageLabel, BorderLayout.CENTER);
    add(closeButton, BorderLayout.EAST);
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        hideNotification();
      }
    });
  }

  /**
   * 绑定ViewModel
   */
  private void bindViewModel() {
    if (viewModel == null) {
      return;
    }

    // 监听ViewModel的通知事件
    viewModel.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> handleViewModelPropertyChange(evt));
      }
    });
  }

  /**
   * 处理ViewModel属性变化
   */
  private void handleViewModelPropertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();
    String message = (String) evt.getNewValue();

    if (message == null || message.trim().isEmpty()) {
      return;
    }

    switch (propertyName) {
      case "successMessage":
        showSuccess(message);
        break;
      case "errorMessage":
        showError(message);
        break;
      case "warningMessage":
        showWarning(message);
        break;
      case "infoMessage":
        showInfo(message);
        break;
    }
  }

  /**
   * 显示通知
   * 
   * @param type    通知类型
   * @param message 通知消息
   */
  public void showNotification(NotificationType type, String message) {
    showNotification(type, message, 3000); // 默认3秒后自动隐藏
  }

  /**
   * 显示通知（带自动隐藏时间）
   * 
   * @param type          通知类型
   * @param message       通知消息
   * @param autoHideDelay 自动隐藏延迟时间（毫秒），0表示不自动隐藏
   */
  public void showNotification(NotificationType type, String message, int autoHideDelay) {
    // 停止之前的计时器
    if (autoHideTimer != null && autoHideTimer.isRunning()) {
      autoHideTimer.stop();
    }

    // 设置通知类型和颜色
    typeLabel.setText(type.getText());
    typeLabel.setBackground(type.getColor());
    typeLabel.setForeground(Color.WHITE);

    // 设置消息
    messageLabel.setText(message);

    // 显示通知
    setVisible(true);

    // 设置自动隐藏
    if (autoHideDelay > 0) {
      autoHideTimer = new Timer(autoHideDelay, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          hideNotification();
        }
      });
      autoHideTimer.setRepeats(false);
      autoHideTimer.start();
    }

    // 重新绘制父容器
    if (getParent() != null) {
      getParent().revalidate();
      getParent().repaint();
    }
  }

  /**
   * 隐藏通知
   */
  public void hideNotification() {
    setVisible(false);

    // 停止计时器
    if (autoHideTimer != null && autoHideTimer.isRunning()) {
      autoHideTimer.stop();
    }

    // 重新绘制父容器
    if (getParent() != null) {
      getParent().revalidate();
      getParent().repaint();
    }
  }

  /**
   * 显示成功消息
   * 
   * @param message 消息内容
   */
  public void showSuccess(String message) {
    showNotification(NotificationType.SUCCESS, message);
  }

  /**
   * 显示错误消息
   * 
   * @param message 消息内容
   */
  public void showError(String message) {
    showNotification(NotificationType.ERROR, message, 5000); // 错误消息显示5秒
  }

  /**
   * 显示警告消息
   * 
   * @param message 消息内容
   */
  public void showWarning(String message) {
    showNotification(NotificationType.WARNING, message, 4000); // 警告消息显示4秒
  }

  /**
   * 显示信息消息
   * 
   * @param message 消息内容
   */
  public void showInfo(String message) {
    showNotification(NotificationType.INFO, message);
  }

  /**
   * 检查通知是否正在显示
   * 
   * @return 如果通知正在显示则返回true
   */
  public boolean isNotificationVisible() {
    return isVisible();
  }
}