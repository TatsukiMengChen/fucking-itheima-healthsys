package com.healthsys.viewmodel.common;

import com.healthsys.view.common.NotificationComponent;
import com.healthsys.viewmodel.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知组件的ViewModel
 * 管理通知状态和消息
 * 
 * @author HealthSys Team
 */
public class NotificationViewModel extends BaseViewModel {

  /**
   * 通知监听器接口
   */
  public interface NotificationListener {
    /**
     * 显示通知
     * 
     * @param type          通知类型
     * @param message       通知消息
     * @param autoHideDelay 自动隐藏延迟时间（毫秒）
     */
    void showNotification(NotificationComponent.NotificationType type, String message, int autoHideDelay);

    /**
     * 隐藏通知
     */
    void hideNotification();
  }

  private List<NotificationListener> listeners = new ArrayList<>();
  private boolean isVisible = false;
  private String currentMessage = "";
  private NotificationComponent.NotificationType currentType = NotificationComponent.NotificationType.INFO;

  /**
   * 构造函数
   */
  public NotificationViewModel() {
    super();
  }

  /**
   * 添加通知监听器
   * 
   * @param listener 监听器
   */
  public void addNotificationListener(NotificationListener listener) {
    if (listener != null && !listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * 移除通知监听器
   * 
   * @param listener 监听器
   */
  public void removeNotificationListener(NotificationListener listener) {
    listeners.remove(listener);
  }

  /**
   * 显示成功消息
   * 
   * @param message 消息内容
   */
  public void showSuccess(String message) {
    showNotification(NotificationComponent.NotificationType.SUCCESS, message, 3000);
  }

  /**
   * 显示错误消息
   * 
   * @param message 消息内容
   */
  public void showError(String message) {
    showNotification(NotificationComponent.NotificationType.ERROR, message, 5000);
  }

  /**
   * 显示警告消息
   * 
   * @param message 消息内容
   */
  public void showWarning(String message) {
    showNotification(NotificationComponent.NotificationType.WARNING, message, 4000);
  }

  /**
   * 显示信息消息
   * 
   * @param message 消息内容
   */
  public void showInfo(String message) {
    showNotification(NotificationComponent.NotificationType.INFO, message, 3000);
  }

  /**
   * 显示通知
   * 
   * @param type          通知类型
   * @param message       通知消息
   * @param autoHideDelay 自动隐藏延迟时间（毫秒）
   */
  public void showNotification(NotificationComponent.NotificationType type, String message, int autoHideDelay) {
    if (message == null || message.trim().isEmpty()) {
      return;
    }

    this.currentType = type;
    this.currentMessage = message.trim();
    this.isVisible = true;

    // 通知所有监听器
    for (NotificationListener listener : listeners) {
      try {
        listener.showNotification(type, this.currentMessage, autoHideDelay);
      } catch (Exception e) {
        // 记录日志但不抛出异常，避免影响其他监听器
        System.err.println("通知监听器异常: " + e.getMessage());
      }
    }
  }

  /**
   * 隐藏通知
   */
  public void hideNotification() {
    this.isVisible = false;
    this.currentMessage = "";

    // 通知所有监听器
    for (NotificationListener listener : listeners) {
      try {
        listener.hideNotification();
      } catch (Exception e) {
        // 记录日志但不抛出异常，避免影响其他监听器
        System.err.println("通知监听器异常: " + e.getMessage());
      }
    }
  }

  /**
   * 根据异常显示错误消息
   * 
   * @param exception 异常对象
   */
  public void showError(Exception exception) {
    String message = "系统错误";
    if (exception != null) {
      String exceptionMessage = exception.getMessage();
      if (exceptionMessage != null && !exceptionMessage.trim().isEmpty()) {
        message = exceptionMessage;
      } else {
        message = "系统错误: " + exception.getClass().getSimpleName();
      }
    }
    showError(message);
  }

  /**
   * 显示操作结果通知
   * 
   * @param success        操作是否成功
   * @param successMessage 成功消息
   * @param errorMessage   错误消息
   */
  public void showOperationResult(boolean success, String successMessage, String errorMessage) {
    if (success) {
      showSuccess(successMessage != null ? successMessage : "操作成功");
    } else {
      showError(errorMessage != null ? errorMessage : "操作失败");
    }
  }

  /**
   * 检查通知是否正在显示
   * 
   * @return 如果通知正在显示则返回true
   */
  public boolean isVisible() {
    return isVisible;
  }

  /**
   * 获取当前消息
   * 
   * @return 当前消息
   */
  public String getCurrentMessage() {
    return currentMessage;
  }

  /**
   * 获取当前通知类型
   * 
   * @return 当前通知类型
   */
  public NotificationComponent.NotificationType getCurrentType() {
    return currentType;
  }

  /**
   * 清理资源
   */
  public void cleanup() {
    hideNotification();
    listeners.clear();
  }
}