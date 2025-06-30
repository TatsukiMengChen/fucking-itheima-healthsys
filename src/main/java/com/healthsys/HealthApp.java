package com.healthsys;

import com.formdev.flatlaf.FlatDarkLaf;
import com.healthsys.view.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * 健康管理系统主入口类
 * 负责应用程序的启动和初始化
 * 
 * @author AI健康管理系统开发团队
 */
public class HealthApp {

  private static final Logger logger = LoggerFactory.getLogger(HealthApp.class);

  /**
   * 应用程序主入口方法
   * 
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    try {
      // 设置系统属性
      System.setProperty("java.awt.headless", "false");

      // 设置FlatLaf外观
      setupLookAndFeel();

      // 设置默认字体（支持中文）
      setupFonts();

      // 在事件调度线程中启动GUI
      SwingUtilities.invokeLater(() -> {
        try {
          startApplication();
        } catch (Exception e) {
          logger.error("启动应用程序失败", e);
          showErrorMessage("应用程序启动失败: " + e.getMessage());
          System.exit(1);
        }
      });

    } catch (Exception e) {
      logger.error("初始化应用程序失败", e);
      showErrorMessage("应用程序初始化失败: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * 设置外观主题
   */
  private static void setupLookAndFeel() {
    try {
      // 使用FlatLaf深色主题
      UIManager.setLookAndFeel(new FlatDarkLaf());
      logger.info("成功设置FlatLaf深色主题");

      // 可选：设置其他UI属性
      UIManager.put("Button.arc", 5);
      UIManager.put("Component.arc", 5);
      UIManager.put("ProgressBar.arc", 5);
      UIManager.put("TextComponent.arc", 5);

    } catch (UnsupportedLookAndFeelException e) {
      logger.warn("设置FlatLaf主题失败，使用系统默认主题", e);
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ex) {
        logger.error("设置系统默认主题也失败", ex);
      }
    }
  }

  /**
   * 设置字体（确保中文显示正常）
   */
  private static void setupFonts() {
    try {
      // 设置全局字体
      java.awt.Font font = new java.awt.Font("微软雅黑", java.awt.Font.PLAIN, 12);

      // 应用到所有UI组件
      UIManager.put("Button.font", font);
      UIManager.put("Label.font", font);
      UIManager.put("TextField.font", font);
      UIManager.put("TextArea.font", font);
      UIManager.put("ComboBox.font", font);
      UIManager.put("Table.font", font);
      UIManager.put("TableHeader.font", font);
      UIManager.put("MenuItem.font", font);
      UIManager.put("Menu.font", font);
      UIManager.put("TabbedPane.font", font);
      UIManager.put("CheckBox.font", font);
      UIManager.put("RadioButton.font", font);

      logger.info("字体设置完成");
    } catch (Exception e) {
      logger.warn("设置字体失败", e);
    }
  }

  /**
   * 启动应用程序
   */
  private static void startApplication() {
    logger.info("正在启动健康管理系统...");

    // 创建并显示主窗口
    MainFrame mainFrame = new MainFrame();
    mainFrame.setVisible(true);

    logger.info("健康管理系统启动成功");
  }

  /**
   * 显示错误消息对话框
   * 
   * @param message 错误消息
   */
  private static void showErrorMessage(String message) {
    SwingUtilities.invokeLater(() -> {
      JOptionPane.showMessageDialog(
          null,
          message,
          "健康管理系统 - 错误",
          JOptionPane.ERROR_MESSAGE);
    });
  }
}